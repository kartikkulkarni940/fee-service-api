package com.school.feeservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentClientResponse {
    private String studentId;
    private String name;
    private String grade;
    private String schoolName;
}
