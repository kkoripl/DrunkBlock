package konrad_wpam.drunkblock;

import android.graphics.drawable.Drawable;

public class AppData {
    private String appLabel;
    private String appPkgName;
    private boolean locked = false;
    private Drawable icon;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    public boolean getIfLocked() {
        return locked;
    }

    public void setIfLocked(boolean locked) {
        this.locked = locked;
    }

    public String getAppPkgName() {
        return appPkgName;
    }

    public void setAppPkgName(String appPkgName) {
        this.appPkgName = appPkgName;
    }
}
