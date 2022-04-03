package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostStatusGetFollowersHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {
    @Override
    public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {
        StatusService statusService = new StatusService(new DynamoDAOFactory());
        return statusService.postStatus(request);
    }
}