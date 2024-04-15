package com.example.chat.chatexample;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.chat.chatexample.util.JsonDecoder;
import com.example.chat.chatexample.util.JsonEncoder;
import com.migratorydata.client.MigratoryDataClient;
import com.migratorydata.client.MigratoryDataListener;
import com.migratorydata.client.MigratoryDataLogLevel;
import com.migratorydata.client.MigratoryDataLogListener;
import com.migratorydata.client.MigratoryDataMessage;
import com.migratorydata.client.utils.MonotonicId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.example.chat.chatexample.MainActivity.LOG_TAG;

public class ChatApp {
    // TODO: to be configured
    private static final String PUBLISH_PASSWORD = "some-token";
    private static final String SERVER_ADDRESS = "127.0.0.1:8800";
    private static final boolean USE_ENCRYPTION = false;
    //private static final String SERVER_ADDRESS = "demo.migratorydata.com";
    //private static final boolean USE_ENCRYPTION = true;

    private AppStatus appStatus = AppStatus.LOG_IN;
    private String LOGOUT_SUBJECT = "/__migratorydata__/presence/logout";

    private Handler handler = new Handler();

    private final SortedMap<Long, String> roomMessages = new TreeMap<>();

    private MigratoryDataClient migratoryDataClient;
    private final TextView textViewChatMessages;
    private final TextView textViewInfo;
    private final String fcmToken;
    private final String roomName;

    public ChatApp(TextView textViewChatMessages, TextView textViewInfo, String fcmToken, String roomName) {
        this.textViewChatMessages = textViewChatMessages;
        this.textViewInfo = textViewInfo;
        this.fcmToken = fcmToken;
        this.roomName = roomName;
    }

    public void init() {
        migratoryDataClient = new MigratoryDataClient();
        migratoryDataClient.setLogListener(new MigratoryDataLogListener() {
            @Override
            public void onLog(String message, MigratoryDataLogLevel migratoryDataLogLevel) {
                Log.i(LOG_TAG, message);
            }
        }, MigratoryDataLogLevel.DEBUG);

        migratoryDataClient.setEntitlementToken(PUBLISH_PASSWORD);
        migratoryDataClient.setExternalToken(fcmToken);
        migratoryDataClient.setListener(new MigratoryDataListener() {
            @Override
            public void onMessage(final MigratoryDataMessage message) {
                Log.i(LOG_TAG, "Got onMessage=" + message);

                if (message.getMessageType() == MigratoryDataMessage.MessageType.RECOVERED || message.getMessageType() == MigratoryDataMessage.MessageType.SNAPSHOT) {
                    return;
                }

                final JsonDecoder decodeChatMessage = new JsonDecoder(new String(message.getContent()));
                String chatMessage = formatChatMessage(decodeChatMessage);
                roomMessages.put(Long.valueOf(decodeChatMessage.getTimestamp()), chatMessage);

                // update activity view
                final Collection<String> messages = new ArrayList<>(roomMessages.values());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewChatMessages.setText("");
                        for (String m : messages) {
                            textViewChatMessages.append(m + "\n");
                        }
                    }
                });
            }

            @Override
            public void onStatus(final String status, final String info) {
                Log.i(LOG_TAG, "Got onStatus=" + status + " - " + info);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewInfo.append(status + " - " + info + "\n");
                    }
                });

                if (MigratoryDataClient.NOTIFY_SERVER_UP.equals(status)) {
                    if (appStatus == AppStatus.PERFORMING_LOG_OUT && migratoryDataClient.getSubjects().contains(LOGOUT_SUBJECT)) {
                        updateAppStatus(AppStatus.LOG_OUT);
                    }
                }
            }

            private String formatChatMessage(JsonDecoder decodeChatMessage) {
                String username = decodeChatMessage.getUserName();
                String message = decodeChatMessage.getMessage();
                Long timestamp = decodeChatMessage.getTimestamp();

                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                String date = df.format(new Date(timestamp));

                String chatMessage = "[" + date + "][" + (username != null ? username : "nobody") + "] " + message;
                return chatMessage;
            }
        });

        migratoryDataClient.setEncryption(USE_ENCRYPTION);
        migratoryDataClient.setServers(new String[]{SERVER_ADDRESS});

        migratoryDataClient.subscribeWithHistory(Arrays.asList("/rooms/" + roomName), 20);
        handler.post(new Runnable() {
            @Override
            public void run() {
                textViewInfo.append("Subscribing to room=" + "/rooms/" + roomName + "\n");
            }
        });

        migratoryDataClient.connect();
    }

    public void pause() {
        if (migratoryDataClient != null) {
            migratoryDataClient.pause();
        }
    }

    public void resume() {
        if (migratoryDataClient != null) {
            migratoryDataClient.resume();
        }
    }

    public void disconnect() {
        if (migratoryDataClient != null) {
            migratoryDataClient.disconnect();
        }
    }

    public void chat(String userName, String message) {
        JsonEncoder encoder = new JsonEncoder();
        encoder.setUserName(userName);
        encoder.setMessageTimestamp(System.currentTimeMillis());
        encoder.setMessageContent(message);

        try {
            if (migratoryDataClient != null) {
                migratoryDataClient.publish(new MigratoryDataMessage("/rooms/" + roomName, encoder.toString().getBytes()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized public AppStatus getAppStatus() {
        return appStatus;
    }

    synchronized private void updateAppStatus(AppStatus newStatus) {
        this.appStatus = newStatus;
    }

    public void logout() {
        migratoryDataClient.pause();

        migratoryDataClient.unsubscribe(new ArrayList<String>(migratoryDataClient.getSubjects()));

        migratoryDataClient.subscribe(Arrays.asList(LOGOUT_SUBJECT));

        updateAppStatus(AppStatus.PERFORMING_LOG_OUT);

        migratoryDataClient.resume();
    }

    public void login() {
        if (appStatus == AppStatus.LOG_OUT) {
            migratoryDataClient.pause();

            migratoryDataClient.unsubscribe(new ArrayList<String>(migratoryDataClient.getSubjects()));

            migratoryDataClient.subscribe(Arrays.asList("/rooms/" + roomName));

            updateAppStatus(AppStatus.LOG_IN);

            migratoryDataClient.resume();
        }
    }

    enum AppStatus {
        LOG_IN, PERFORMING_LOG_OUT, LOG_OUT
    }
}
