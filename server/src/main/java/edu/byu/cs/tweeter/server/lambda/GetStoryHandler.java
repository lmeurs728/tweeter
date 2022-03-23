package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.service.StatusService;

public class GetStoryHandler implements RequestHandler<GetStoryRequest, StoryResponse> {
    @Override
    public StoryResponse handleRequest(GetStoryRequest input, Context context) {
        return new StatusService().getStory(input);
    }
}
