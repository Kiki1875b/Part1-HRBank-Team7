package team7.hrbank.domain.backup.mapper;


import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.entity.Backup;

@Mapper
public interface BackupMapper {

  @Mapping(target = "fileId", source = "file.id")
  BackupDto fromEntity(Backup backup);
  List<BackupDto> fromEntityList(List<Backup> backups);
}
