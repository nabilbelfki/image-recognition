# Use the Amazon Corretto base image
FROM amazoncorretto:11

# Set the working directory
WORKDIR /app

# Install required tools
RUN yum -y install git
RUN amazon-linux-extras install epel
RUN yum -y install maven

# Clone the repository
RUN git clone https://github.com/nabilbelfki/image-recognition.git

# Build the application
WORKDIR /app/image-recognition/object-detection-module
RUN mvn clean install
RUN mvn clean package

# Set the entry point
CMD ["java", "-cp", "target/object-detection-module-1.0-SNAPSHOT-jar-with-dependencies.jar", "com.nabilbelfki.ObjectDetectionApp"]
