chmod 400 /Users/nabilbelfki/Instance\ A\ Key.pem
chmod 400 /Users/nabilbelfki/Instance\ B\ Key.pem

For Instance A:
ssh -i "/Users/nabilbelfki/Instance A Key.pem" ec2-user@3.83.248.80
sudo yum install git
sudo yum install java-11-amazon-corretto-devel
sudo amazon-linux-extras install epel
sudo yum install maven
git clone https://github.com/nabilbelfki/image-recognition.git
cd image-recognition
mvn clean install
mvn clean package
java -cp object-detection-module/target/object-detection-module-1.0-SNAPSHOT-jar-with-dependencies.jar com.nabilbelfki.ObjectDetectionApp

For Instance B:
ssh -i "/Users/nabilbelfki/Instance B Key.pem" ec2-user@54.160.205.146
sudo yum install git
sudo yum install java-11-amazon-corretto-devel
sudo amazon-linux-extras install epel
sudo yum install maven
git clone https://github.com/nabilbelfki/image-recognition.git
cd image-recognition
mvn clean install
mvn clean package
java -cp text-recognition-module/target/text-recognition-module-1.0-SNAPSHOT-jar-with-dependencies.jar com.nabilbelfki.TextRecognitionApp
cat output.txt

#docker stuff
sudo yum update -y
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
sudo yum install -y docker
sudo service docker start
sudo chkconfig docker on
sudo usermod -aG docker ec2-user
newgrp docker
docker --version

docker pull nabilbelfki/text-recognition-app:latest
docker pull nabilbelfki/object-detection-app:latest

docker tag object-detection-app nabilbelfki/object-detection-app:object-detection-tag
docker tag text-recognition-app nabilbelfki/text-recognition-app:text-recognition-tag
mkdir .aws

cd .aws

nano credentials

cd ..

docker run -d -e AWS_ACCESS_KEY_ID=$(aws configure get aws_access_key_id) -e AWS_SECRET_ACCESS_KEY=$(aws configure get aws_secret_access_key) -e AWS_DEFAULT_REGION=us-east-1 -e AWS_SESSION_TOKEN=$(aws configure get aws_session_token) nabilbelfki/object-detection-app:latest

mkdir Output
chmod 775 /home/ec2-user/Output

docker run -it -v /home/ec2-user/Output:/app/Output -e AWS_ACCESS_KEY_ID=$(aws configure get aws_access_key_id) -e AWS_SECRET_ACCESS_KEY=$(aws configure get aws_secret_access_key) -e AWS_DEFAULT_REGION=us-east-1 -e AWS_SESSION_TOKEN=$(aws configure get aws_session_token) nabilbelfki/text-recognition-app:latest

docker run -it -v /home/ec2-user/Output:/app/Output -e AWS_ACCESS_KEY_ID=$(aws configure get aws_access_key_id) -e AWS_SECRET_ACCESS_KEY=$(aws configure get aws_secret_access_key) -e AWS_DEFAULT_REGION=us-east-1 -e AWS_SESSION_TOKEN=$(aws configure get aws_session_token) nabilbelfki/text-recognition-app:latest

nano execute.sh
chmod +x execute.sh
./execute.sh
