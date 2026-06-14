package com.devch105.moneymanger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {
    private Long id;
    private String name;
    private String icon;
    private BigDecimal amount;
    private Long categoryId;
    private String categoryName;
    private LocalDate  date;
    private Instant createdAt;
    private Instant updatedAt;
}
