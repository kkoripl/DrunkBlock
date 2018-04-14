package konrad_wpam.drunkblock;

import java.util.ArrayList;
import java.util.List;

public class DataToSetBlock
{
    private String password;
    private float blockHours;
    private List<String> appsToBlockPkgNames;

    public DataToSetBlock()
    {
        appsToBlockPkgNames = new ArrayList<String>();
    }

    public void addAppToBlockList(String appPackageName)
    {
        appsToBlockPkgNames.add(appPackageName);
    }

    // GETTERS & SETTERS
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public float getBlockHours() {
        return blockHours;
    }

    public void setBlockHours(float blockHours) {
        this.blockHours = blockHours;
    }

    public List<String> getAppsToBlockPkgNames() {
        return appsToBlockPkgNames;
    }

    public void setAppsToBlockPkgNames(List<String> appsToBlockPkgNames) {
        this.appsToBlockPkgNames = appsToBlockPkgNames;
    }
}
