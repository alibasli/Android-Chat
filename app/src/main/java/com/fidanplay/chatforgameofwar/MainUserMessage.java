package com.fidanplay.chatforgameofwar;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

public class MainUserMessage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_message_main);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("ChatPrefs", 0);
        String pages = pref.getString("pages", "");
        if (pages.equals("messages")) {
            LinearLayout lyt = (LinearLayout) (findViewById(R.id.search_lyt));
            lyt.setVisibility(View.GONE);
        }

    }
}
