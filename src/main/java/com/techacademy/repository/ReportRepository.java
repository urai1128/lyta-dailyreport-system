package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Report;
import com.techacademy.entity.Employee;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByEmployee(Employee employee);
    Optional<Report> findByEmployeeAndReportDate(Employee employee, LocalDate reportDate);
    Optional<Report> findByEmployeeAndReportDateAndIdNot(Employee employee, LocalDate reportDate, Integer id);

}