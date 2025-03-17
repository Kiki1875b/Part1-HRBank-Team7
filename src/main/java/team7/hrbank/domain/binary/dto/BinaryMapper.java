package team7.hrbank.domain.binary.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;
import team7.hrbank.domain.binary.BinaryContent;

import javax.management.ConstructorParameters;
import java.io.IOException;
import java.util.Optional;


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

    @Mapping(target = "bytes", ignore = true)
    BinaryContentDto toDto(BinaryContent binaryContent);

    BinaryContentDto toDto(BinaryContent binaryContent, byte[] bytes);
}

