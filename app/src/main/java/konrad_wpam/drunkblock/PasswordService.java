package konrad_wpam.drunkblock;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * Created by Konrad on 2018-03-23.
 */

/**
 * Serwis stawiajacy blokade
 */

public class PasswordService extends Service
{
    private TelephonyManager telephony = (TelephonyManager) MainActivity.getContext().getSystemService(Context.TELEPHONY_SERVICE);
    private CallsReceiver cr;
    private DataToSetBlock dtsb;
    private Intent helperIntent = new Intent();
    private int timeForTopActivity = 1000 * 5; // 5 sekund
    private String lastActivity;
    private Timer timer;
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
        if(dtsb.getDialerToBeBlocked()) {
            cr = new CallsReceiver();
            telephony.listen(cr, PhoneStateListener.LISTEN_CALL_STATE);
        }
       // System.out.println("Password service ruszyl!");
        timer  =  new Timer();
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
                    //System.out.println("AKTYWNE PAKIETY!!!!");
                    for (String activePackage : activePackages) {
                        if(!activePackage.equals(lastActivity))
                        {
                           // System.out.println("NOWA AKTYWNOSC! ==> " + activePackage);
                            if(lastActivity!= null && !lastActivity.equals(getString(R.string.this_app_package)) && dtsb.getAppsToBlockPkgNames().contains(activePackage))
                            {
                                //System.out.println("ZNALEZIONE DIALERY!!!!");
                                helperIntent = new Intent(getString(R.string.password_window_act));//new Intent(BlockedAppCallResolver.PASSWORD_WINDOW_ACT);
                                helperIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                helperIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                helperIntent.putExtra(getString(R.string.when_password_window), BlockedAppCallResolver.TRY_TO_UNLOCK_APP);
                                startActivity(helperIntent);
                            }
                            lastActivity = activePackage;
                        }
                        else {
                            System.out.println(activePackage);
                        }
                    }
                }
               /* else
                {
                    System.out.println("NULL");
                }*/


            }
        }, 100, 300);  // sprawdzam co 0.4 sekundy
      return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        timer.cancel();
        if(cr!=null) {
            telephony.listen(cr, PhoneStateListener.LISTEN_NONE);
            cr = null;
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.getContext());
        notificationManager.cancelAll();
        dtsb.setAppsToBlockPkgNames(null);
        dtsb.setPassword("");
        dtsb.setDialerToBeBlocked(false);
        dtsb.setBlockSet(false);
        Toast.makeText(this,R.string.blocking_apps_stopped,Toast.LENGTH_SHORT).show();
        super.onDestroy();
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
}

