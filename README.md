chmod 400 /Users/nabilbelfki/Instance\ A\ Key.pem
chmod 400 /Users/nabilbelfki/Instance\ B\ Key.pem

ssh -i "/Users/nabilbelfki/Instance A Key.pem" ec2-user@3.88.58.49
sudo yum install git
sudo yum install java-11-amazon-corretto-devel
sudo amazon-linux-extras install epel
sudo yum install maven
git clone https://github.com/nabilbelfki/image-recognition.git
cd ~/image-recognition/object-detection-module
java -cp target/object-detection-module-1.0-SNAPSHOT.jar com.nabilbelfki.ObjectDetectionApp

ssh -i "/Users/nabilbelfki/Instance B Key.pem" ec2-user@54.160.205.146
sudo yum install git
sudo yum install java-11-amazon-corretto-devel
sudo amazon-linux-extras install epel
sudo yum install maven
git clone https://github.com/nabilbelfki/image-recognition.git
cd ~/image-recognition/text-recognition-module
java -cp target/text-recognition-module-1.0-SNAPSHOT.jar com.nabilbelfki.TextRecognitionApp
