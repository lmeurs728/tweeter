package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.*;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.FollowDAODynamo;
import org.junit.Assert;
import org.junit.Test;

public class FollowDAODynamoTest {
    private final FollowDAO followDAO = new FollowDAODynamo();

    @Test
    public void getFollowers() {
        System.out.println(followDAO.getFollowers(new FollowersRequest(null, "@lmeurs", 10, null, true)));
    }

    @Test
    public void getFollowing() {
        followDAO.getFollowees(new FollowingRequest(null, "@lmeurs", 10, null, true));
    }

    @Test
    public void getFollowersCount() {
        Assert.assertEquals(1, followDAO.getFollowerCount("@lmeurs"));
    }

    @Test
    public void getFollowingCount() {
        Assert.assertEquals(0, followDAO.getFolloweeCount("@lmeurs"));
    }

    @Test
    public void isFollower() {
        followDAO.Follow(new FollowRequest(null, "@lmeurs", "@mamamoo"));
        Assert.assertTrue(followDAO.IsFollower(new IsFollowerRequest(null, "@lmeurs", "@mamamoo")).getIsFollower());
        followDAO.Unfollow(new UnfollowRequest(null, "@lmeurs", "@mamamoo"));
        Assert.assertFalse(followDAO.IsFollower(new IsFollowerRequest(null, "@lmeurs", "@mamamoo")).getIsFollower());
    }

    @Test
    public void follow() {
        followDAO.Follow(new FollowRequest(null, "@lmeurs", "@mamamoo"));
    }

    @Test
    public void unfollow() {
        followDAO.Unfollow(new UnfollowRequest(null, "@lmeurs", "@mamamoo"));
    }
}
