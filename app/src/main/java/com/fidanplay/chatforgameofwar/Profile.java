package com.fidanplay.chatforgameofwar;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Profile extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if(!cd.isConnectingToInternet())
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        SharedPreferences pref = getApplication().getSharedPreferences("ChatPrefs", 0);
        String Username = pref.getString("username", "");

        mSocket.emit("user logined", Username);
        mSocket.connect();

        AdView adView = (AdView) findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        adView.loadAd(adRequest1);

    }
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://104.209.44.102:8003");
        } catch (Exception e) {}
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    @Override
    public void onBackPressed() {
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if(!cd.isConnectingToInternet())
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        return;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_leave) {
            leave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void leave() {
        mSocket.disconnect();
        mSocket.connect();
        startSignIn();
    }
    private void startSignIn() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("ChatPrefs", 0);
        pref.edit().putString("email", "").commit();
        pref.edit().putString("username", "").commit();
        pref.edit().putString("room", "").commit();
        pref.edit().putString("name", "").commit();
        pref.edit().putString("surname", "").commit();

        Intent i = new Intent(getApplicationContext(), Login.class);
        startActivity(i);
    }
    public void mesajlar(View view) {

        Intent i = new Intent(Profile.this, MainUserMessage.class);
        SharedPreferences pref = Profile.this.getSharedPreferences("ChatPrefs", 0);
        pref.edit().putString("pages", "messages").commit();
        startActivity(i);
    }
    public void kullanicilar(View view) {

        Intent i = new Intent(Profile.this, MainUserMessage.class);
        SharedPreferences pref = Profile.this.getSharedPreferences("ChatPrefs", 0);
        pref.edit().putString("pages", "users").commit();
        startActivity(i);
    }

    public void logout(View view) {
        mSocket.disconnect();
        SharedPreferences pref = Profile.this.getSharedPreferences("ChatPrefs", 0);
        pref.edit().putString("email", "").commit();
        pref.edit().putString("username", "").commit();
        pref.edit().putString("room", "").commit();

        Intent i = new Intent(Profile.this, Login.class);
        startActivity(i);
    }
}
