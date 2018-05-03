package konrad_wpam.drunkblock;

import android.app.Activity;
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
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.KILL_BACKGROUND_PROCESSES;
import static android.Manifest.permission.PACKAGE_USAGE_STATS;

public class PermissionsChecker implements PermissionsCheck{

    Activity activity;
    public PermissionsChecker(Activity activity)
    {
        this.activity=activity;
    }
    private static final String[] PERMISSIONS = new String[] {
            KILL_BACKGROUND_PROCESSES,
            CALL_PHONE
    };

    public void makeCheck()
    {
        if(!checkPermissions(PERMISSIONS))
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
                                activity.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                            }
                        });
            }
            else Toast.makeText(activity, "Permissions granted", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public String[] retrievePermsFromManifest() {
        try
        {
            PackageManager pm = activity.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = null;
            if( pi != null)
            {
                requestedPermissions = pi.requestedPermissions;
            }
            return requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void requestPerms(String[] permissions, int permission_request_code)
    {
        ActivityCompat.requestPermissions(activity, permissions, permission_request_code);
    }

    @Override
    public boolean checkPermissions(String[] permissions)
    {
        for (String permission : permissions)
        {
            if(ContextCompat.checkSelfPermission(activity,permission) == PackageManager.PERMISSION_DENIED)
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener)
    {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean checkPackageUsageStatsPermGrant()
    {
        boolean granted = false;
        AppOpsManager appOps = (AppOpsManager) activity.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), activity.getPackageName());
        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (activity.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
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
            PackageManager pm = activity.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
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
}
