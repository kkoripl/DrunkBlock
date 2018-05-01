package konrad_wpam.drunkblock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends Activity
{
    private DataToSetBlock dtsb = DataToSetBlock.getDataToBlockInstance();
    private Context thisContext = this;
    private Button block_button;
    private Spinner block_hours;
    private Switch pass_off_on;
    private EditText password, host_number, friend_number;

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
        if(pass_off_on.isChecked())
        {
            dtsb.setPassword(password.getText().toString());
        }
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
                if(isChecked) password.setActivated(true);
                else
                {
                    password.setText(null);
                    password.setActivated(false);
                }
            }
        });
    }

    private void addBlockButtonListener()
    {
        block_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(thisContext,"Block set",Toast.LENGTH_SHORT);
                startService(new Intent(thisContext, PasswordService.class));
            }
        });
    }

}
