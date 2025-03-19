package team7.hrbank.domain.binary.dto;

import java.io.IOException;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;
import team7.hrbank.domain.binary.BinaryContent;


//@MapperConfig(mappingInheritanceStrategy = AUTO_INHERIT_ALL_FROM_CONFIG)
@Mapper
public interface BinaryMapper {

    default Optional<BinaryContentDto> convertFileToBinaryContent(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(new BinaryContentDto(file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    BinaryContent toEntity(BinaryContentDto binaryContentDtoSave);
}

