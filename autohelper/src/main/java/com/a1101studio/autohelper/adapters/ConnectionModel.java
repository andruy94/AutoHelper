package com.a1101studio.autohelper.adapters;

/**
 * Created by andruy94 on 29.05.2017.
 */

public class ConnectionModel {
    private String serverUri = "tcp://iot.eclipse.org:1883";
    private String clientId = "ExampleAndroidClient228";
    private String subscriptionTopic = "test228";
    private String publishTopic = "test";
    private String publishMessage = "";
    public int getQos=0;

    public ConnectionModel(String serverUri, String clientId, String subscriptionTopic, String publishTopic) {
        this.serverUri = serverUri;
        this.clientId = clientId;
        this.subscriptionTopic = subscriptionTopic;
        this.publishTopic = publishTopic;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSubscriptionTopic() {
        return subscriptionTopic;
    }

    public void setSubscriptionTopic(String subscriptionTopic) {
        this.subscriptionTopic = subscriptionTopic;
    }

    public String getPublishTopic() {
        return publishTopic;
    }

    public void setPublishTopic(String publishTopic) {
        this.publishTopic = publishTopic;
    }

    public String getPublishMessage() {
        return publishMessage;
    }

    public void setPublishMessage(String publishMessage) {
        this.publishMessage = publishMessage;
    }




}
