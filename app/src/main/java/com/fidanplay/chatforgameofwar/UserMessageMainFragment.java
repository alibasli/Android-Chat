package com.fidanplay.chatforgameofwar;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ali on 18.05.2015.
 */
public class UserMessageMainFragment extends Fragment {

    private static final int TYPING_TIMER_LENGTH = 600;

    private RecyclerView mMessagesView;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter;
    private String mUsername;
    private String mRoom;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://104.209.44.102:8003");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public UserMessageMainFragment() {
        super();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new UserMessageAdapter(activity, mMessages);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectionDetector cd = new ConnectionDetector(getActivity());
        if(!cd.isConnectingToInternet())
        {
            Intent intent = new Intent(getActivity(),MainActivity.class);
            startActivity(intent);
        }

        setHasOptionsMenu(true);

        SharedPreferences pref = getActivity().getSharedPreferences("ChatPrefs", 0);
        mRoom = pref.getString("room", "");
        mUsername = pref.getString("username", "");
        String pages = pref.getString("pages", "");

        if (pages.equals("messages")) {
            mSocket.emit("message list");
        }
        else if (pages.equals("users"))
            mSocket.emit("user list");
        else
            startSignIn();

        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("userList", onOldMessage);
        mSocket.on("messageList", onOldMessage);
        mSocket.connect();

        //  startSignIn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_message_main, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("userList", onOldMessage);
        mSocket.off("messageList", onOldMessage);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                attemptSend(view);
            }
        });

        AdView adView = (AdView) view.findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        adView.loadAd(adRequest1);

    }
    private void attemptSend(View view) {
        if (!mSocket.connected()) return;

        EditText mInputMessageView = (EditText) view.findViewById(R.id.message_input);

        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }
        mMessages.clear();
        mAdapter.notifyDataSetChanged();

        mInputMessageView.setText("");
        mSocket.emit("search friends", message);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        //String pages = data.getStringExtra("pages");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_leave) {
            leave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addMessage(String username, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_USER_MESSAGE).username(username).message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }


    private void startSignIn() {
        mUsername = null;
        SharedPreferences pref = getActivity().getSharedPreferences("ChatPrefs", 0);
        pref.edit().putString("email", "").commit();
        pref.edit().putString("username", "").commit();
        pref.edit().putString("room", "").commit();
        pref.edit().putString("name", "").commit();
        pref.edit().putString("surname", "").commit();

        Intent i = new Intent(getActivity(), Login.class);
        startActivity(i);
    }

    private void leave() {
        mUsername = null;
        mSocket.disconnect();
        mSocket.connect();
        startSignIn();
    }

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    private Emitter.Listener onOldMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String username;
                    String message;
                    try {
                        username = args[0].toString();
                        message = args[1].toString();
                        addMessage(username, message);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

}

