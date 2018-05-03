package konrad_wpam.drunkblock;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataToSetBlock
{
    private static DataToSetBlock instance;
    private String password;
    private String blockTill;
    private ArrayList<String> appsToBlockPkgNames = new ArrayList<String>();
    private String friend_number;
    private String host_number;
    private boolean blockSet = false;
    private Intent passServiceIntent;
    private Intent timeCheckIntent;

    private Thread checkingTimeBlock;


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
    public Thread getCheckingTimeBlock() {
        return checkingTimeBlock;
    }

    public void setCheckingTimeBlock(Thread checkingTimeBlock) {
        this.checkingTimeBlock = checkingTimeBlock;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBlockTill() {
        return blockTill;
    }

    public void setBlockTill(long timePicked) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        this.blockTill = (String) df.format(MainActivity.getContext().getString(R.string.date_format),new Date(Calendar.getInstance().getTimeInMillis() + timePicked));
    }

    public boolean isBlockSet() {
        return blockSet;
    }

    public void setBlockSet(boolean blockSet) {
        this.blockSet = blockSet;
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

    public Intent getPassServiceIntent() {
        return passServiceIntent;
    }

    public void setPassServiceIntent(Intent passServiceIntent) {
        this.passServiceIntent = passServiceIntent;
    }

    public Intent getTimeCheckIntent() {
        return timeCheckIntent;
    }

    public void setTimeCheckIntent(Intent timeCheckIntent) {
        this.timeCheckIntent = timeCheckIntent;
    }
}
