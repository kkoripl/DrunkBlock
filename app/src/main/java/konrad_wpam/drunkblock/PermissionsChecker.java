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

import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.KILL_BACKGROUND_PROCESSES;
import static android.Manifest.permission.PACKAGE_USAGE_STATS;
import static android.Manifest.permission.PROCESS_OUTGOING_CALLS;
import static android.Manifest.permission.READ_PHONE_STATE;

public class PermissionsChecker implements PermissionsCheck{

    Activity activity;
    public PermissionsChecker(Activity activity)
    {
        this.activity=activity;
    }
    private static final String[] PERMISSIONS = new String[] {
            KILL_BACKGROUND_PROCESSES,
            CALL_PHONE,
            READ_PHONE_STATE,
            PROCESS_OUTGOING_CALLS
    };

    public void makeCheck()
    {
        boolean permissionsGiven1 = false;
        boolean permissionsGiven2 = false;
        if(!checkPermissions(PERMISSIONS))
        {
            showMessageOKCancel(MainActivity.getContext().getString(R.string.other_please),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            requestPerms();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            DataToSetBlock.getDataToBlockInstance().setPermissionsGiven1(false);
                        }
                    }
            );
        }
        else
        {
            DataToSetBlock.getDataToBlockInstance().setPermissionsGiven1(true);
        }
        if(checkIfPackageUsageStatsPermAdded())
        {
            if(!checkPackageUsageStatsPermGrant())
            {
                showMessageOKCancel(MainActivity.getContext().getString(R.string.usage_stats_please),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                activity.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                DataToSetBlock.getDataToBlockInstance().setPermissionsGiven2(false);
                                //activity.finish();
                               // MainActivity.getContext().startActivity(new Intent (MainActivity.getContext(),MainActivity.class));
                            }
                        });
            }
            else
            {
                DataToSetBlock.getDataToBlockInstance().setPermissionsGiven2(true);
            }
            //else Toast.makeText(activity, "Stats Permissions granted", Toast.LENGTH_LONG).show();
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
    public void requestPerms()
    {
        ActivityCompat.requestPermissions(activity, PERMISSIONS, MainActivity.PERMISSION_REQUEST_CODE);
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
    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener)
    {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", cancelListener)
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
