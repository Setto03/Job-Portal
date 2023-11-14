package project.job_portal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.job_portal.model.FileEntity;
import project.job_portal.model.User;
import project.job_portal.repository.FileRepository;
import project.job_portal.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    private final UserRepository userRepository;

    private static final String directory = "D:/Test";

    private String saveFileToServer(MultipartFile file, User user) throws IOException {
        String fileName = user.getId() + "_" + file.getOriginalFilename();

        // Build path đến file
        Path filePath = Paths.get(directory, fileName);

        // Thực hiện xóa file cũ trước khi ghi file mới
        Files.deleteIfExists(filePath);
        Files.write(filePath, file.getBytes());

        return filePath.toString();
    }

    public void saveFile(MultipartFile file, int id) throws IOException {
        User user = userRepository.findUserById(id);
        String fileName = user.getId() + "_" + file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        String extension = "";

        // Lấy tất cả phần đằng sau dấu . (file extension)
        if (fileName.lastIndexOf(".") > 0) {
            extension = fileName.substring(index + 1);
        }

        String filePath = saveFileToServer(file, user);

        var newFile = FileEntity.builder()
                .name(fileName)
                .user(user)
                .path(filePath)
                .extension(extension)
                .build();

        // Lưu thông tin file và path trong database
        // Thay file trong trường hợp file đã tồn tại
        try {
            fileRepository.save(newFile);
        } catch (Exception e) {
            FileEntity fileEntity = getFileByUser(user);

            fileEntity.setPath(newFile.getPath());
            fileEntity.setName(newFile.getName());
            fileEntity.setExtension(extension);

            fileRepository.save(fileEntity);
        }
    }

    public FileEntity getFileByUser(User user) {
        return fileRepository.getFileEntityByUserId(user.getId());
    }

    public String getDirectory() {
        return directory;
    }
}
