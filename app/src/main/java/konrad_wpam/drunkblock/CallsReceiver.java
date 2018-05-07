package konrad_wpam.drunkblock;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

public class CallsReceiver extends PhoneStateListener
{
    private String prev_number;
    private int prev_state = TelephonyManager.CALL_STATE_IDLE;
    private DataToSetBlock dtsb = DataToSetBlock.getDataToBlockInstance();

    @Override
    public void onCallStateChanged(int state, String incomingNumber)
    {

       // prev_state = state;
        ;
        //System.out.println(state);
            /*
            -1 - po to by informowac, ze wylezlismy z dialera
             0 - idle
             1 - ringing
             2 - offhook
             */
        //POLACZENIE WYCHODZACE: IDLE - OFFHOOK - IDLE
        //         PRZYCHODZACE: IDLE - RINGING - OFFHOOK - IDLE
        if(prev_state != state)
        {
            switch(state)
            {
                case TelephonyManager.CALL_STATE_IDLE:
                {
                    prev_state=state;
                }
                break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                {
                   if(!(prev_state == TelephonyManager.CALL_STATE_RINGING ||
                           (prev_state== TelephonyManager.CALL_STATE_IDLE &&
                                   (incomingNumber.equals(dtsb.getFriend_number()) || incomingNumber.equals(dtsb.getHost_number())))))
                   {
                       killCall(MainActivity.getContext());
                   }
                   prev_state = state;
                }
                break;

                case TelephonyManager.CALL_STATE_RINGING:
                {
                    prev_state = state;
                }
                break;
            }
            prev_number = incomingNumber;
        }
    }

    public boolean killCall(Context context) {
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            return false;
        }
        return true;
    }

    public int getPrev_state() {
        return prev_state;
    }

    public void setPrev_state(int prev_state) {
        this.prev_state = prev_state;
    }

    public String getPrev_number() {
        return prev_number;
    }

    public void setPrev_number(String prev_number) {
        this.prev_number = prev_number;
    }
}
