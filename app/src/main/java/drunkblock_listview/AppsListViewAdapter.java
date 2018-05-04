package drunkblock_listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import konrad_wpam.drunkblock.AppData;
import konrad_wpam.drunkblock.R;

public class AppsListViewAdapter extends BaseAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<AppData> mDataSet;

    public AppsListViewAdapter(Context context, ArrayList<AppData> apps)
    {
        mContext = context;
        mDataSet = apps;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        AppData appData = (AppData) getItem(position);
        convertView = mInflater.inflate(R.layout.app_on_list_view, parent, false);
        holder.appName = (TextView) convertView.findViewById(R.id.app_name);
        holder.ifAppLocked = (TextView) convertView.findViewById(R.id.locked_unlocked_text);
        holder.appThumbnail = (ImageView) convertView.findViewById(R.id.app_thumbnail);
        holder.padlock = (ImageView) convertView.findViewById(R.id.locker_thumbnail);
        holder.appName.setText(appData.getAppLabel());
        holder.appThumbnail.setImageDrawable(appData.getIcon());
        if(appData.getIfLocked())
        {
            holder.ifAppLocked.setText(R.string.locked);
            holder.padlock.setImageResource(R.drawable.locked_locker);
        }
        else
        {
            holder.ifAppLocked.setText(R.string.unlocked);
            holder.padlock.setImageResource(R.drawable.unlocked_locker);
        }
        convertView.setTag(holder);
        return convertView;
    }


    private static class ViewHolder
    {
        public TextView appName;
        public TextView ifAppLocked;
        public ImageView appThumbnail;
        public ImageView padlock;
    }

}
