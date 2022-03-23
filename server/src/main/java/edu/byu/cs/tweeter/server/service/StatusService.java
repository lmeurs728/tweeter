package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

public class StatusService {
    public FeedResponse getFeed(GetFeedRequest request) {
        if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        else if(request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user");
        }
        return getStatusDAO().getStatuses(request);
    }

    public StoryResponse getStory(GetStoryRequest request) {
        if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        else if(request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user");
        }
        FeedResponse res = getStatusDAO().getStatuses(new GetFeedRequest(request.getAuthToken(), request.getLastStatus(),
                request.getLimit(), request.getTargetUser()));
        return new StoryResponse(res.getStatuses(), res.getHasMorePages());
    }

    StatusDAO getStatusDAO() {
        return new StatusDAO();
    }

    public PostStatusResponse postStatus(PostStatusRequest input) {
        return new PostStatusResponse();
    }
}
