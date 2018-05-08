package konrad_wpam.drunkblock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import drunkblock_listview.AppsListViewAdapter;

/**
 * AppsListTemp - okno wyboru aplikacji do blokowania
 */
public class AppsListTemp extends Activity
{
    private Context thisContext = this;
    private ListView mListView;
    private AppsListViewAdapter adapter;
    private DataToSetBlock dtsb = DataToSetBlock.getDataToBlockInstance();
    private String clockPkg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps_list);
        mListView = (ListView) findViewById(R.id.apps_list); // pobierz widok
        dtsb.setAllApps(loadApps(dtsb.getAllApps())); // zainstalowane aplikacje
        adapter = new AppsListViewAdapter(this, dtsb.getAllApps()); // stworz adapter, czyli dzialania ukryte pod widokiem
        mListView.setAdapter(adapter); // nastaw adpater
        addOnListViewClickListener(mListView);
        addToSettingsButtonClickListener((Button) findViewById(R.id.lv_to_settings),this);
    }

    // Co ma sie dziac jesli wcisniemy panel listy?
    private void addOnListViewClickListener(final ListView mListView)
    {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ImageView padlock = (ImageView) view.findViewById(R.id.locker_thumbnail);
                TextView ifAppLocked = (TextView) view.findViewById(R.id.locked_unlocked_text);
                if(dtsb.getAllApps().get(position).getIfLocked())
                {
                    dtsb.getAllApps().get(position).setIfLocked(false); // informujemy, ze apka ma nie byc blokowana
                    ifAppLocked.setText(R.string.unlocked);
                    padlock.setImageResource(R.drawable.unlocked_locker); // nastawiamy obrazek
                }
                else
                {
                    dtsb.getAllApps().get(position).setIfLocked(true); // informujemy, ze apka ma byc blokowana
                    ifAppLocked.setText(R.string.locked);
                    padlock.setImageResource(R.drawable.locked_locker);
                }
            }
        });
    }

    // Co ma sie dziac jesli zechcemy przejsc do ustawien blokady
    private void addToSettingsButtonClickListener(Button button, final Activity activity)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dtsb.setAppsToBlockPkgNames(null); // zerujemy liste aplikacji do blokowania
                ArrayList<String> appsBlocked = getAppsToBlock(dtsb.getAllApps()); // pobieramy nowa
                if(appsBlocked.size()>0 || dtsb.getDialerToBeBlocked()) // jesli jsst wieksza od 0 lub wybralismy do blokowania telefon - nie ma go na liscie
                {
                    dtsb.setAppsToBlockPkgNames(appsBlocked); // wszystko gra nastaw apki do blokowania
                    startActivity(new Intent(activity, Settings.class)); // przejdz do ustawien
                }
                else
                {
                    Toast.makeText(thisContext,R.string.choose_apps_to_block,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Metoda pobierajaca zainstalowane aplikacje
    private ArrayList<AppData> loadApps( ArrayList<AppData> allApps)
    {
        boolean contains = false;
        PackageManager manager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN,null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i,0);
        for( ResolveInfo info: availableActivities)
        {
            if(allApps.size()!=0) { // jesli lista aplikacji juz byla zainicjalizowana to sprawdzmy czy cos nie przybylo
                contains = false;
                for (AppData app : allApps) {
                    if (app.getAppPkgName().equals(info.activityInfo.packageName) && app.getAppLabel().equals((String) info.loadLabel(manager))) {
                        contains = true;
                        break;
                    }
                }
            }
            // jesli zainicjowana i apki tam nie ma - dodaj
            // jesli lista niezainicjowana - dodaj
            // wszystko o ile nie dodajemy naszej aplikacji
            if(((allApps.size()!=0 && contains == false) || allApps.size()==0) && !info.activityInfo.packageName.equals(getString(R.string.this_app_package)))
            {
                AppData newApp = new AppData();
                newApp.setAppLabel((String) info.loadLabel(manager));
                newApp.setAppPkgName(info.activityInfo.packageName);
                newApp.setIcon(info.activityInfo.loadIcon(manager));
                allApps.add(newApp);
            }
        }
        return allApps;

    }

    // Metoda nastawiajaca liste aplikacji do blokowania
    // Jesli znajdzie sie na niej telefon to nie dodajemy go do listy, a odznaczamy odpowiednie pole
    private ArrayList<String> getAppsToBlock(ArrayList<AppData> apps)
    {
        dtsb.setDialerToBeBlocked(false);
        ArrayList<String> appsToBlock = new ArrayList<String>();
        for (AppData app : apps)
        {
            if(app.getIfLocked())
            {
                if(app.getAppPkgName().equals(getDefaultDialerPkg()))
                {
                    dtsb.setDialerToBeBlocked(true);
                }
                else appsToBlock.add(app.getAppPkgName());
            }
        }
        return appsToBlock;
    }

    // Metoda pobierajaca nazwe domyslnej aplikacji do dzwonienia
    private String getDefaultDialerPkg()
    {
        TelecomManager manger = (TelecomManager) getSystemService(TELECOM_SERVICE);
        return manger.getDefaultDialerPackage();
    }

}
