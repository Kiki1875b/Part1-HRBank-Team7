package team7.hrbank.domain.change_log.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DiffDto(
    @JsonProperty("propertyName") String propertyName,
    @JsonProperty("before") String before,
    @JsonProperty("after") String after
) {
}
