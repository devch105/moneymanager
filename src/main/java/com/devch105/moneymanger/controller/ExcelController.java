package com.devch105.moneymanger.controller;

import com.devch105.moneymanger.service.ExcelService;
import com.devch105.moneymanger.service.ExpenseService;
import com.devch105.moneymanger.service.IncomeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor

public class ExcelController {

    private final ExcelService excelService;
    private final IncomeService incomeService;
    private  final ExpenseService expenseService;



    @GetMapping("/download/income")
    public void downloadIncomeExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=income.xlsx");

        excelService.writeIncomesToExcel(response.getOutputStream(), incomeService.getCurrentMonthExpensesForCurrentUser());
    }

    @GetMapping("/download/expense")
    public void downloadExpenseExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=expense.xlsx");

        excelService.writeExpensesToExcel(response.getOutputStream(), expenseService.getCurrentMonthExpensesForCurrentUser());
    }
}
