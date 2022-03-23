package edu.byu.cs.tweeter.client.model.net;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ServerFacadeTest {
    @Test
    public void testRegister_success() {
        try {
            RegisterResponse response = new ServerFacade().register(
                    new RegisterRequest("Lance", "Meurs", "lmeurs", "mypassword", "12345"),
                    "/register");
            assertEquals("@allen", response.getUser().getAlias());
            assertNotNull(response.getAuthToken());
        }
        catch(IOException | TweeterRemoteException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void testRegister_failure() {
        try {
            RegisterResponse response = new ServerFacade().register(
                    new RegisterRequest(null, "Meurs", "lmeurs", "mypassword", "12345"),
                    "/register");
        }
        catch (IOException | TweeterRemoteException ex) {
            assertEquals("[BadRequest] Missing a username", ex.getMessage());
        }
    }

    @Test
    public void getFollowers_success() {
        try {
            FollowersResponse response = new ServerFacade().getFollowers(
                    new FollowersRequest(new AuthToken(), "@allen", 10, "@elizabeth"),
                    "/get-followers");
            assertEquals(10, response.getFollowers().size());
            assertEquals("@frank", response.getFollowers().get(0).getAlias());
            assertTrue(response.getHasMorePages());
        }
        catch(IOException | TweeterRemoteException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void testFollowers_failure() {
        try {
            FollowersResponse response = new ServerFacade().getFollowers(
                    new FollowersRequest(new AuthToken(), null, 10, "@elizabeth"),
                    "/get-followers");
        }
        catch (IOException | TweeterRemoteException ex) {
            assertEquals("[BadRequest] Request needs to have a follower alias", ex.getMessage());
        }
    }

    @Test
    public void getFollowingCount_success() {
        try {
            FollowingCountResponse response = new ServerFacade().getFollowingCount(
                    new GetFollowingCountRequest(new AuthToken(),
                    new User("Allen", "Anderson", "@allen", "hglsrihgsgs")),
                    "/get-following-count");
            assertEquals(21, response.getNumFollowees());
        }
        catch(IOException | TweeterRemoteException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void getFollowingCount_failure() {
        try {
            FollowingCountResponse response = new ServerFacade().getFollowingCount(
                    new GetFollowingCountRequest(new AuthToken(),null),
                    "/get-following-count");
        }
        catch(IOException | TweeterRemoteException ex) {
            assertEquals("[BadRequest] Request needs to have a target user", ex.getMessage());
        }
    }
}
