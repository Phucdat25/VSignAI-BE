package com.vsignai.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AiPredictResponse {

    private String status;

    @JsonProperty("total_frames")
    private Integer totalFrames;

    private List<AiWindowResponse> windows;

    @JsonProperty("final_sentence")
    private String finalSentence;
}