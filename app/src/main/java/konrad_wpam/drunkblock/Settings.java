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
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Settings - activity odpowiedzialne za wyrenderowanie okna ustawien blokady
 */
public class Settings extends Activity
{
    private static String NOTIF_CHANNEL_ID = "db_notfs"; // od androida 27 trzeba tworzyc kanal powiadomien przed powiadomieniami - jego nazwa
    private static int FRIEND_NOTF_ID = 500;
    private static int HOST_NOTIF_ID = 501;
    private static int DB_NOTIF_ID = 502; // wszystkie trzy potrzebne do identyfikacji powiadomien
    private DataToSetBlock dtsb = DataToSetBlock.getDataToBlockInstance();
    private Context thisContext = this;
    private Button block_button;
    private Spinner block_hours;
    private Switch pass_off_on;
    private EditText password, host_number, friend_number;
    private boolean isNotfChannelSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_settings);
        initViewObjects();
    }

    // inicjacja elementow okna
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

    // wcisnelismy przycisk set block - pobieramy dane do blokady, wpierw zerujac wczesniejsze
    // numer gospodarza, kumpla jesli sa oraz czas blokady
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

    // nastawienie eventow, ktore maja sie wydarzyc po aktywacji lub dezaktywacji hasla
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
                //if((pass_off_on.isChecked() && !password.getText().toString().equals("")) || !pass_off_on.isChecked())
                if(Validator.validateSettingsPassword(pass_off_on.isChecked(),password.getText().toString())) // haslo aktywowane i podane
                {
                    if(Validator.validatePhoneNumber(dtsb.getFriend_number())) { // numer kumpla w dobrym formacie
                        if(Validator.validatePhoneNumber(dtsb.getHost_number())) { // numer gospodarza - || -
                            dtsb.setPassword(password.getText().toString());
                            dtsb.setBlockSet(true);
                            dtsb.setPassServiceIntent(new Intent(thisContext, PasswordService.class));
                            dtsb.setTimeCheckIntent(new Intent(thisContext, BlockTimeChecker.class));

                            if (makeNotificationChannel(NOTIF_CHANNEL_ID)) {
                                if (dtsb.getHost_number().length() != 0)
                                    makeNotifications(NOTIF_CHANNEL_ID, getString(R.string.host), "");
                                if (dtsb.getFriend_number().length() != 0)
                                    makeNotifications(NOTIF_CHANNEL_ID, getString(R.string.friend), "");
                                makeNotifications(NOTIF_CHANNEL_ID, getString(R.string.db_running), dtsb.getBlockTill());
                            }
                            Toast.makeText(thisContext, getString(R.string.block_set) + " " + dtsb.getBlockTill(), Toast.LENGTH_SHORT).show();
                            startService(dtsb.getPassServiceIntent()); // wystartuj blokowanie
                            startService(dtsb.getTimeCheckIntent()); // wystartuj odmierzanie czasu do konca blokowania
                            startActivity(new Intent(thisContext, MainActivity.class)); // wroc do ekranu glownego
                        }
                        else Toast.makeText(thisContext,R.string.hostNo_not_correct,Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(thisContext,R.string.friendNo_not_correct,Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(thisContext,R.string.fill_in_password,Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Tworzymy notyfikacje
    // - jedna mowi o tym, ze blokady aktywne i do kiedy trwaja
    // - a pozostale pokazuja, ze mozesz zadzwonic jesli podales numery
    private void makeNotifications(String channel_id, String which,String blockTill)
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
                    .setShowWhen(false)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(createPendingIntent(intent))
                    .setOngoing(true);
            notificationManager.notify(HOST_NOTIF_ID, notifBuilder.build());
        }
        else if(which == getString(R.string.db_running))
        {
            intent = new Intent(thisContext,MainActivity.class);
            notifBuilder = new NotificationCompat.Builder(thisContext, channel_id)
                    .setSmallIcon(R.drawable.locked_locker)
                    .setContentTitle(getString(R.string.db_running))
                    .setContentText(getString(R.string.block_set) + " " + blockTill)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(createPendingIntent(intent))
                    .setOngoing(true);
            notificationManager.notify(DB_NOTIF_ID, notifBuilder.build());
        }
    }

    // od androida 27 przed stworzeniem powiadomien trzeba stworzyc ich kanal - robimy go
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

    // tworzymy pending intent, czyli akcje, ktora wykonana notyfikacja po jej kliknieciu
    private PendingIntent createPendingIntent(Intent intent)
    {
        return PendingIntent.getActivities(thisContext, 0, new Intent[]{intent}, 0);
    }



}
