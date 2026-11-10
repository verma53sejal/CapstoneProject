package com.placementpro.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "redemptions")
public class Redemption {

    @Id
    private String id;

    private String user;   // user ID
    private String reward; // reward ID
    private Integer creditsSpent;
    private Instant redeemedAt = Instant.now();
    private String status = "pending"; // pending | fulfilled

    @CreatedDate
    private Instant createdAt;
}

