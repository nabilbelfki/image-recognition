# Image Recognition Project

This project is designed to perform image recognition tasks using Amazon Corretto, Git, Java, Maven, and AWS services. It consists of two main components: Object Detection and Text Recognition.

## Prerequisites

Before you begin, ensure you have the following requirements:

- An Amazon EC2 instance with SSH access
- AWS CLI configured with the necessary credentials

## Setup

### Step 1: Connect to the EC2 Instance

Use SSH to connect to your EC2 instance:

```bash
ssh -i "/path/to/your/InstanceAKey.pem" ec2-user@your-instance-ip
```

### Step 2: Install Required Software

Install Git, Java, Maven, and EPEL repository on your EC2 instance:

```bash
sudo yum install git
sudo yum install java-11-amazon-corretto-devel
sudo amazon-linux-extras install epel
sudo yum install maven
```

### Step 3: Clone the Repository

Clone the image-recognition repository:

```bash
git clone https://github.com/nabilbelfki/image-recognition.git
cd image-recognition
```

### Step 4: Build the Project

Build the application using Maven:

```bash
mvn clean install
mvn clean package
```

## Object Detection (Instance A)

To perform object detection, use the following command:

```bash
java -cp object-detection-module/target/object-detection-module-1.0-SNAPSHOT-jar-with-dependencies.jar com.nabilbelfki.ObjectDetectionApp
```

## Text Recognition (Instance B)

To perform text recognition, use the following command:

```bash
java -cp text-recognition-module/target/text-recognition-module-1.0-SNAPSHOT-jar-with-dependencies.jar com.nabilbelfki.TextRecognitionApp
```

## Viewing Output

You can view the output of the Text Recognition component by running:

```bash
cat output.txt
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

Special thanks to Amazon Web Services for their services and support.
