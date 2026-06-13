package com.vsignai.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AiPredictResponse {

    private String status;

    @JsonProperty("predicted_gloss")
    private String predictedGloss;

    private Double confidence;

    @JsonProperty("total_processed_frames")
    private Integer totalProcessedFrames;

    @JsonProperty("total_windows_checked")
    private Integer totalWindowsChecked;

    private String message;
}