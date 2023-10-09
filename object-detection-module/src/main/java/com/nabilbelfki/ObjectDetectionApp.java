package com.nabilbelfki;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

public class ObjectDetectionApp implements RequestHandler<Object, Object> {
    private final String s3BucketName = "njit-cs-643"; // Replace with your S3 bucket name
    private final String sqsQueueUrl = "https://sqs.us-east-1.amazonaws.com/966415988081/MyQueue";

    public ObjectDetectionApp() {
    }

    @Override
    public Object handleRequest(final Object input, final Context context) {
        // Create an SQS client to send detected car indexes
        // Initialize S3 client to read images from S3 bucket

        // Initialize the Rekognition client
        RekognitionClient rekognitionClient = RekognitionClient.builder()
                .region(Region.US_EAST_1) // Replace with your desired AWS region
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        // Initialize the S3 client
        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1) // Replace with your desired AWS region
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        // List S3 objects in the bucket
        ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(s3BucketName)
                .build());

        for (S3Object s3Object : listObjectsResponse.contents()) {
            // Download the image from S3 (s3Object.key())

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
                if (label.name().equals("Car")) {
                    // Car detected with confidence > 90%, send the index to SQS
                    // SQS.sendMessage(sqsQueueUrl, s3Object.key());
                }
            }
        }

        // Signal the end of processing by sending a termination message to SQS
        // SQS.sendMessage(sqsQueueUrl, "-1");

        return "Object detection completed.";
    }

    public static void main(String[] args) {
        ObjectDetectionApp app = new ObjectDetectionApp();
        Object inputEvent = "{ \"Records\": [{ \"s3\": { \"bucket\": { \"name\": \"njit-cs-643\" }, \"object\": { \"key\": \"your-object-key\" } } }] }";
        Context context = null; // You can create a mock context for local testing
        Object result = app.handleRequest(inputEvent, context);
        System.out.println("Result: " + result);
    }
}
