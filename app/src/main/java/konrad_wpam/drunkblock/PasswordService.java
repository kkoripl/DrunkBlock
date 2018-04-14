package konrad_wpam.drunkblock;

import android.app.ActivityManager;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

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

/**
 * Created by Konrad on 2018-03-23.
 */

public class PasswordService extends Service
{
    List<Intent> appsToBeBlocked;
    List<String> appsInstalled;
    Intent helperIntent = new Intent();
    Intent appToBeFound = new Intent(Intent.ACTION_DIAL);
    int timeForTopActivity = 1000 * 10;
    final Context context = this;
    String lastActivity;
    public PasswordService() {
        super();
        appsToBeBlocked = new ArrayList<Intent>();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        final List<String> blockedAppsPkgNames = queryAppPkgName(appToBeFound);
        appsInstalled = getInstalledAppsPkgNames();
        System.out.println("ZAINSTALOWANE APKI!!!!");
        for (String app: appsInstalled)
        {
            System.out.println(app);
        }
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
                            lastActivity = activePackage;
                            System.out.println("NOWA AKTYWNOSC! ==> " + lastActivity + " / " + blockedAppsPkgNames.size());
                            if(blockedAppsPkgNames.contains(lastActivity))
                            {
                                System.out.println("ZNALEZIONE DIALERY!!!!");
                                helperIntent = new Intent("drunkblocker.PASSWORD_WINDOW_AC");
                                //helperIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(helperIntent);
                            }
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
        }, 20000, 6000);  // every 6 seconds
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

    private void backToMainScreen()
    {
        Intent getToHomeScreen = new Intent(Intent.ACTION_MAIN);
        getToHomeScreen.addCategory(Intent.CATEGORY_HOME);
        getToHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(getToHomeScreen);
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
}

