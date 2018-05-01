package konrad_wpam.drunkblock;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DataToSetBlock
{
    private static DataToSetBlock instance;
    private String password;
    private float blockHours;
    private ArrayList<String> appsToBlockPkgNames = new ArrayList<String>();
    private String friend_number;
    private String host_number;


    private DataToSetBlock() {
    }

    public static DataToSetBlock getDataToBlockInstance()
    {
        if(instance == null) instance = new DataToSetBlock();
        return instance;
    }

    public void nullSettingsData()
    {
        password = null;
        blockHours = 0;
        friend_number = null;
        host_number = null;
    }

    public void addAppToBlockList(String appPackageName)
    {
        appsToBlockPkgNames.add(appPackageName);
    }

    public void setAppsToBlockPkgNames(ArrayList<String> appsToBlockPkgNames) {
        if(appsToBlockPkgNames == null)
        {
            this.appsToBlockPkgNames.clear();
        }
        else this.appsToBlockPkgNames= appsToBlockPkgNames;
    }

    public void addAppsToBlockList(ArrayList<String> packageNames)
    {
        appsToBlockPkgNames.addAll(packageNames);
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

    public String getFriend_number() {
        return friend_number;
    }

    public void setFriend_number(String friend_number) {
        this.friend_number = friend_number;
    }

    public String getHost_number() {
        return host_number;
    }

    public void setHost_number(String host_number) {
        this.host_number = host_number;
    }
}
