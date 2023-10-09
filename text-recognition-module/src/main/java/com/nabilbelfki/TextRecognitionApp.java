package com.nabilbelfki;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public class TextRecognitionApp implements RequestHandler<Object, Object> {
    private final String sqsQueueUrl = "https://sqs.us-east-1.amazonaws.com/966415988081/MyQueue";

    public TextRecognitionApp() {
        // Initialize the Rekognition client
        RekognitionClient rekognitionClient = RekognitionClient.builder()
                .region(Region.US_EAST_1) // Replace with your desired AWS region
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Override
    public Object handleRequest(final Object input, final Context context) {
        // Create an SQS client to receive indexes from EC2 A
        // Initialize S3 client to read images from S3 bucket

        // Initialize the SQS client
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1) // Replace with your desired AWS region
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        while (true) {
            // Receive messages (image indexes) from SQS
            ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                    .queueUrl(sqsQueueUrl)
                    .maxNumberOfMessages(1)
                    .waitTimeSeconds(20) // Adjust as needed
                    .build());

            for (Message message : receiveMessageResponse.messages()) {
                String index = message.body();

                if (index.equals("-1")) {
                    // Termination signal received, exit the loop and terminate
                    return "Text recognition completed.";
                }

                // Download the image from S3 using the received index
                // Perform text recognition using Rekognition

                // Store the index and recognized text to a file on EBS
            }
        }
    }

    public static void main(String[] args) {
        // This is the entry point when running on EC2 B
        // You can create an AWS Lambda function that uses this class for text
        // recognition
        // Initialize the Lambda function handler and execute it
    }
}
