//package reservation.Controller;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import reservation.Service.S3Service;
//
//@RestController
//@RequestMapping("/api/s3")
//public class FileUploadController {
//	@Autowired
//	private S3Service s3Service;
//	
//	@PostMapping("/upload")
//	public String upload(@RequestParam("file") MultipartFile multipartFile ) throws IOException {
//		File file = File.createTempFile("upload-", multipartFile.getOriginalFilename());
//		multipartFile.transferTo(file);
//		
//		s3Service.uploadFile("my-bookwise-app", multipartFile.getOriginalFilename(), file);
//		
//		return "Uploaded";
//	}
//	
//	
//}
