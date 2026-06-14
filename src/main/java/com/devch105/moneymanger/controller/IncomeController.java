package com.devch105.moneymanger.controller;
import com.devch105.moneymanger.dto.IncomeDTO;
import com.devch105.moneymanger.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {
    private final IncomeService incomeService;


    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO incomeDTO) {
        IncomeDTO dto =incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getMonthIncomes(){
        List<IncomeDTO> incomes = incomeService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(incomes);
    }

    @DeleteMapping("{incomeId}")
    public ResponseEntity<Void> deleteExpensesForCurrentUser(@PathVariable Long incomeId){
        incomeService.deleteIncome(incomeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
