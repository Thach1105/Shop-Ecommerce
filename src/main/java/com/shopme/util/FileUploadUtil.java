package com.shopme.util;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {

        //lấy đường dẫn thư mục upload
        Path uploadPath = Paths.get(uploadDir);

        //kiểm tra xem thư mục upload đã tổn tại chưa, nếu chưa tạo thư mục
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        // tạo một path cho đường dẫn đến tập tin cần lưu và lưu hoặc ghi đè bằng file được upload lên
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Không thể lưu file: "+ fileName, e);
        }
    }

    public static void cleanDir(String uploadDir){
        Path pathDir = Paths.get(uploadDir);
        try {
            Files.list(pathDir).forEach(
                    file -> {
                        if(!Files.isDirectory(file)){
                            try {
                                Files.delete(file);
                            } catch (IOException e){
                                System.out.println("Could not delete file: " + file);
                            }
                        }
                    }
            );
        } catch (IOException e)  {
            System.out.println("Could not list directory: "+ pathDir);
        }
    }

    public static void deleteDir(String dir) throws IOException {
        File directory = new File(dir);
        // Sử dụng FileUtils để xóa thư mục cùng tất cả các tệp con
        if (directory.exists()) FileUtils.deleteDirectory(directory);

    }

}
