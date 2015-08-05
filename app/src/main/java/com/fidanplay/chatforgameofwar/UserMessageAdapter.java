package com.fidanplay.chatforgameofwar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by ali on 18.05.2015.
 */
public class UserMessageAdapter extends RecyclerView.Adapter<UserMessageAdapter.ViewHolder>{

        private List<Message> mMessages;
    private Context cnt;

        public UserMessageAdapter(Context context, List<Message> messages) {
            mMessages = messages;
            cnt = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layout = -1;
            switch (viewType) {
                case Message.TYPE_USER_MESSAGE:
                    layout = R.layout.item_user_message;
                    break;
            }
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(layout, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Message message = mMessages.get(position);
            viewHolder.setUsername(message.getUsername(),message.getMessage());
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mMessages.get(position).getType();
        }

        public class ViewHolder extends RecyclerView.ViewHolder  {
            private Button mUsernameView;

            public ViewHolder(View itemView) {
                super(itemView);

                mUsernameView = (Button) itemView.findViewById(R.id.users);
            }

            public void setUsername(String username,String message) {
                if (null == mUsernameView) return;

                mUsernameView.setText(username);
                if (message.equals("0"))
                    mUsernameView.setBackgroundColor(Color.RED);
                else if (message.equals("1"))
                    mUsernameView.setBackgroundColor(Color.GREEN);
                else
                    mUsernameView.setBackgroundColor(Color.WHITE);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(calendar.getTime());
                int minutes = calendar.get(Calendar.MINUTE);
                int seconds = calendar.get(Calendar.SECOND);
                Random rdm = new Random();
                int  n = rdm.nextInt(5000) + 1;
                mUsernameView.setId(seconds+minutes+n);

                mUsernameView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Button btnx = (Button) (v.findViewById(v.getId()));
                        SharedPreferences pref = cnt.getSharedPreferences("ChatPrefs", 0);
                        pref.edit().putString("room", btnx.getText().toString()).apply();
                        Intent i = new Intent(cnt, MainMessage.class);
                        cnt.startActivity(i);
                    }
                });

            }

        }
    }

