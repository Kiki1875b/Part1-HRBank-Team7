package team7.hrbank.domain.binary;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team7.hrbank.domain.binary.dto.BinaryContentDto;
import team7.hrbank.domain.binary.dto.BinaryMapper;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final LocalBinaryContentStorage localBinaryContentStorage;
    private final BinaryMapper binaryMapper;
    private static final List<String> ALLOWED_FILE_TYPES = List.of("image/jpeg", "image/png", "image/gif");

    public BinaryContent save(BinaryContentDto dto) {
        if (!ALLOWED_FILE_TYPES.contains(dto.fileType())) {
            throw new RuntimeException("허용하지 않는 파일 타입입니다: " + dto.fileType());
        }
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryMapper.toEntity(dto));
        localBinaryContentStorage.put(dto.bytes(), savedBinaryContent.getId(), dto.fileType().split("/")[1]);
        return savedBinaryContent;
    }

    public String findFileTypeById(Long id) {
        return binaryContentRepository.findById(id)
                .map((binaryContent) -> binaryContent.getFileType().split("/")[1])
                .orElseThrow(() -> new IllegalArgumentException("Binary content not found with id: " + id));
    }
}
