package konrad_wpam.drunkblock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * BlockTimeChecker - serwis sprawdzajacy czy czas blokad nie dobiegl juz konca
 */
public class BlockTimeChecker extends Service {

    private DataToSetBlock dtsb = DataToSetBlock.getDataToBlockInstance();
    private android.text.format.DateFormat df = new android.text.format.DateFormat();
    private Calendar calendar;
    private String current_time;
    private Context context = this;
    private Timer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                calendar = Calendar.getInstance();
                current_time = df.format(getString(R.string.date_format),calendar.getTime()).toString(); // pobieramy aktualny czas formatujac go
                if(dtsb.getBlockTill().equals(current_time)) // jesli zrownal sie z czasem blokady
                {
                    stopService(new Intent(context, PasswordService.class)); // zakoncz serwis blokad
                    dtsb.setBlockSet(false); // odznacz, ze blokady sie skonczyly
                    this.cancel(); // zakoncz swoje dzialanie
                }
            }
        }, 100, 1000); // sprawdzamy co sekunde
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        timer.cancel();
        super.onDestroy();
    }
}
