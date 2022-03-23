package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;

public abstract class BaseHandler extends Handler {
    protected String thingtoAccomplish = "thing";

    public static final String SUCCESS_KEY = "success";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    public BaseHandler() {}

    public BaseHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        onResponse();
        boolean success = msg.getData().getBoolean(SUCCESS_KEY);
        Bundle bundle = msg.getData();
        if (success) {
            onSuccess(bundle);
        } else if (bundle.containsKey(MESSAGE_KEY)) {
            String message = bundle.getString(MESSAGE_KEY);
            onFailure("Failed to " + thingtoAccomplish + ": " + message);
        } else if (bundle.containsKey(EXCEPTION_KEY)) {
            Exception ex = (Exception) bundle.getSerializable(EXCEPTION_KEY);
            onFailure("Failed to " + thingtoAccomplish + " because of exception: " + ex.getMessage());
        }
        afterHandledResponse();
    }

    protected void onResponse() {}

    protected abstract void onSuccess(Bundle msg);

    protected abstract void onFailure(String message);

    protected void afterHandledResponse() {}
}
