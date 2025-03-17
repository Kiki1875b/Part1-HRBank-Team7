package team7.hrbank.mapper;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.entity.Backup;
import team7.hrbank.domain.backup.entity.BackupStatus;
import team7.hrbank.domain.backup.mapper.BackupMapper;
import team7.hrbank.domain.binary.BinaryContent;

@SpringBootTest
public class BackupMapperTest {

  @Autowired
  private BackupMapper backupMapper;

  @Test
  void EntityToDTOMappingTest(){
    BinaryContent content = new BinaryContent("test", "testType", 1L);
    Backup backup = new Backup(1L, content, "worker", Instant.EPOCH, Instant.EPOCH, BackupStatus.COMPLETED);

    BackupDto dto = backupMapper.fromEntity(backup);

    assertThat(dto).isNotNull();
    assertThat(dto.id()).isEqualTo(1L);
    assertThat(dto.worker()).isEqualTo("worker");
    assertThat(dto.startedAt()).isEqualTo(Instant.EPOCH);
    assertThat(dto.endedAt()).isEqualTo(Instant.EPOCH);
    assertThat(dto.status()).isEqualTo(BackupStatus.COMPLETED);
  }
}
