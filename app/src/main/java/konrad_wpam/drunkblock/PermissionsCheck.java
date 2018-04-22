package konrad_wpam.drunkblock;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

public interface PermissionsCheck {
    final int PERMISSION_REQUEST_CODE = 200;
    Activity activity = null;
    String[] retrievePermsFromManifest();
    void requestPerms(String[] permissions, int permission_request_code);
    boolean checkPermissions(String[] permissions);
    void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener);

}
