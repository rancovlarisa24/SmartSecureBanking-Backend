package com.lrs.SSB.controller;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInsuranceDTO {
    private Integer userId;
    private Long insuranceId;
    private Integer cardId;
    private String extraDetails;
}
