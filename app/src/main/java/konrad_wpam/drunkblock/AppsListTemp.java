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
        mListView = (ListView) findViewById(R.id.apps_list);
        dtsb.setAllApps(loadApps(dtsb.getAllApps()));
        adapter = new AppsListViewAdapter(this, dtsb.getAllApps());
        mListView.setAdapter(adapter);
        addOnListViewClickListener(mListView);
        addToSettingsButtonClickListener((Button) findViewById(R.id.lv_to_settings),this);
    }

    private void addOnListViewClickListener(final ListView mListView)
    {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ImageView padlock = (ImageView) view.findViewById(R.id.locker_thumbnail);
                TextView ifAppLocked = (TextView) view.findViewById(R.id.locked_unlocked_text);
                if(dtsb.getAllApps().get(position).getIfLocked())
                {
                    dtsb.getAllApps().get(position).setIfLocked(false);
                    ifAppLocked.setText(R.string.unlocked);
                    padlock.setImageResource(R.drawable.unlocked_locker);
                }
                else
                {
                    dtsb.getAllApps().get(position).setIfLocked(true);
                    ifAppLocked.setText(R.string.locked);
                    padlock.setImageResource(R.drawable.locked_locker);
                }
            }
        });
    }

    private void addToSettingsButtonClickListener(Button button, final Activity activity)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dtsb.setAppsToBlockPkgNames(null);
                ArrayList<String> appsBlocked = getAppsToBlock(dtsb.getAllApps());
                if(appsBlocked.size()>1 || dtsb.getDialerToBeBlocked())
                {
                    dtsb.setAppsToBlockPkgNames(appsBlocked);
                    startActivity(new Intent(activity, Settings.class));
                }
                else
                {
                    Toast.makeText(thisContext,R.string.choose_apps_to_block,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<AppData> loadApps( ArrayList<AppData> allApps)
    {
        boolean contains = false;
        PackageManager manager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN,null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i,0);
        for( ResolveInfo info: availableActivities)
        {
            if(allApps.size()!=0) {
                contains = false;
                for (AppData app : allApps) {
                    if (app.getAppPkgName().equals(info.activityInfo.packageName)) {
                        contains = true;
                        break;
                    }
                }
            }
            if(((allApps.size()!=0 && contains == false) || allApps.size()==0) && !info.activityInfo.packageName.equals(getString(R.string.this_app_package)))
            {
                if(info.activityInfo.packageName.contains("clock")) clockPkg = info.activityInfo.packageName;
                else
                {
                    AppData newApp = new AppData();
                    newApp.setAppLabel((String) info.loadLabel(manager));
                    newApp.setAppPkgName(info.activityInfo.packageName);
                    newApp.setIcon(info.activityInfo.loadIcon(manager));
                    allApps.add(newApp);
                }
            }
        }
        return allApps;

    }

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
        appsToBlock.add(clockPkg);
        return appsToBlock;
    }

    private String getDefaultDialerPkg()
    {
        TelecomManager manger = (TelecomManager) getSystemService(TELECOM_SERVICE);
        return manger.getDefaultDialerPackage();
    }

}
