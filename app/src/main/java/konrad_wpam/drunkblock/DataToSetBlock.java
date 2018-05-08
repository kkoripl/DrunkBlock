package konrad_wpam.drunkblock;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Klasa przechowujaca informacje o zakladanej blokadzie - singleton
 */
public class DataToSetBlock
{
    private static DataToSetBlock instance;
    private String password=""; // haslo
    private String blockTill; // do kiedy blokada
    private ArrayList<AppData> allApps = new ArrayList<AppData>(); // wszystkie apki z telefonu
    private ArrayList<String> appsToBlockPkgNames = new ArrayList<String>(); // pakiety aplikacji do blokowania
    private String friend_number; // telefon kumpla
    private String host_number; // telefon gospodarza
    private boolean dialerToBeBlocked = false; // oznaczenie czy mamy blokowac aplikacje telefonu
    private boolean blockSet = false; // czy blok ustawiony
    private Intent passServiceIntent; // intent uruchamianego serwisu blokady
    private Intent timeCheckIntent; // intent uruchamianego serwisu sprawdzenia czy czas blokad juz nie minal
    private boolean permissionsGiven1 = false; // info czy uprawnienie na sprawdzenie telefonu zostalo nadane
    private boolean permissionsGiven2 = false; // info czy uprawnienie na Usage Stats zostalo nadane


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

    public boolean getDialerToBeBlocked() {
        return dialerToBeBlocked;
    }

    public void setDialerToBeBlocked(boolean dialerToBeBlocked) {
        this.dialerToBeBlocked = dialerToBeBlocked;
    }

    public ArrayList<AppData> getAllApps() {
        return allApps;
    }

    public void setAllApps(ArrayList<AppData> allApps) {
        this.allApps = allApps;
    }

    public boolean isPermissionsGiven1() {
        return permissionsGiven1;
    }

    public void setPermissionsGiven1(boolean permissionsGiven) {
        this.permissionsGiven1 = permissionsGiven;
    }

    public boolean isPermissionsGiven2() {
        return permissionsGiven2;
    }

    public void setPermissionsGiven2(boolean permissionsGiven) {
        this.permissionsGiven2 = permissionsGiven;
    }
}
