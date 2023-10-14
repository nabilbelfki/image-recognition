package com.nabilbelfki;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.io.FileWriter;
import java.io.IOException;

public class TextRecognitionApp {

    private static final String sqsQueueUrl = "YOUR_SQS_QUEUE_URL";
    private static final String s3BucketName = "njit-cs-643";
    private static final String ebsFilePath = "/path/to/your/ebs/folder/results.txt";

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
                                                    .name(index + ".jpg")
                                                    .build())
                                    .build())
                            .build();
                    DetectTextResponse detectTextResponse = rekognitionClient.detectText(detectTextRequest);

                    // Store the index and recognized text to the EBS file
                    for (TextDetection textDetection : detectTextResponse.textDetections()) {
                        String detectedText = textDetection.detectedText();
                        fileWriter.write("Image Index: " + index + "\n");
                        fileWriter.write("Detected Text: " + detectedText + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Text recognition failed with an error: " + e.getMessage());
        }
    }
}
