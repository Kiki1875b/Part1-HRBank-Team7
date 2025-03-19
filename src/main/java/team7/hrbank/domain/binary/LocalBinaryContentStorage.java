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
        init();// 임시로
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

    public ResponseEntity<Resource> download(Long id, String fileType) {
        Path profilePath = resolvePath(id, fileType);
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_DISPOSITION, "attachment; filename= "+ profilePath.getFileName());
        headers.add(CONTENT_TYPE, fileType);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new FileSystemResource(profilePath));
    }


    // 임시 메서드 - 요한님 나중에 동작하는 기능 만드시면 지워도 됩니다
    public InputStream get(Path path) {
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    public ResponseEntity<Resource> downloadTmp(Long id, String fileType){
        Path filePath = resolvePath(id, fileType);

        if (!Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            InputStream inputStream = get(filePath);
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
        //String realFileType = fileType.split("/")[1];
        return root.resolve(id.toString() + "." + fileType);
    }

    //루트 디렉토리를 초기화합니다.
    void init() {
        if (!Files.exists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (Stream<Path> list = Files.list(root)) {
                list.forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
