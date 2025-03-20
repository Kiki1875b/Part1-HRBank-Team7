package team7.hrbank.common.dto;

import java.util.List;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.change_log.entity.ChangeLogType;

public record StatisticChangeLogDto(
    Long id,
    ChangeLogType type,
    List<DiffDto> details
) {

}
