package com.nabilbelfki;

import java.time.Instant;
import java.util.UUID;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class ObjectDetectionApp {

    private static final String s3BucketName = "njit-cs-643";
    private static final String sqsQueueUrl = "https://sqs.us-east-1.amazonaws.com/966415988081/ImageQueuePipeline.fifo";

    public static void main(String[] args) {

        // Initialize the Rekognition client
        RekognitionClient rekognitionClient = RekognitionClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // Initialize the S3 client
        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();

        // Initialize the SQS client
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // List S3 objects in the bucket
        ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(s3BucketName)
                .build());

        for (S3Object s3Object : listObjectsResponse.contents()) {

            // Generate a unique identifier using timestamp and a random UUID
            String uniqueId = Instant.now().toString() + "-" + UUID.randomUUID().toString();
            String groupUniqueId = Instant.now().toString() + "-" + UUID.randomUUID().toString();

            // Create an S3Object from the AWS SDK S3 model
            software.amazon.awssdk.services.rekognition.model.S3Object rekognitionS3Object = software.amazon.awssdk.services.rekognition.model.S3Object
                    .builder()
                    .bucket(s3BucketName)
                    .name(s3Object.key())
                    .build();

            // Perform object detection using Rekognition
            DetectLabelsResponse detectLabelsResponse = rekognitionClient.detectLabels(DetectLabelsRequest.builder()
                    .image(Image.builder()
                            .s3Object(rekognitionS3Object)
                            .build())
                    .maxLabels(10)
                    .minConfidence(90F)
                    .build());

            for (Label label : detectLabelsResponse.labels()) {
                System.out.println("Label: " + label.name() + ", Confidence: " + label.confidence());

                if (label.name().equals("Car")) {
                    // See the key
                    System.out.println("Key: " + s3Object.key());

                    // Car detected with confidence > 90%, send the index to SQS
                    SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                            .queueUrl(sqsQueueUrl)
                            .messageBody(s3Object.key())
                            .messageGroupId(groupUniqueId)
                            .messageDeduplicationId(uniqueId)
                            .build();
                    sqsClient.sendMessage(sendMessageRequest);
                }
            }
        }

        // Signal the end of processing by sending a termination message to SQS
        SendMessageRequest terminationMessage = SendMessageRequest.builder()
                .queueUrl(sqsQueueUrl)
                .messageBody("-1")
                .messageGroupId("Cars")
                .messageDeduplicationId("termination")
                .build();
        sqsClient.sendMessage(terminationMessage);

        System.out.println("Object detection completed.");
    }
}
