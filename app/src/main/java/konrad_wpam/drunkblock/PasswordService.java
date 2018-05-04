package konrad_wpam.drunkblock;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.telecom.TelecomManager;
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
 	import android.telecom.Call;

import static java.lang.Thread.sleep;

/**
 * Created by Konrad on 2018-03-23.
 */

public class PasswordService extends Service
{
    private String dialer = "com.android.dialer";
    private TelephonyManager telephony = (TelephonyManager) MainActivity.getContext().getSystemService(Context.TELEPHONY_SERVICE);
    private CallsReceiver cr;
    private DataToSetBlock dtsb;
    private Intent helperIntent = new Intent();
    private int timeForTopActivity = 1000 * 10; // 10 sekund
    private final Context context = this;
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
        //dtsb.setCallsReceiver(new CallsReceiver());
        if(dtsb.getDialerToBeBlocked()) {
            cr = new CallsReceiver();
            telephony.listen(cr, PhoneStateListener.LISTEN_CALL_STATE);
        }
        System.out.println("Password service ruszyl!");
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
                    System.out.println("AKTYWNE PAKIETY!!!!");
                   // System.out.println("przed1111!!!!" + " // " + cr.getPrev_state());
                    for (String activePackage : activePackages) {
                        if(!activePackage.equals(lastActivity))
                        {
                           // System.out.println("NOWA AKTYWNOSC! ==> " + activePackage + " / " + blockedAppsPkgNames.size());
                            System.out.println("NOWA AKTYWNOSC! ==> " + activePackage);
                            //if(lastActivity!= null && !lastActivity.equals("konrad_wpam.drunkblock") && blockedAppsPkgNames.contains(activePackage))
                            /*if(activePackage.equals(dialer) && !lastActivity.equals("konrad_wpam.drunkblock"))
                            {
                                System.out.println(" in dialer! ==> true");
                                cr.setInDialer(true);
                                if(telephony.getCallState() == TelephonyManager.CALL_STATE_IDLE && cr.getPrev_state() == -1)
                                {
                                    System.out.println("ZNALEZIONE DIALERY xxxxxxxx!!!!");
                                    helperIntent = new Intent(BlockedAppCallResolver.PASSWORD_WINDOW_ACT);
                                    helperIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    helperIntent.putExtra(getString(R.string.when_password_window), BlockedAppCallResolver.TRY_TO_UNLOCK_APP);
                                    startActivity(helperIntent);
                                }
                            }
                            else if(!activePackage.equals("konrad_wpam.drunkblock"))
                            {
                                System.out.println(" in dialer! ==> false");
                                cr.setInDialer(false);
                                cr.setPrev_state(-1);
                            }*/
                            if(lastActivity!= null && !lastActivity.equals("konrad_wpam.drunkblock") && dtsb.getAppsToBlockPkgNames().contains(activePackage))
                            {
                                /*if(activePackage.equals(dialer)) {
                                    if (telephony.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        if (telephony.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                                if (!getActivePackagesBeforeLolli()[0].equals(dialer) || telephony.getCallState() != TelephonyManager.CALL_STATE_IDLE) break;
                                            } else {
                                                if (!getActivePackagesAfterLolli()[0].equals(dialer) || telephony.getCallState() != TelephonyManager.CALL_STATE_IDLE) break;
                                            }
                                        }
                                        else
                                        {
                                            break;
                                        }
                                    }
                                    else
                                    {
                                        break;
                                    }

                                }*/

                                System.out.println("ZNALEZIONE DIALERY!!!!");
                                helperIntent = new Intent(BlockedAppCallResolver.PASSWORD_WINDOW_ACT);
                                helperIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                else
                {
                    System.out.println("NULL");
                }


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

