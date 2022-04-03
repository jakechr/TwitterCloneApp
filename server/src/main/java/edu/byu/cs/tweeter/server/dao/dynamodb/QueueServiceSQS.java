package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.server.dao.IQueueService;

public class QueueServiceSQS implements IQueueService {
    @Override
    public boolean addStatusToQueue(PostStatusRequest request) {
        String messageBody = "Test";
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/382846445826/In-Class_Exercise";

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(messageBody);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);

        String msgId = send_msg_result.getMessageId();
        System.out.println("Message ID: " + msgId);
        return true;
    }
}
