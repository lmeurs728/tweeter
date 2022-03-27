package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.AuthDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

public class StatusService extends BaseService {
    public FeedResponse getFeed(GetFeedRequest request) {
        if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        else if(request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user");
        }

        if (getAuthDAO().verifyAuthToken(request.getAuthToken())) {
            return getStatusDAO().getFeed(request);
        }
        return new FeedResponse("Bad AuthToken");
    }

    public StoryResponse getStory(GetStoryRequest request) {
        if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        else if(request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user");
        }

        if (getAuthDAO().verifyAuthToken(request.getAuthToken())) {
            return getStatusDAO().getStory(request);
        }
        return new StoryResponse("Bad AuthToken");
    }

    public PostStatusResponse postStatus(PostStatusRequest input) {
        if (input.getStatus() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a status");
        }

        if (getAuthDAO().verifyAuthToken(input.getAuthToken())) {
            getStatusDAO().PostStatus(input);
            return new PostStatusResponse();
        }
        return new PostStatusResponse("Bad AuthToken");
    }

    public AuthDAO getAuthDAO() {
        return factory.getAuthDAO();
    }

    StatusDAO getStatusDAO() {
        return factory.getStatusDAO();
    }
}
