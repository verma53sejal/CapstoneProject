package com.placementpro.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "credits")
public class Credit {

    @Id
    private String id;

    private String user; // user ID
    private int amount;
    private String type; // earn | spend
    private String reason;
    private Integer balance;
    private String reference;

    @CreatedDate
    private Instant createdAt;
}

