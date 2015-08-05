package com.fidanplay.chatforgameofwar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;


public class MainActivity extends Activity {

    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                if(!cd.isConnectingToInternet())
                {
                    h.postDelayed(this,1000);

                }else
                    kontrol();
            }
        }, 1000);
    }
    @Override
    public void onBackPressed() {
        return;
    }


    public void kontrol(){
        SharedPreferences pref = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = pref.getString("username", "");

        if(mUsername.equals("")){
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
        }
        else{
            Intent i = new Intent(MainActivity.this, Profile.class);
            startActivity(i);
        }
    }


}
