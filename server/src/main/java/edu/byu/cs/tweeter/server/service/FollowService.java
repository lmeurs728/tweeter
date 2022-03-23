package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.*;
import edu.byu.cs.tweeter.model.net.response.*;
import edu.byu.cs.tweeter.server.dao.FollowDAO;

import java.util.Random;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getFollowDAO().getFollowees(request);
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getFollowDAO().getFollowers(request);
    }

    public FollowResponse follow(FollowRequest request) {
        return new FollowResponse();
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        return new UnfollowResponse();
    }

    public FollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user");
        }
        else if (request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have an auth token");
        }
        return new FollowingCountResponse(getFollowDAO().getFolloweeCount(request.getTargetUser()));
    }

    public FollowersCountResponse getFollowerCount(GetFollowerCountRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user");
        }
        else if (request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have an auth token");
        }
        return new FollowersCountResponse(getFollowDAO().getFollowerCount(request.getTargetUser()));
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowDAO() {
        return new FollowDAO();
    }

    public IsFollowerResponse checkIsFollower(IsFollowerRequest input) {
        return new IsFollowerResponse(new Random().nextInt() > 0);
    }
}
