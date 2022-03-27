package edu.byu.cs.tweeter.client.model.service;

import android.util.Log;
import edu.byu.cs.tweeter.model.domain.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//https://stackoverflow.com/questions/2295221/java-net-url-read-stream-to-byte

/**
 * BackgroundTaskUtils contains utility methods needed by background tasks.
 */
public class BackgroundTaskUtils {

    private static final String LOG_TAG = "BackgroundTaskUtils";

    /**
     * Loads the profile image for the user.
     *
     * @param user the user whose profile image is to be loaded.
     * @return bytes[]
     */
    public static byte[] loadImage(User user) {
        try {
            return bytesFromUrl(user.getImageUrl());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString(), e);
        }
        return new byte[0];
    }

    public static void runTask(Runnable task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }

    private static byte[] bytesFromUrl(String u) {
        URL url = null;
        try {
            url = new URL(u);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            assert url != null;
            is = url.openStream();
            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n;

            while ( (n = is.read(byteChunk)) > 0 ) {
                baos.write(byteChunk, 0, n);
            }
        }
        catch (IOException e) {
            System.err.printf ("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
            e.printStackTrace ();
            // Perform any other exception handling that's appropriate.
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return baos.toByteArray();
    }

}
