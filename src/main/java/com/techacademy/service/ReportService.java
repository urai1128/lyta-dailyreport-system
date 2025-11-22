
package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Report;
import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 日報　一覧表示　一般用
    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }

      // 1件を検索する
    public Report findById(Integer id) {
        Optional<Report> option = reportRepository.findById(id);
        Report report = option.orElse(null);
        return report;
    }

    //新規登録
    @Transactional
    public ErrorKinds save(Report report) {

        // 同じ従業員 同じ日付のデータがあるか検索するメソッド
        Optional<Report> existing = reportRepository
                .findByEmployeeAndReportDate(report.getEmployee(), report.getReportDate());

        if (existing.isPresent()) {
            // すでに登録済みを、エラーにする
            return ErrorKinds.DUPLICATE_ERROR;
        }

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        report.setDeleteFlg(false);

        reportRepository.save(report);

        return ErrorKinds.SUCCESS;
    }





    // 日報　削除
    @Transactional
    public void delete(Integer id) {
        Report report = findById(id);
        if (report != null) {
            LocalDateTime now = LocalDateTime.now();
            report.setUpdatedAt(now);
            report.setDeleteFlg(true);
            reportRepository.save(report);
        }
    }





 // 更新処理
    @Transactional
    public ErrorKinds update(Report report) {


        Report existingReport = findById(report.getId());


        Optional<Report> duplicate = reportRepository.findByEmployeeAndReportDateAndIdNot(
                        report.getEmployee(),
                        report.getReportDate(),
                        report.getId()                                           );

        if (duplicate.isPresent()) {
            return ErrorKinds.DUPLICATE_ERROR;
        }

        report.setCreatedAt(existingReport.getCreatedAt());
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(existingReport.isDeleteFlg());

        reportRepository.save(report);

        return ErrorKinds.SUCCESS;
    }
}
