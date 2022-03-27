package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.*;
import edu.byu.cs.tweeter.model.net.response.*;
import edu.byu.cs.tweeter.server.dao.AuthDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends BaseService {

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

        if (getAuthDAO().verifyAuthToken(request.getAuthToken())) {
            return getFollowDAO().getFollowees(request);
        }
        return new FollowingResponse("Authtoken invalid");
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }

        if (getAuthDAO().verifyAuthToken(request.getAuthToken())) {
            return getFollowDAO().getFollowers(request);
        }
        return new FollowersResponse("Authtoken invalid");
    }

    public FollowResponse follow(FollowRequest request) {
        if (getAuthDAO().verifyAuthToken(request.getAuthToken())) {
            getFollowDAO().Follow(request);
            return new FollowResponse();
        }
        return new FollowResponse("Authtoken invalid");
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if (getAuthDAO().verifyAuthToken(request.getAuthToken())) {
            getFollowDAO().Unfollow(request);
            return new UnfollowResponse();
        }
        return new UnfollowResponse("Authtoken invalid");
    }

    public FollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user");
        }
        else if (request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have an auth token");
        }
        if (getAuthDAO().verifyAuthToken(request.getAuthToken())) {
            return new FollowingCountResponse(getFollowDAO().getFolloweeCount(request.getTargetUser().getAlias()));
        }
        return new FollowingCountResponse("Authtoken invalid");
    }

    public FollowersCountResponse getFollowerCount(GetFollowerCountRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user");
        }
        else if (request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have an auth token");
        }

        if (getAuthDAO().verifyAuthToken(request.getAuthToken())) {
            return new FollowersCountResponse(getFollowDAO().getFollowerCount(request.getTargetUser().getAlias()));
        }
        return new FollowersCountResponse("Authtoken invalid");
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowDAO() {
        return factory.getFollowDAO();
    }

    public AuthDAO getAuthDAO() {
        return factory.getAuthDAO();
    }

    public IsFollowerResponse checkIsFollower(IsFollowerRequest input) {
        return getFollowDAO().IsFollower(input);
    }
}
