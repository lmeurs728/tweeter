package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Looper;

import java.util.List;

public abstract class GetItemsHandler<T> extends BaseHandler {
    String key = "stuff";

    public GetItemsHandler(Looper looper) {
        super(looper);
    }

    public GetItemsHandler() {}

    @Override
    protected void onSuccess(Bundle bundle) {
        List<T> items = (List<T>) bundle.getSerializable(key);
        boolean hasMorePages = bundle.getBoolean(FollowService.GetFollowersTask.MORE_PAGES_KEY);
        addStuff(items, hasMorePages);
    }

    public abstract void addStuff(List<T> items, boolean hasMorePages);
}
