package com.devch105.moneymanger.controller;

import com.devch105.moneymanger.dto.ExpenseDTO;
import com.devch105.moneymanger.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense( @RequestBody  ExpenseDTO expenseDTO) {
        ExpenseDTO dto = expenseService.addExpense(expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getMonthExpensesForCurrentUser(){
        List<ExpenseDTO> expenses = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(expenses);
    }

    @DeleteMapping("{expenseId}")
    public ResponseEntity<Void> deleteExpensesForCurrentUser(@PathVariable Long expenseId){
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
