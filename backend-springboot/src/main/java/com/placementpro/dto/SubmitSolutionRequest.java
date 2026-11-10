package com.placementpro.dto;

import lombok.Data;
import java.util.List;

@Data
public class SubmitSolutionRequest {
    private String code;
    private String language;
    private String status; // Accepted | Wrong Answer | TLE | MLE | Runtime Error
    private Long runtime;
    private Long memory;
    private String stdin;
}

