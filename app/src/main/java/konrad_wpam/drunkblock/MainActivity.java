package konrad_wpam.drunkblock;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.KILL_BACKGROUND_PROCESSES;
import static android.Manifest.permission.PACKAGE_USAGE_STATS;


public class MainActivity extends AppCompatActivity {

    private final int PERMISSION_REQUEST_CODE = 200;
    String msg = "Android : ";
    Button b1;
    int a = 0;
    private static final String[] PERMISSIONS = new String[] {
            KILL_BACKGROUND_PROCESSES
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // PermissionRequestActivity.start(this, REQUEST_CODE, MUST_PERMISSIONS, "Potrzebne uprawnienia", "Serio potrzebne");
        if(!checkPermission(PERMISSIONS))
        {
            showMessageOKCancel("Potrzebujemy uprawnien",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            requestPerms(PERMISSIONS,PERMISSION_REQUEST_CODE);
                        }
                    });
        }
        if(checkIfPackageUsageStatsPermAdded())
        {
            if(!checkPackageUsageStatsPermGrant())
            {
                showMessageOKCancel("Potrzebujemy nadania uprawnien do przegladania statystyk uzywalnosci aplikacji" +
                                "\nby moc sprawdzac zalozone przez Ciebie blokady\n"
                                +"przejdz do ustawien (OK) nadaj je, pozwalajac aplikacji dzialac.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                            }
                        });
            }
            else Toast.makeText(this, "Permissions stats granted", Toast.LENGTH_LONG).show();
        }


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
                            showMessageOKCancel("Potrzebujemy wszystkich uprawnien",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            requestPerms(PERMISSIONS,PERMISSION_REQUEST_CODE);
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
        if(a==0) {
           /* Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
            Intent i = new Intent(this, PasswordService.class);
            startService(i);*/
           startService(new Intent(this, PasswordService.class));
            a=1;
            Log.d(msg,"Serwis ruszyl!!!");
        }
        Log.d(msg,"ON Start");
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        //Intent passwordIntent = new Intent(this,PasswordService.class);
        //this.startService(passwordIntent);
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

    private void requestPerms(String[] permissions, int permission_request_code)
    {
        ActivityCompat.requestPermissions(this, permissions, permission_request_code);
    }

    private boolean checkPackageUsageStatsPermGrant()
    {
        boolean granted = false;
        AppOpsManager appOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), this.getPackageName());
        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (this.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }
        return granted;
    }

    private boolean checkIfPackageUsageStatsPermAdded()
    {
        boolean result = false;
        try
        {
           PackageManager pm = getPackageManager();
           PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
           String[] requestedPermissions = null;
           if( pi != null)
           {
               requestedPermissions = pi.requestedPermissions;
           }
           if( requestedPermissions != null && requestedPermissions.length > 0)
           {
               List<String> requestedPermsList = Arrays.asList(requestedPermissions);
               result = requestedPermsList.contains(PACKAGE_USAGE_STATS);
           }
           return result;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
