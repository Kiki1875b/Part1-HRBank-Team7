package team7.hrbank.domain.binary.dto;

public record BinaryContentDto(
        String fileName,
        String fileType,
        Long fileSize,
        byte[] bytes) {
}
