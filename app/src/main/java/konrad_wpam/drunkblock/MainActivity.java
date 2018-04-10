package konrad_wpam.drunkblock;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    String msg = "Android : ";
    Button b1;
    int a = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(msg,"ON CREATE");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(a==0) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
            Intent i = new Intent(this, PasswordService.class);
            startService(i);
            a=1;
            Log.d(msg,"Serwis ruszyl!!!");
        }
        Log.d(msg,"ON Start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(msg,"ON RESUME");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(msg,"ON Pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Intent passwordIntent = new Intent(this,PasswordService.class);
        //this.startService(passwordIntent);
        Log.d(msg,"ON Stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(msg,"ON Destroy");
    }

}
