package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.QueueFollowersRequest;
import edu.byu.cs.tweeter.model.net.response.QueueFollowersResponse;
import edu.byu.cs.tweeter.server.dao.IQueueService;

public class QueueServiceSQS implements IQueueService {
    @Override
    public boolean addStatusToQueue(PostStatusRequest request) {
        Gson gson = new Gson();
        String json = gson.toJson(request);
        String messageBody = json;
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/382846445826/PostStatusQueue";

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(messageBody);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);

        System.out.println("Status added to SQS Queue PostStatusQueue: " + send_msg_result.toString());
        return true;
    }

    @Override
    public QueueFollowersResponse addFollowersToQueue(QueueFollowersRequest queueFollowersRequest) {
        Gson gson = new Gson();
        String json = gson.toJson(queueFollowersRequest);
        String messageBody = json;
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/382846445826/UpdateFeedForFollowersQueue";

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(messageBody);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);

        System.out.println("Status added to SQS Queue UpdateFeedForFollowersQueue: " + send_msg_result.toString());
        return new QueueFollowersResponse(true);
    }
}
