package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowingCountHandler implements RequestHandler<GetFollowingCountRequest, FollowingCountResponse> {
    @Override
    public FollowingCountResponse handleRequest(GetFollowingCountRequest input, Context context) {
        return new FollowService().getFollowingCount(input);
    }
}
