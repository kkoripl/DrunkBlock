package konrad_wpam.drunkblock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import drunkblock_listview.AppsListViewAdapter;

public class AppsListTemp extends Activity
{
    private Context thisContext = this;
    private ListView mListView;
    private ArrayList<AppData> apps;
    private DataToSetBlock dtsb = DataToSetBlock.getDataToBlockInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps_list);

        mListView = (ListView) findViewById(R.id.apps_list);
        apps = loadApps();
        AppsListViewAdapter adapter = new AppsListViewAdapter(this, apps);
        mListView.setAdapter(adapter);
        addOnListViewClickListener(mListView);
        addToSettingsButtonClickListener((Button) findViewById(R.id.lv_to_settings),this);
    }

    private void addOnListViewClickListener(ListView mListView)
    {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(apps.get(position).getIfLocked()) apps.get(position).setIfLocked(false);
                    else apps.get(position).setIfLocked(true);
                    parent.getAdapter().getView(position,view,parent);
            }
        });
    }

    private void addToSettingsButtonClickListener(Button button, final Activity activity)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dtsb.setAppsToBlockPkgNames(null);
                if(getAppsToBlock(apps).size()!=0)
                {
                    dtsb.addAppsToBlockList(getAppsToBlock(apps));
                    startActivity(new Intent(activity, Settings.class));
                }
                else
                {
                    Toast.makeText(thisContext,"Choose apps to be blocked before going further.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<AppData> loadApps()
    {
        PackageManager manager = getPackageManager();
        ArrayList<AppData> apps = new ArrayList<AppData>();
        Intent i = new Intent(Intent.ACTION_MAIN,null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i,0);
        for( ResolveInfo info: availableActivities)
        {
            AppData newApp = new AppData();
            newApp.setAppLabel((String) info.loadLabel(manager));
            newApp.setAppPkgName(info.activityInfo.packageName);
            newApp.setIcon(info.activityInfo.loadIcon(manager));
            apps.add(newApp);
        }
        return apps;

    }

    private ArrayList<String> getAppsToBlock(ArrayList<AppData> apps)
    {
        ArrayList<String> appsToBlock = new ArrayList<String>();
        for (AppData app : apps)
        {
            System.out.println(app.getAppPkgName() + " // " + app.getIfLocked());
            if(app.getIfLocked())
            {
                appsToBlock.add(app.getAppPkgName());
            }
        }
        System.out.println(appsToBlock.size());
        return appsToBlock;
    }

}
