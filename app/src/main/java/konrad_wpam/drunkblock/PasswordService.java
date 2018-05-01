package konrad_wpam.drunkblock;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import static android.content.Intent.makeMainSelectorActivity;
import static android.content.pm.PackageManager.MATCH_DEFAULT_ONLY;

/**
 * Created by Konrad on 2018-03-23.
 */

public class PasswordService extends Service
{
    private DataToSetBlock dtsb;
    private Intent helperIntent = new Intent();
    private int timeForTopActivity = 1000 * 10;
    private final Context context = this;
    private String lastActivity;
    public PasswordService() {
        super();
        dtsb = DataToSetBlock.getDataToBlockInstance();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
       System.out.println("Password service ruszyl!");
        Timer timer  =  new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run()
            {
                String[] activePackages;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    activePackages = getActivePackagesBeforeLolli();
                }
                else
                {
                    activePackages = getActivePackagesAfterLolli();
                }

                if(activePackages != null) {
                    System.out.println("AKTYWNE PAKIETY!!!!");
                    for (String activePackage : activePackages) {
                        if(!activePackage.equals(lastActivity))
                        {
                           // System.out.println("NOWA AKTYWNOSC! ==> " + activePackage + " / " + blockedAppsPkgNames.size());
                            System.out.println("NOWA AKTYWNOSC! ==> " + activePackage);
                            //if(lastActivity!= null && !lastActivity.equals("konrad_wpam.drunkblock") && blockedAppsPkgNames.contains(activePackage))
                            if(lastActivity!= null && !lastActivity.equals("konrad_wpam.drunkblock") && dtsb.getAppsToBlockPkgNames().contains(activePackage))
                            {
                                System.out.println("ZNALEZIONE DIALERY!!!!");
                                helperIntent = new Intent("drunkblocker.PASSWORD_WINDOW_AC");
                                helperIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(helperIntent);
                            }
                            lastActivity = activePackage;
                        }
                        else {
                            System.out.println(activePackage);
                        }
                    }
                }
                else
                {
                    System.out.println("NULL");
                }


            }
        }, 20000, 1000);  // every 1 seconds
      return START_STICKY;
    }

    private String[] getActivePackagesBeforeLolli()
    {
        final Set<String> activePackages = new HashSet<String>();
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info :  processInfos)
        {
            if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            {
                activePackages.addAll(Arrays.asList(info.pkgList));
            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }

    private String[] getActivePackagesAfterLolli()
    {
        String[] latestActivePackage = new String[1];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - timeForTopActivity, time);
            if(stats != null)
            {
                SortedMap<Long, UsageStats> sortedStats = new TreeMap<Long, UsageStats>();
                for(UsageStats us : stats)
                {
                    sortedStats.put(us.getLastTimeUsed(),us);
                }
                if( sortedStats != null && !sortedStats.isEmpty())
                {
                    latestActivePackage[0] = sortedStats.get(sortedStats.lastKey()).getPackageName();
                    return latestActivePackage;
                }
            }
        }
        return null;
    }

    private List<String> queryAppPkgName(Intent appIntent)
    {
        List<String> appPkgNames = new ArrayList<>();
        List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(appIntent, 0);
        for(ResolveInfo app : apps)
        {
            ActivityInfo ai = app.activityInfo;
            appPkgNames.add(ai.applicationInfo.packageName);
        }
        return appPkgNames;
    }

    private List<String> queryBlockedAppsPkgName(List<Intent> blockedApps)
    {
        List<String> blockedAppsPkgNames = new ArrayList<String>();
        for(int i = 0; i<blockedApps.size();i++)
        {
            blockedAppsPkgNames.addAll(blockedAppsPkgNames.size(),queryAppPkgName(blockedApps.get(i)));
        }
        return blockedAppsPkgNames;
    }

    private List<String> getInstalledAppsPkgNames()
    {
        List<String> appsPkgNames = new ArrayList<String>();
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> ai = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo info : ai)
        {
            appsPkgNames.add(info.packageName);
        }
        return appsPkgNames;
    }

    private List<String> getSms()
    {

        List<String> appsPkgNames = new ArrayList<String>();
        final PackageManager pm = getPackageManager();
        List<PackageInfo> ai = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ai = pm.getPackagesHoldingPermissions(new String[]{"android.permission.SEND_SMS"}, PackageManager.MATCH_SYSTEM_ONLY);
            for (PackageInfo info : ai) {
                appsPkgNames.add(info.packageName);
            }
        }
        return appsPkgNames;
    }

    private List<String> getPhone()
    {
        List<String> appsPkgNames = new ArrayList<String>();
        final PackageManager pm = getPackageManager();
        List<PackageInfo> ai = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ai = pm.getPackagesHoldingPermissions(new String[]{"android.permission.CALL_PHONE"}, PackageManager.GET_META_DATA);
        }
        for (PackageInfo info : ai)
        {
            appsPkgNames.add(info.packageName);
        }
        return appsPkgNames;
    }
}

