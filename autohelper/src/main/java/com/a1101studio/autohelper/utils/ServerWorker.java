package com.a1101studio.autohelper.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;

import com.a1101studio.autohelper.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;

import static com.a1101studio.autohelper.OpenFragment.msg;


/**
 * Created by andruy94 on 14.05.2017.
 */

public class ServerWorker {

    private final CallBackMessage callBackMessage;

    public interface CallBackMessage {
        void onMessageArrive(String s1, String s2);
    }

    Context context;

    public IMqttMessageListener getiMqttMessageListener() {
        return iMqttMessageListener;
    }

    public void setiMqttMessageListener(IMqttMessageListener iMqttMessageListener) {
        this.iMqttMessageListener = iMqttMessageListener;
    }

    IMqttMessageListener iMqttMessageListener;

    public MqttAndroidClient getMqttAndroidClient() {
        return mqttAndroidClient;
    }

    MqttAndroidClient mqttAndroidClient;

    String serverUri = "tcp://iot.eclipse.org:1883";

    String clientId = "ExampleAndroidClient228";
    String subscriptionTopic = "test228";
    String publishTopic = "test";
    final String publishMessage = "Hello World!";


    public ServerWorker(Context context, CallBackMessage callBackMessage) {
        this.context = context;
        serverUri = PreferenceManager
                .getDefaultSharedPreferences(
                        context
                )
                .getString(context.getString(R.string.open_key_web_adress), "");

        subscriptionTopic =  PreferenceManager
                .getDefaultSharedPreferences(
                        context
                )
                .getString(context.getString(R.string.open_key_sub_kanal), "");
        publishTopic=PreferenceManager
                .getDefaultSharedPreferences(
                        context
                )
                .getString(context.getString(R.string.open_key_write_kanal), "");
        clientId = clientId + System.currentTimeMillis();
        this.callBackMessage = callBackMessage;
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    addToHistory("Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic();
                } else {
                    addToHistory("Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                addToHistory("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                msg += new String(message.getPayload()) + "\n";
                addToHistory("Incoming message: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);


        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    addToHistory("Failed to connect to: " + serverUri);
                }
            });


        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    addToHistory("Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    addToHistory("Failed to subscribe");
                }
            });

            // THIS DOES NOT WORK!
            //if (iMqttMessageListener != null)
            mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(final String topic, final MqttMessage message) throws Exception {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBackMessage.onMessageArrive(topic, message.toString());
                        }
                    });

                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void publishMessage(String publishMessage) {

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(publishMessage.getBytes());
            mqttAndroidClient.publish(publishTopic, message);
            mqttAndroidClient.publish(publishTopic + 228, ("test" + new Random().nextInt()%14).getBytes(), 0, false);
            addToHistory("Message Published");
            if (!mqttAndroidClient.isConnected()) {
                addToHistory(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void addToHistory(String mainText) {
        System.out.println("LOG: " + mainText);
        //mAdapter.add(mainText);
        if (context != null && false)
            Snackbar.make(((Activity) context).findViewById(android.R.id.content), mainText, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();


    }
}
