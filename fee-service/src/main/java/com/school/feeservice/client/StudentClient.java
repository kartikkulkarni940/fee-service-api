package com.school.feeservice.client;

import com.school.feeservice.dto.StudentClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "student-service", url = "http://localhost:8081/api/students")
public interface StudentClient {

    @GetMapping("/{studentId}")
    StudentClientResponse getByStudentId(@PathVariable("studentId") String studentId);
}
