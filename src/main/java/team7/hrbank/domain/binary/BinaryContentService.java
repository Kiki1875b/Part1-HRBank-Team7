package team7.hrbank.domain.binary;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team7.hrbank.common.exception.binaryContent.BinaryCustomException;
import team7.hrbank.common.exception.binaryContent.ErrorCode;
import team7.hrbank.domain.binary.dto.BinaryContentDto;
import team7.hrbank.domain.binary.dto.BinaryMapper;

import static team7.hrbank.common.exception.binaryContent.ErrorCode.*;

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
            throw new BinaryCustomException(NOT_ALLOWED_FILE_TYPE);
        }
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryMapper.toEntity(dto));
        localBinaryContentStorage.put(dto.bytes(), savedBinaryContent.getId(), dto.fileType().split("/")[1]);
        return savedBinaryContent;
    }

    public String findFileTypeById(Long id) {
        return binaryContentRepository.findById(id)
                .map((binaryContent) -> binaryContent.getFileType().split("/")[1])
                .orElseThrow(() -> new BinaryCustomException(NO_SUCH_BINARY_CONTENT));
    }
}
