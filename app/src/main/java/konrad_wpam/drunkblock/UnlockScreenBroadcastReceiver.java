package konrad_wpam.drunkblock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Konrad on 2018-03-23.
 */

public class UnlockScreenBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        System.out.println(intent.getAction());
    }
}

