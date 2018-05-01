package konrad_wpam.drunkblock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Random;

public class BlockedAppCallResolver extends android.app.Activity //FragmentActivity
{
    private String passwordSet = "12345";
    private int[] passwordSignsOrder;
    AlertDialog passwordWindow;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        passwordWindow = createPasswordWindow();
        passwordWindow.show();
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

    private AlertDialog createPasswordWindow()
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
                actionIfPasswordFailed();
            }
        });
        passwordSignsOrder = samplingWithoutReplacement(passwordSet.length());
        TextView title = (TextView) mView.findViewById(R.id.pw_title);
        title.setText(R.string.signs_order + Arrays.toString(passwordSignsOrder));
        builder.setView(mView);
        final AlertDialog ad = builder.create();
        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                if(!checkPassword(passwordField.getText().toString(),passwordSet,passwordSignsOrder)) actionIfPasswordFailed();
                else
                {
                    finish();
                }
            }
        });
        return ad;
    }

    private boolean checkPassword(String passwordInput, String passwordSet, int[] passwordSignsOrder)
    {
        if(passwordInput.length()==passwordSet.length())
        {
            for (int i = 0; i < passwordSet.length(); i++) {
                System.out.println(i + " IN: " + passwordInput.charAt(i) + " || " + passwordSet.charAt(passwordSignsOrder[i] - 1));
                if (passwordInput.charAt(i) != passwordSet.charAt(passwordSignsOrder[i] - 1)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

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
       System.out.println("Przelosowalem: " + Arrays.toString(passwordSignsOrder));
       return passwordSignsOrder;
    }

    private void actionIfPasswordFailed()
    {
        //backToMainScreen()
        Intent getToHomeScreen = new Intent(Intent.ACTION_MAIN);
        getToHomeScreen.addCategory(Intent.CATEGORY_HOME);
        getToHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(getToHomeScreen);
    }
}
