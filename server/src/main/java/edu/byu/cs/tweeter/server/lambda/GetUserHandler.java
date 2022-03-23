package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.service.UserService;

public class GetUserHandler implements RequestHandler<GetUserRequest, UserResponse> {

    @Override
    public UserResponse handleRequest(GetUserRequest input, Context context) {
        return new UserService().getUser(input);
    }
}
