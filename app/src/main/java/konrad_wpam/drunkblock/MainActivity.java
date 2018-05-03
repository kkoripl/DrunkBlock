package konrad_wpam.drunkblock;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.KILL_BACKGROUND_PROCESSES;
import static android.Manifest.permission.PACKAGE_USAGE_STATS;


public class MainActivity extends AppCompatActivity {

    private static Context thisContext;
    private DataToSetBlock dtsb = DataToSetBlock.getDataToBlockInstance();
    private static final int PERMISSION_REQUEST_CODE = 200;
    private String msg = "Android : ";
    private ListView mListView;
    private static final String[] PERMISSIONS = new String[] {
            KILL_BACKGROUND_PROCESSES,
            CALL_PHONE
    };
    private PermissionsChecker permsChecker = new PermissionsChecker(this);
    private Button ChooseApps_StopService;

    public static Context getContext()
    {
        return thisContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thisContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permsChecker.makeCheck();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
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
                if(ifGranted) Toast.makeText(this, "Permissions granted", Toast.LENGTH_LONG).show();
                else
                {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                    for( String permission : permissions)
                    {
                        if(shouldShowRequestPermissionRationale(permission))
                        {
                            permsChecker.showMessageOKCancel("Potrzebujemy wszystkich uprawnien",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            permsChecker.requestPerms(PERMISSIONS,PERMISSION_REQUEST_CODE);
                                        }
                                    });
                        }
                    }

                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ChooseApps_StopService = (Button) findViewById(R.id.to_choose_apps);
        if(!dtsb.isBlockSet())
        {
            ChooseApps_StopService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(thisContext, AppsListTemp.class));
                }
            });

        }
        else
        {
            ChooseApps_StopService.setText(R.string.stop_locking_apps);
            ChooseApps_StopService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent helperIntent = new Intent(BlockedAppCallResolver.PASSWORD_WINDOW_ACT);
                    helperIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    helperIntent.putExtra(String.valueOf(R.string.when_password_window),BlockedAppCallResolver.STOP_LOCKING);
                    startActivity(helperIntent);
                }
            });
        }
        Log.d(msg,"ON Start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChooseApps_StopService = (Button) findViewById(R.id.to_choose_apps);
        ChooseApps_StopService.invalidate();
        if(!dtsb.isBlockSet())
        {
            ChooseApps_StopService.setText(R.string.choose_apps);
            ChooseApps_StopService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(thisContext, AppsListTemp.class));
                }
            });
        }
        else
        {
            ChooseApps_StopService.setText(R.string.stop_locking_apps);
            ChooseApps_StopService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent helperIntent = new Intent(BlockedAppCallResolver.PASSWORD_WINDOW_ACT);
                    helperIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    helperIntent.putExtra(String.valueOf(R.string.when_password_window),BlockedAppCallResolver.STOP_LOCKING);
                    startActivity(helperIntent);
                }
            });

        }
        Log.d(msg,"ON RESUME");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(msg,"ON Pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
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




}
