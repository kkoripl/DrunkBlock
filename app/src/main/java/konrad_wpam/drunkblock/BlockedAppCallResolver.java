package konrad_wpam.drunkblock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Random;

/**
 * BlockedAppCallResolver - okienko wpisywania hasla w razie proby uruchomienia zablokowanej apki
 */
public class BlockedAppCallResolver extends android.app.Activity
{
    public static int STOP_LOCKING = 1;
    public static int TRY_TO_UNLOCK_APP = 0;
    private int whenWantWindow;
    private DataToSetBlock dtsb = DataToSetBlock.getDataToBlockInstance();
    private int[] passwordSignsOrder;
    private AlertDialog passwordWindow;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Intent intent = getIntent();
        whenWantWindow = intent.getIntExtra(String.valueOf(R.string.when_password_window),TRY_TO_UNLOCK_APP); // probujemy odblokowac aplikacje
        super.onCreate(savedInstanceState);
        if (whenWantWindow == TRY_TO_UNLOCK_APP) {
            if (!dtsb.getPassword().equals("")) {
                passwordWindow = createPasswordWindow(whenWantWindow);
                passwordWindow.show();
            } else {
                actionIfPasswordFailedToUnlock();
                finish();
            }
        } else { //chcemy zakonczyc blokowanie wszystkich aplikacji
            passwordWindow = createPasswordWindow(whenWantWindow);
            passwordWindow.show();
        }

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(passwordWindow != null)
            {
                passwordWindow.dismiss();
                passwordWindow = null;
            }
    }

    private AlertDialog createPasswordWindow(int when)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(BlockedAppCallResolver.this);
        LayoutInflater inflater = (LayoutInflater) BlockedAppCallResolver.this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View mView = getLayoutInflater().inflate(R.layout.password_window,null);

        final EditText passwordField = (EditText) mView.findViewById(R.id.pw_password_field);
        Button ok = (Button) mView.findViewById(R.id.pw_ok);
        Button cancel = (Button) mView.findViewById(R.id.pw_cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                actionIfPasswordFailedToUnlock();
            }
        });
        passwordSignsOrder = samplingWithoutReplacement(dtsb.getPassword().length()); // losujemy kolejnosc znakow hasla do wpisania
        TextView title = (TextView) mView.findViewById(R.id.pw_title);
        title.setText(getString(R.string.signs_order) + " " + Arrays.toString(passwordSignsOrder));
        builder.setView(mView);
        final AlertDialog ad = builder.create();

        if(when == TRY_TO_UNLOCK_APP) { // probujemy odblokowac aplikacje
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!Validator.validateFilledPassword(passwordField.getText().toString(), dtsb.getPassword(), passwordSignsOrder))
                        actionIfPasswordFailedToUnlock();
                    else {
                        finish();
                    }
                }
            });
        }
        else if(when == STOP_LOCKING) //chcemy zakonczyc blokowanie wszystkich aplikacji
        {
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Validator.validateFilledPassword(passwordField.getText().toString(), dtsb.getPassword(), passwordSignsOrder))
                    {
                        stopLocking();
                    }
                    finish();
                }
            });
        }
        return ad;
    }

    //Losujemy kolejnosc znakow hasla do wpisania
    private int[] samplingWithoutReplacement(int passwordSize)
    {
       int[] passwordSignsOrder = new int[passwordSize];
       int randomNumber = -1;
       boolean[] wasRand = new boolean[passwordSize];
       Arrays.fill(wasRand,false);
       for(int i=1;i<=passwordSize;i++)
       {
           do
           {
               randomNumber = new Random().nextInt(passwordSize);
           }
           while (wasRand[randomNumber]==true);
           passwordSignsOrder[i-1] = randomNumber+1;
           wasRand[randomNumber] = true;
       }
       return passwordSignsOrder;
    }

    // Jak pomylimy haslo lub wcisniemy cancel - wroc do menu glownego
    private void actionIfPasswordFailedToUnlock()
    {
        //backToMainScreen()
        Intent getToHomeScreen = new Intent(Intent.ACTION_MAIN);
        getToHomeScreen.addCategory(Intent.CATEGORY_HOME);
        getToHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(getToHomeScreen);
    }

    private void stopLocking()
    {
        stopService(dtsb.getTimeCheckIntent()); // przerwij sprawdzanie czasu
        stopService(dtsb.getPassServiceIntent()); // przerwij blokowanie
    }
}
