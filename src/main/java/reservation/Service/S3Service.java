package reservation.Service;

import java.io.File;

import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Service
public class S3Service {
	private final AmazonS3 s3;
	
	public S3Service() {
		this.s3 = AmazonS3ClientBuilder.standard()
				.withRegion(Regions.AP_SOUTHEAST_2)
				.build();
	}
	
	public void uploadFile(String bucketName, String key, File file) {
		s3.putObject(bucketName, key, file);
	}
	
}
