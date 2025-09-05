package ru.practicum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatDto {

    @JsonProperty("app")
    private String getApp;

    @JsonProperty("uri")
    private String getUri;

    @JsonProperty("hits")
    private Long getHits;
}
