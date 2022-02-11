package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;

public class GetFollowCountTask extends BackgroundTask {
    public static final String COUNT_KEY = "count";

    public GetFollowCountTask(Handler messageHandler) {
        super(messageHandler);
    }

    @Override
    protected void runTask() {
        sendSuccessMessage(msgBundle -> msgBundle.putInt(COUNT_KEY, 20));
    }
}
