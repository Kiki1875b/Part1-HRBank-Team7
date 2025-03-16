package team7.hrbank.binary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team7.hrbank.domain.binary.*;
import team7.hrbank.domain.binary.dto.BinaryContentDto;
import team7.hrbank.domain.binary.dto.BinaryMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class basicBinary {


    @Autowired
    private BinaryContentService binaryContentService;
    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private LocalBinaryContentStorage localBinaryContentStorage;

    @Autowired
    private BinaryMapper binaryMapper;

    @Transactional
    @Test
    void save(){
        // given
        byte[] testBytes = "test".getBytes();
        BinaryContentDto binaryContentDto = new BinaryContentDto("test.txt", "text/plain", 4L, testBytes);
        System.out.println("binaryContentDto는 = " + binaryContentDto);

        // when
        BinaryContent savedBinaryContent = binaryContentService.save(binaryContentDto);
        Optional<BinaryContent> binaryContent = binaryContentRepository.findById(savedBinaryContent.getId());

        // then
        assertThat(binaryContent).isPresent();
        assertThat(savedBinaryContent.getFileName()).isEqualTo("test.txt");
        assertThat(savedBinaryContent.getFileType()).isEqualTo("text/plain");
        assertThat(savedBinaryContent.getFileSize()).isEqualTo(4L);
    }

    // 다운로드는 추가로 employ, department랑 엮여서 나중에

    //        public record BinaryContentDto(
//                String fileName,
//                String fileType,
//                Long fileSize,
//                byte[] bytes)
}
