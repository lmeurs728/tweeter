package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.*;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * A DAO for accessing 'following' data from the database.
 */
public interface FollowDAO {

    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param follower the User whose count of how many following is desired.
     * @return said count.
     */
    int getFolloweeCount(String follower);

    int getFollowerCount(String followee);

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    FollowingResponse getFollowees(FollowingRequest request);

    FollowersResponse getFollowers(FollowersRequest request);

    void Follow(FollowRequest request);

    void Unfollow(UnfollowRequest request);

    IsFollowerResponse IsFollower(IsFollowerRequest request);
}
