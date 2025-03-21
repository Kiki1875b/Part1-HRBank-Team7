package team7.hrbank.common.batch.rowmapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import team7.hrbank.common.dto.StatisticChangeLogDto;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.change_log.entity.ChangeLogType;

public class ChangeLogRowMapper implements RowMapper<StatisticChangeLogDto> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public StatisticChangeLogDto mapRow(ResultSet rs, int rowNum) throws SQLException {
    try {
      // JSONB 컬럼을 List<DiffDto>로 변환
      String jsonDetails = rs.getString("details");
      List<DiffDto> detailsList = objectMapper.readValue(jsonDetails, new TypeReference<>() {});

      return new StatisticChangeLogDto(
          rs.getLong("id"),
          ChangeLogType.valueOf(rs.getString("type")),
          detailsList
      );
    } catch (Exception e) {
      throw new SQLException("Error mapping ChangeLog row", e);
    }
  }
}
