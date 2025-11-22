package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;
import java.util.List;
import com.techacademy.constants.ErrorKinds;


@Controller
@RequestMapping("reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // 日報一覧画面
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetail userDetail) {

        // ログイン中ユーザーのEmployee取得
        Employee loginUser = userDetail.getEmployee();

        List<Report> list;

        // 管理者なら全件、一般なら自分のだけ
        if (loginUser.getRole() == Employee.Role.ADMIN) {
            list = reportService.findAll();
        } else {
            list = reportService.findByEmployee(loginUser);
        }

        model.addAttribute("reportList", list);
        model.addAttribute("listSize", list.size());

        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report) {
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res,
                      @AuthenticationPrincipal UserDetail userDetail, Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            return "reports/new";
        }

        // ログイン従業員をセット
        report.setEmployee(userDetail.getEmployee());

        ErrorKinds result = reportService.save(report);

        if (result == ErrorKinds.DUPLICATE_ERROR) {
            model.addAttribute("errorMessage", "既に登録されている日付です");
            return "reports/new";
        }
        reportService.save(report);


        return "redirect:/reports";
    }

    // 日報更新画面の表示
    @GetMapping(value = "/{id}/update")
    public String edit(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("report", reportService.findById(id));
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping(value = "/update")
    public String update(@Validated Report report, BindingResult res,
                         @AuthenticationPrincipal UserDetail userDetail, Model model) {

        if (res.hasErrors()) {
            return "reports/update";
        }

        report.setEmployee(userDetail.getEmployee());

        ErrorKinds result = reportService.update(report);

        if (result == ErrorKinds.DUPLICATE_ERROR) {
            model.addAttribute("errorMessage", "既に登録されている日付です");
            return "reports/update";
        }

        return "redirect:/reports";
    }


    // 日報削除処理（論理削除）
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") Integer id) {
        reportService.delete(id);
        return "redirect:/reports";
    }
}
