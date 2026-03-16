package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    private List<Long> playerIds;
    private int cost; // Points to deduct (e.g. 4, 8, 12...)
}
