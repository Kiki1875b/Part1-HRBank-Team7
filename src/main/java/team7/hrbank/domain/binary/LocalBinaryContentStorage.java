package team7.hrbank.domain.binary;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import team7.hrbank.domain.employee.entity.Employee;

@Repository
public class LocalBinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(@Value("${hrbank.storage.local.root-path}") String root) {
        this.root = Paths.get(root);
    }

    public void put(byte[] content, Long id, String fileType) {
        try {
            Path savedPath = resolvePath(id, fileType);
            Files.createFile(savedPath);
            Files.write(savedPath, content);
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    public ResponseEntity<Resource> downloadTmp(Long id, String fileType){
        Path filePath = resolvePath(id, fileType);

        if (!Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            InputStream inputStream = Files.newInputStream(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            long fileSize = Files.size(filePath);
            String fileName = filePath.getFileName().toString();
            Resource resource = new InputStreamResource(inputStream);

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /*========================== 여기까지 지우셔도 됩니다 =======================================*/

    public void backUpEmployeeToCsv(List<Employee> employeeList){

    }
    /**
     * 편의
     */
    private Path resolvePath(Long id, String fileType) {
        return root.resolve(id.toString() + "." + fileType);
    }
}
