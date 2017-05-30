package bt_amazon.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

// class upload va ket noi amazons3
public class UploadService {

	//Get a client connection
	public AmazonS3 getAmazonS3()
	{
		//Tạo thông tin xác thực
		AWSCredentials credentials = new BasicAWSCredentials(
				"AKIAJMWH5D4DWW56STMA", //Access key ID
				"xyOAncHvmA+yrnsJQU/nlMiXEXEtd9stkVYzG5b7v"); //Secret access key
				
		// Trả về 1 client kết nối dựa trên thông tin xác thực
		return new AmazonS3Client(credentials);
	}
	//Upload file
	public String upload(File file) throws IOException {
		AmazonS3 client = getAmazonS3();
		
		// Tạo bucket - tên bucket phải là duy nhất và không được có ký tự in hoa
		String bucketName = "nhom14";
		client.createBucket(bucketName);
		
		// tạo folder trong bucket
		String folderName = "edit";
		createFolder(bucketName, folderName, client);
		
		// upload file vào trong folder ms tạo ở trên và đặt ở chế độ public
		String fileKey = folderName + "/" + file.getName();
		client.putObject(new PutObjectRequest(bucketName, fileKey, file.getAbsoluteFile())
				.withCannedAcl(CannedAccessControlList.PublicRead));

		//Trả về đường dẫn của file đã upload
		return "https://s3.amazonaws.com/"+bucketName+"/"+ fileKey;
	}
	//Tạo folder
	private static void createFolder(String bucketName, String folderName, AmazonS3 client) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + "/", emptyContent, metadata);
		// send request to S3 to create folder
		// gui yeu cau tu s3 den tao folder
		client.putObject(putObjectRequest);
	}
	//Delete file
	public void deleteFile(String url) {
		String[] arr = url.split("/");
		String bucketName = arr[3];
		String folderName = arr[4];
		String fileName = arr[5];
		String fileKey = folderName + "/" + fileName;
		AmazonS3 client = getAmazonS3();
		client.deleteObject(bucketName, fileKey);
	}
}
