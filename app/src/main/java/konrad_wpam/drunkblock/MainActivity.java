package konrad_wpam.drunkblock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.KILL_BACKGROUND_PROCESSES;
import static android.Manifest.permission.PROCESS_OUTGOING_CALLS;
import static android.Manifest.permission.READ_PHONE_STATE;


public class MainActivity extends AppCompatActivity {

    private static Context thisContext;
    private DataToSetBlock dtsb = DataToSetBlock.getDataToBlockInstance();
    public static final int PERMISSION_REQUEST_CODE = 200;
    private String msg = "Android : ";
    private Intent stopLockIntent;
   /* private static final String[] PERMISSIONS = new String[] {
            KILL_BACKGROUND_PROCESSES,
            CALL_PHONE,
            READ_PHONE_STATE,
            PROCESS_OUTGOING_CALLS
    };*/
    private PermissionsChecker permsChecker = new PermissionsChecker(this);
    private Button ChooseApps_StopService;
    private boolean permsChecked = false;

    public static Context getContext()
    {
        return thisContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thisContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (stopLockIntent == null) stopLockIntent = createStopLockIntent(stopLockIntent);
        if(!permsChecked)
        {
            permsChecker.makeCheck();
            permsChecked = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        permsChecked = true;
        boolean ifGranted = true;
        if(requestCode == PERMISSION_REQUEST_CODE)
        {
            if(grantResults.length>0)
            {
                for( int result : grantResults)
                {
                    if(result!=PackageManager.PERMISSION_GRANTED)
                    {
                        ifGranted = false;
                        break;
                    }
                }
                if(ifGranted)
                {
                    whichButtonToCreate();
                   /* if(dtsb.isPermissionsGiven1() && dtsb.isPermissionsGiven2())
                    {
                        if (!dtsb.isBlockSet()) {
                            makeChooseAppsButton();
                        } else {
                            if(!dtsb.getPassword().equals("")) {
                                makeStopLockingButton();
                            }
                            else
                            {
                                makeCantStopButton();
                            }
                        }
                    }*/
                }
                else
                {
                    makePermissionsNeededButton();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!permsChecked)
        {
            permsChecker.makeCheck();
            permsChecked = true;
        }
        whichButtonToCreate();
        /*ChooseApps_StopService = (Button) findViewById(R.id.to_choose_apps);
        ChooseApps_StopService.invalidate();
            if(!(dtsb.isPermissionsGiven1() && dtsb.isPermissionsGiven2()))
            {
                makePermissionsNeededButton();
            }
            else if (!dtsb.isBlockSet()) {
                makeChooseAppsButton();
            } else {
                if(!dtsb.getPassword().equals("")) {
                    makeStopLockingButton();
                }
                else
                {
                    makeCantStopButton();
                }
            }*/
        Log.d(msg,"ON Start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!permsChecked)
        {
            permsChecker.makeCheck();
            permsChecked = true;
        }

        whichButtonToCreate();
       /* ChooseApps_StopService = (Button) findViewById(R.id.to_choose_apps);
        ChooseApps_StopService.invalidate();
        if(!(dtsb.isPermissionsGiven1() && dtsb.isPermissionsGiven2()))
        {
           makePermissionsNeededButton();
        }
        else if (!dtsb.isBlockSet())
            {
                makeChooseAppsButton();
            } else {
                if(!dtsb.getPassword().equals("")) {
                    makeStopLockingButton();
                }
                else
                {
                    makeCantStopButton();
                }
            }*/
        Log.d(msg,"ON RESUME");
    }

    @Override
    protected void onPause() {
        super.onPause();
        permsChecked = false;
        Log.d(msg,"ON Pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        permsChecked = false;
        Log.d(msg,"ON Stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(msg,"ON Destroy");
    }

    private boolean checkPermission(String[] permissions)
    {
        for (String permission : permissions)
        {
            if(ContextCompat.checkSelfPermission(getApplicationContext(),permission) == PackageManager.PERMISSION_DENIED)
            {
                return false;
            }
        }
        return true;
    }

    private Intent createStopLockIntent(Intent intent)
    {
        intent = new Intent(getString(R.string.password_window_act)); //new Intent(BlockedAppCallResolver.PASSWORD_WINDOW_ACT);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(String.valueOf(R.string.when_password_window),BlockedAppCallResolver.STOP_LOCKING);
        return intent;
    }

    private void whichButtonToCreate()
    {
        ChooseApps_StopService = (Button) findViewById(R.id.to_choose_apps);
        ChooseApps_StopService.invalidate();
        if(!(dtsb.isPermissionsGiven1() && dtsb.isPermissionsGiven2()))
        {
            makePermissionsNeededButton();
        }
        else if (!dtsb.isBlockSet())
        {
            makeChooseAppsButton();
        }
        else if(!dtsb.getPassword().equals(""))
        {
            makeStopLockingButton();
        }
        else
        {
            makeCantStopButton();
        }
    }

    private void makeChooseAppsButton()
    {
        ChooseApps_StopService.setEnabled(true);
        ChooseApps_StopService.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        ChooseApps_StopService.setTextColor(getResources().getColor(R.color.colorWhite));
        ChooseApps_StopService.setText(R.string.choose_apps);
        ChooseApps_StopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(thisContext, AppsListTemp.class));
            }
        });
    }

    private void makeStopLockingButton()
    {
        ChooseApps_StopService.setEnabled(true);
        ChooseApps_StopService.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        ChooseApps_StopService.setTextColor(getResources().getColor(R.color.colorWhite));
        ChooseApps_StopService.setText(R.string.stop_locking_apps);
        ChooseApps_StopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(stopLockIntent);
            }});
    }

    private void makeCantStopButton()
    {
        ChooseApps_StopService.setText(R.string.cant_stop_locking);
        ChooseApps_StopService.setEnabled(false);
    }

    private void makePermissionsNeededButton()
    {
        ChooseApps_StopService.setText(R.string.we_need_permissions);
        ChooseApps_StopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permsChecker.makeCheck();
            }
        });
    }

}
