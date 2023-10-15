package com.nabilbelfki;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.io.FileWriter;
import java.io.IOException;

public class TextRecognitionApp {

    private static final String sqsQueueUrl = "https://sqs.us-east-1.amazonaws.com/966415988081/ImageQueuePipeline.fifo";
    private static final String s3BucketName = "njit-cs-643";
    private static final String ebsFilePath = "results.txt";

    public static void main(String[] args) {

        // Initializing the Rekognition client
        RekognitionClient rekognitionClient = RekognitionClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // Initializing the SQS client
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        try (FileWriter fileWriter = new FileWriter(ebsFilePath)) {
            while (true) {
                // Receive messages (image indexes) from SQS
                ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                        .queueUrl(sqsQueueUrl)
                        .maxNumberOfMessages(1)
                        .waitTimeSeconds(20)
                        .build();
                ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);

                for (Message message : receiveMessageResponse.messages()) {
                    String index = message.body();

                    // Print the image index
                    System.out.println("Processing image index: " + index);

                    if (index.equals("-1")) {
                        // Termination signal received, exit the loop and terminate
                        fileWriter.close();
                        System.out.println("Text recognition completed.");
                        return;
                    }

                    // Perform text recognition using Rekognition
                    DetectTextRequest detectTextRequest = DetectTextRequest.builder()
                            .image(Image.builder()
                                    .s3Object(
                                            S3Object.builder()
                                                    .bucket(s3BucketName)
                                                    .name(index)
                                                    .build())
                                    .build())
                            .build();
                    DetectTextResponse detectTextResponse = rekognitionClient.detectText(detectTextRequest);

                    fileWriter.write(index);

                    // Store the index and recognized text to the EBS file
                    for (TextDetection textDetection : detectTextResponse.textDetections()) {
                        String detectedText = textDetection.detectedText();
                        fileWriter.write(" " + detectedText);
                    }

                    fileWriter.write("\n");

                    // Delete the processed message from the SQS queue
                    sqsClient.deleteMessage(DeleteMessageRequest.builder()
                            .queueUrl(sqsQueueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Text recognition failed with an error: " + e.getMessage());
        }
    }
}
