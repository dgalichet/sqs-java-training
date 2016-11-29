package com.scaleo;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

import java.util.List;

public class SQSConsumer {

    private static AmazonSQS getSQS() {
        AWSCredentials credentials = null;
        try {
            credentials = InstanceProfileCredentialsProvider.getInstance().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }

        AmazonSQS sqs = new AmazonSQSClient(credentials);
        Region euWest1 = Region.getRegion(Regions.EU_WEST_1);
        sqs.setRegion(euWest1);
        return sqs;
    }

    private static void deleteMessage(AmazonSQS sqs, String queueUrl, Message message) {
        String receiptHandle = message.getReceiptHandle();
        sqs.deleteMessage(queueUrl, receiptHandle);
    }

    private static void loadMessages(AmazonSQS sqs, String queueUrl) {
        ReceiveMessageResult receiveMessageResult = sqs.receiveMessage(queueUrl);
        List<Message> messages = receiveMessageResult.getMessages();
        for (Message message: messages) {
            System.out.println("Received message : " + message.getBody());
            deleteMessage(sqs, queueUrl, message);
        }
    }

    public static void main(String[] args) {

        String queueName = null;

        if (args.length == 1) {
            queueName = args[0];
        }

        AmazonSQS sqs = getSQS();
        GetQueueUrlResult result = sqs.getQueueUrl(queueName);
        final String queueUrl = result.getQueueUrl();

        System.out.println("Start consuming messages on " + queueName);
        while (true) {
            loadMessages(sqs, queueUrl);
        }
    }

}
