package konrad_wpam.drunkblock;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * MainActivity - Activity wyswietlajaca powitalny ekran aplikacji
 *
 * Instrukcja oraz przycisk bedacy zaleznie od sytuacji:
 * - przejsciem do wyboru blokowanych aplikacji;
 * - proba zakonczenia blokowania;
 * - przejsciem do prosb o nadanie uprawnienia;
 * - informacja, ze nie ustawiono hasla i zakonczenie blokowania skonczy sie dopiero po uplywie czasu
 *
 */
public class MainActivity extends AppCompatActivity {

    private static Context thisContext;
    private DataToSetBlock dtsb = DataToSetBlock.getDataToBlockInstance();
    public static final int PERMISSION_REQUEST_CODE = 200;
    private Intent stopLockIntent;
    private PermissionsChecker permsChecker = new PermissionsChecker(this);
    private Button ChooseApps_StopService;
    private boolean permsChecked = false;

    public static Context getContext()
    {
        return thisContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thisContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (stopLockIntent == null) stopLockIntent = createStopLockIntent(stopLockIntent);

        // sprawdzamy uprawnienia
        if(!permsChecked)
        {
            permsChecker.makeCheck();
            permsChecked = true;
        }
    }

    // metoda odpowiadajaca na prosbe o nadanie uprawnienia - okienko deny / allow
    // permission_granted = wcisniecie allow
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        permsChecked = true;
        boolean ifGranted = true;
        if(requestCode == PERMISSION_REQUEST_CODE)
        {
            if(grantResults.length>0)
            {
                for( int result : grantResults)
                {
                    if(result!=PackageManager.PERMISSION_GRANTED)
                    {
                        ifGranted = false;
                        break;
                    }
                }
                if(ifGranted)
                {
                    DataToSetBlock.getDataToBlockInstance().setPermissionsGiven1(true);
                    whichButtonToCreate();
                }
                else
                {
                    makePermissionsNeededButton();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //jesli nie sprawdzilismy uprawnien to sprawdzamy
        if(!permsChecked)
        {
            permsChecker.makeCheck();
            permsChecked = true;
        }
        //zaleznie od sytuacji renderujemy przycisk w odpowiedniej formie
        whichButtonToCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!permsChecked)
        {
            permsChecker.makeCheck();
            permsChecked = true;
        }

        whichButtonToCreate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        permsChecked = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        permsChecked = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(dtsb.getTimeCheckIntent());
        stopService(dtsb.getPassServiceIntent());
    }

    //Stworzenie intentu do zakonczenia blokowania przyciskiem z menu
    private Intent createStopLockIntent(Intent intent)
    {
        intent = new Intent(getString(R.string.password_window_act)); //new Intent(BlockedAppCallResolver.PASSWORD_WINDOW_ACT);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(String.valueOf(R.string.when_password_window),BlockedAppCallResolver.STOP_LOCKING);
        return intent;
    }

    // metoda tworzaca przyciski zaleznie od sytuacji
    private void whichButtonToCreate()
    {
        ChooseApps_StopService = (Button) findViewById(R.id.to_choose_apps);
        ChooseApps_StopService.invalidate();
        if(!(dtsb.isPermissionsGiven1() && dtsb.isPermissionsGiven2()))
        {
            makePermissionsNeededButton();
        }
        else if (!dtsb.isBlockSet())
        {
            makeChooseAppsButton();
        }
        else if(!dtsb.getPassword().equals(""))
        {
            makeStopLockingButton();
        }
        else
        {
            makeCantStopButton();
        }
    }

    private void makeChooseAppsButton()
    {
        ChooseApps_StopService.setEnabled(true);
        ChooseApps_StopService.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        ChooseApps_StopService.setTextColor(getResources().getColor(R.color.colorWhite));
        ChooseApps_StopService.setText(R.string.choose_apps);
        ChooseApps_StopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(thisContext, AppsListTemp.class));
            }
        });
    }

    private void makeStopLockingButton()
    {
        ChooseApps_StopService.setEnabled(true);
        ChooseApps_StopService.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        ChooseApps_StopService.setTextColor(getResources().getColor(R.color.colorWhite));
        ChooseApps_StopService.setText(R.string.stop_locking_apps);
        ChooseApps_StopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(stopLockIntent);
            }});
    }

    private void makeCantStopButton()
    {
        ChooseApps_StopService.setText(R.string.cant_stop_locking);
        ChooseApps_StopService.setEnabled(false);
    }

    private void makePermissionsNeededButton()
    {
        ChooseApps_StopService.setText(R.string.we_need_permissions);
        ChooseApps_StopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permsChecker.makeCheck();
            }
        });
    }

}
