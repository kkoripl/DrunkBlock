package konrad_wpam.drunkblock;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends Activity
{
    private static String NOTIF_CHANNEL_ID = "db_notfs";
    private static int FRIEND_NOTF_ID = 500;
    private static int HOST_NOTIF_ID = 501;
    private DataToSetBlock dtsb = DataToSetBlock.getDataToBlockInstance();
    private Context thisContext = this;
    private Button block_button;
    private Spinner block_hours;
    private Switch pass_off_on;
    private EditText password, host_number, friend_number;
    private boolean isNotfChannelSet = false;
    private String dateFormat = "hh:mm dd/MM/yyyy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_settings);
        initViewObjects();
    }

    private void initViewObjects()
    {
        block_hours = (Spinner) findViewById(R.id.block_duration);
        pass_off_on = (Switch) findViewById(R.id.password_switch);
        password = (EditText) findViewById(R.id.set_password_field);
        host_number = (EditText) findViewById(R.id.host_number_field);
        friend_number = (EditText) findViewById(R.id.friends_number_field);
        block_button = (Button) findViewById(R.id.block_button);
        addPassOffOnListener();
        addBlockButtonListener();
    }

    private void getBlockData(DataToSetBlock dtsb)
    {
        dtsb.nullSettingsData();
        dtsb.setBlockTill(Long.parseLong(getResources().getStringArray(R.array.hours_spinner_data)[block_hours.getSelectedItemPosition()]));
        if(!host_number.getText().toString().equals(null))
        {
            dtsb.setHost_number(host_number.getText().toString());
        }
        if(!friend_number.getText().toString().equals(null))
        {
            dtsb.setFriend_number(friend_number.getText().toString());
        }
    }

    private void addPassOffOnListener()
    {
        pass_off_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    password.setEnabled(true);
                    password.setFocusable(true);
                    password.setFocusableInTouchMode(true);
                }
                else
                {
                    password.setText(null);
                    password.setEnabled(false);
                    password.setFocusable(false);
                    password.setFocusableInTouchMode(false);
                }
            }
        });
    }

    private void addBlockButtonListener()
    {
        block_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getBlockData(dtsb);
                if((pass_off_on.isChecked() && !password.getText().toString().equals(null)) || !pass_off_on.isChecked())
                {
                    dtsb.setPassword(password.getText().toString());
                    dtsb.setBlockSet(true);
                    dtsb.setPassServiceIntent(new Intent(thisContext, PasswordService.class));
                    dtsb.setTimeCheckIntent(new Intent(thisContext,BlockTimeChecker.class));
                    if(makeNotificationChannel(NOTIF_CHANNEL_ID))
                    {
                        if(dtsb.getHost_number().length()!=0) makeNotifications(NOTIF_CHANNEL_ID,getString(R.string.host));
                        if(dtsb.getFriend_number().length()!=0) makeNotifications(NOTIF_CHANNEL_ID,getString(R.string.friend));
                    }
                    makeNotifications(NOTIF_CHANNEL_ID,getString(R.string.db_running));
                    Toast.makeText(thisContext,getString(R.string.block_set) + dtsb.getBlockTill() ,Toast.LENGTH_SHORT).show();
                    startService(dtsb.getPassServiceIntent());
                    startService(dtsb.getTimeCheckIntent());
                    startActivity(new Intent(thisContext, MainActivity.class));
                }
                else Toast.makeText(thisContext,R.string.fill_in_password,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeNotifications(String channel_id, String which)
    {
        Intent intent;
        NotificationCompat.Builder notifBuilder;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(thisContext);

        if(which == getString(R.string.friend)) {
            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dtsb.getFriend_number()));
            notifBuilder = new NotificationCompat.Builder(thisContext, channel_id)
                    .setSmallIcon(R.drawable.friend_call_icon)
                    .setContentTitle(getString(R.string.call_friend))
                    .setContentText(dtsb.getFriend_number())
                    .setShowWhen(false)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(createPendingIntent(intent))
                    .setOngoing(true);

            notificationManager.notify(FRIEND_NOTF_ID, notifBuilder.build());
        }
        else if (which == getString(R.string.host)) {
            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dtsb.getHost_number()));
            notifBuilder = new NotificationCompat.Builder(thisContext, channel_id)
                    .setSmallIcon(R.drawable.home_phone_small)
                    .setContentTitle(getString(R.string.call_host))
                    .setContentText(dtsb.getHost_number())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(createPendingIntent(intent))
                    .setShowWhen(false)
                    .setOngoing(true);
            notificationManager.notify(HOST_NOTIF_ID, notifBuilder.build());
        }
        else if(which == getString(R.string.db_running))
        {
            intent = new Intent(thisContext,MainActivity.class);
            notifBuilder = new NotificationCompat.Builder(thisContext, channel_id)
                    .setSmallIcon(R.drawable.locked_locker)
                    .setContentTitle(getString(R.string.db_running))
                    .setContentText(getString(R.string.click_to_launch))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(createPendingIntent(intent))
                    .setOngoing(true);
            notificationManager.notify(HOST_NOTIF_ID, notifBuilder.build());
        }
    }

    private boolean makeNotificationChannel(String channel_id)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isNotfChannelSet)
            {
                NotificationChannel channel = new NotificationChannel(channel_id, getString(R.string.notf_channel_name), NotificationCompat.PRIORITY_DEFAULT);
                channel.setDescription(getString(R.string.notf_chan_desc));
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
                isNotfChannelSet = true;
                return isNotfChannelSet;
            }
        }
        isNotfChannelSet = true;
        return isNotfChannelSet;
    }

    private PendingIntent createPendingIntent(Intent intent)
    {
        return PendingIntent.getActivities(thisContext, 0, new Intent[]{intent}, 0);
    }



}
