package com.placementpro.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "rewards")
public class Reward {

    @Id
    private String id;

    private String title;
    private String description;
    private String type; // resume_review | mock_token | premium_set | badge | certificate
    private int creditCost;
    private String icon;
    private boolean available = true;
    private int totalRedeemed = 0;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

