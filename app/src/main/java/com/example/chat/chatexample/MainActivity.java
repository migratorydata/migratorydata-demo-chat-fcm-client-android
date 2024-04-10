package com.example.chat.chatexample;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends Activity {
    // TODO: to be configured
    public static final String LOG_TAG = "migratorydataChatApp";
    private static final String roomName = "demoRoom";

    private ChatApp chatApp;

    private EditText usernameEditText;
    private EditText chatEditText;
    private EditText roomEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        roomEditText = findViewById(R.id.room);
        chatEditText = findViewById(R.id.chat_edit_text);
        usernameEditText = findViewById(R.id.username);
        roomEditText.setText(roomName);
        usernameEditText.setText("name-" + Build.ID);

        final TextView textViewChatMessages = (TextView) findViewById(R.id.chat);
        textViewChatMessages.setMovementMethod(new ScrollingMovementMethod());
        final TextView textViewInfo = (TextView) findViewById(R.id.info);
        textViewInfo.setMovementMethod(new ScrollingMovementMethod());

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String fcmToken = task.getResult();

                        Log.i(LOG_TAG, "Got FCM Token=" + fcmToken);

                        chatApp = new ChatApp(textViewChatMessages, textViewInfo, fcmToken, roomName);
                        chatApp.init();
                    }
                });

        final Button chat = (Button) findViewById(R.id.send);
        chat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.sendChatMessage();
            }
        });

        chatEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    MainActivity.this.sendChatMessage();
                    return false;
                }
                return false;
            }
        });

        final Button logout = findViewById(R.id.logInOut_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.logInOut();
            }
        });

        final Button closeApp = findViewById(R.id.closeapp_btn);
        closeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.closeApp();
            }
        });

    }

    private void logInOut() {
        if (chatApp == null) {
            Log.i("LOG_TAG", "App is not initialize, ignore logout!");
            return;
        }

        Button btn = (Button) findViewById(R.id.logInOut_btn);
        if (chatApp.getAppStatus() == ChatApp.AppStatus.LOG_IN) {
            chatApp.logout();
            btn.setText("Login");
        } else if (chatApp.getAppStatus() == ChatApp.AppStatus.LOG_OUT) {
            chatApp.login();
            btn.setText("Logout");
        }
    }

    private void closeApp() {
        if (chatApp.getAppStatus() != ChatApp.AppStatus.LOG_OUT) {
            Toast.makeText(getApplicationContext(), "Please LOG OUT before closing the APP", Toast.LENGTH_LONG).show();
            return;
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void sendChatMessage() {
        if (chatApp == null) {
            return;
        }

        String chatMessage = chatEditText.getText().toString();
        if (chatMessage != null && chatMessage.length() > 0) {
            chatApp.chat(usernameEditText.getText().toString(), chatMessage);
        }

        chatEditText.setText("");
    }

    @Override
    public void onPause() {
        Log.i(LOG_TAG, "onPause");

        super.onPause();
        if (chatApp != null) {
            chatApp.pause();
        }
    }

    @Override
    public void onResume() {
        Log.i(LOG_TAG, "onResume");

        super.onResume();
        if (chatApp != null) {
            chatApp.resume();
        }
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy");

        super.onDestroy();
        if (chatApp != null) {
            chatApp.disconnect();
        }
    }
}
