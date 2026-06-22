package com.devch105.moneymanger.controller;

import com.devch105.moneymanger.entity.ProfileEntity;
import com.devch105.moneymanger.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {
    private final EmailService emailService;
    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final ExcelService excelService;
    private  final ProfileService profileService;

    @GetMapping("/income-excel")
    public ResponseEntity<String> emailIncomeExcel() {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            excelService.writeIncomesToExcel(
                    baos,
                    incomeService.getCurrentMonthIncomesForCurrentUser()
            );

            emailService.sendEmailWithAttachment(
                    profileService.getCurrentProfile().getEmail(),
                    "Income Report",
                    "<h2>Your Income Report</h2>",
                    baos.toByteArray(),
                    "income_report.xlsx"
            );

            return ResponseEntity.ok("Email sent successfully");

        } catch (Exception e) {

            log.error("Failed to send income report", e);

            return ResponseEntity.internalServerError()
                    .body("Failed to send email");
        }
    }





    @GetMapping("/expense-excel")
    public ResponseEntity<String> emailExpenseExcel() {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            excelService.writeExpensesToExcel(
                    baos,
                    expenseService.getCurrentMonthExpensesForCurrentUser()
            );

            emailService.sendEmailWithAttachment(
                    profileService.getCurrentProfile().getEmail(),
                    "Income Report",
                    "<h2>Your Income Report</h2>",
                    baos.toByteArray(),
                    "expense_report.xlsx"
            );

            return ResponseEntity.ok("Email sent successfully");

        } catch (Exception e) {

            log.error("Failed to send income report", e);

            return ResponseEntity.internalServerError()
                    .body("Failed to send email");
        }
    }
}
