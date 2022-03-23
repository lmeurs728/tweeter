package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.service.StatusService;

public class GetFeedHandler implements RequestHandler<GetFeedRequest, FeedResponse> {
    @Override
    public FeedResponse handleRequest(GetFeedRequest input, Context context) {
        StatusService statusService = new StatusService();
        return statusService.getFeed(input);
    }
}
