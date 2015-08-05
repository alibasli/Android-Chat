package com.fidanplay.chatforgameofwar;


import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.common.AccountPicker;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

public class Login extends Activity {

    String userInceptions[]=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if(!cd.isConnectingToInternet())
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        kontrol();
        mSocket.connect();
        mSocket.on("login",logined);
        mSocket.on("login failed", loginFailed);
        mSocket.on("already have account",haveAccounted);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("login", logined);
        mSocket.off("login failed", loginFailed);
        mSocket.off("already have account", haveAccounted);
    }
    private Emitter.Listener logined = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.disconnect();
            SharedPreferences pref = Login.this.getSharedPreferences("ChatPrefs", 0); // 0 - for private mode
            pref.edit().putString("username", args[0].toString()).commit();
            pref.edit().putString("email", args[1].toString()).commit();
            pref.edit().putString("name", args[2].toString()).commit();
            pref.edit().putString("surname", args[3].toString()).commit();
            pref.edit().putString("room", args[4].toString()).commit();

            Intent i = new Intent(Login.this, Profile.class);
            startActivity(i);
        }
    };

    private Emitter.Listener loginFailed = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
          loginFail.run();

        }
    };
    private Emitter.Listener haveAccounted = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
           haveAccount.run();
        }
    };
    public void signupshow(View view){
        RelativeLayout lyt_signup = (RelativeLayout) (findViewById(R.id.layout_signup));
        lyt_signup.setVisibility(View.VISIBLE);
        RelativeLayout lyt_signin = (RelativeLayout) (findViewById(R.id.layout_signin));
        lyt_signin.setVisibility(View.GONE);
    }
    public void signinshow(View view){
        RelativeLayout lyt_signup = (RelativeLayout) (findViewById(R.id.layout_signup));
        lyt_signup.setVisibility(View.GONE);
        RelativeLayout lyt_signin = (RelativeLayout) (findViewById(R.id.layout_signin));
        lyt_signin.setVisibility(View.VISIBLE);
    }
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://104.209.44.102:8003");
        } catch (Exception e) {}
    }

    public void login(View view) {
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if(!cd.isConnectingToInternet())
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        EditText text_username = (EditText) (findViewById(R.id.text_signup_username));
        String Uname = text_username.getText().toString();
        EditText text_password = (EditText) (findViewById(R.id.text_signup_sifre));
        String Upass = text_password.getText().toString();

        if(Uname.equals("")){
            Toast.makeText(Login.this, "Please choose username or email", Toast.LENGTH_SHORT).show();
        }
        else {
            Uname = temizle(Uname);

            if(Upass.equals("")) {
                Toast toast = Toast.makeText(Login.this, "Please type your password", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                Button btn = (Button) findViewById(R.id.buton_signup_login);
                btn.setEnabled(false);
                mSocket.connect();
                mSocket.emit("logining", Uname,Upass);
            }
        }
    }
    public void creataccount(View view) {
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if(!cd.isConnectingToInternet())
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        Button EmailButton = (Button) (findViewById(R.id.buton_signin_email));
        String Email = EmailButton.getText().toString();
        EditText text_name = (EditText) (findViewById(R.id.text_signin_name));
        String name = text_name.getText().toString();
        EditText text_surname = (EditText) (findViewById(R.id.text_signin_surname));
        String surname = text_surname.getText().toString();
        EditText text_password = (EditText) (findViewById(R.id.text_signin_sifre));
        String pass = text_password.getText().toString();

        if(Email.equals("Choose Email")){
            Toast toast = Toast.makeText(Login.this, "Please choose email", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            if(name.equals("")){
                Toast.makeText(Login.this, "Please type your name", Toast.LENGTH_SHORT).show();
            }
            else {
                if(surname.equals("")){
                    Toast.makeText(Login.this, "Please type your surname", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(pass.equals("")){
                        Toast.makeText(Login.this, "Please type your password", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Button btn = (Button) findViewById(R.id.buton_signin_login);
                        btn.setEnabled(false);
                        mSocket.connect();
                        mSocket.emit("create account", Email, temizle(Email), name, surname, pass);
                    }
                }
            }

        }
    }

    public void choseEmail(View view) {
        Intent intent1 = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"}, false, null, null, null, null);
        startActivityForResult(intent1, 100);
    }

    public void kontrol(){
        SharedPreferences pref = getApplication().getSharedPreferences("ChatPrefs", 0);
        String Username = pref.getString("username", "");
        if(Username.equals("")){
            //-----
        }
        else{
            Intent i = new Intent(Login.this, Profile.class);
            startActivity(i);
        }
    }
    @Override
    public void onBackPressed() {
        return;
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String Email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

            if(Email.equals("")) {
                Toast toast = Toast.makeText(Login.this, "Please choose Email", Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                Button EmailButton = (Button) (findViewById(R.id.buton_signin_email));
                EmailButton.setText(Email);
                EditText text_username = (EditText) (findViewById(R.id.text_signin_username));
                text_username.setText("User Name is : "+temizle(Email));
                text_username.setEnabled(false);
                text_username.setTextColor(Color.BLACK);
            }
        }
    }
    private String temizle(String email){
        for(int i=0;i<email.length();i++)
        {
            if(email.substring(i,i+1).equals("@"))
                return email.substring(0,i);
        }
        return email;
       // Random r = new Random();
       // String Username = "guest" + r.nextInt(100000);
       // return Username;
    }
    private Thread loginFail = new Thread(){
        @Override
        public void run() {
            try {
                synchronized (this) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Button btn = (Button) findViewById(R.id.buton_signup_login);
                            btn.setEnabled(true);
                            Toast.makeText(Login.this, "username or password is wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
    };
    private Thread haveAccount = new Thread(){
        @Override
        public void run() {
            try {
                synchronized (this) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Button btn = (Button) findViewById(R.id.buton_signin_login);
                            btn.setEnabled(true);
                            Toast.makeText(Login.this, "You have already an account!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    };
}
