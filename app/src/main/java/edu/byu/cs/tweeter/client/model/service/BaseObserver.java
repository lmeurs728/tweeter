package edu.byu.cs.tweeter.client.model.service;

/**
 * An observer interface to be implemented by observers who want to be notified when
 * asynchronous operations complete.
 */
public interface BaseObserver {
    void sendMessage(String message);
}
