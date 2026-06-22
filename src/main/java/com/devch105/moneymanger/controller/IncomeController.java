package com.devch105.moneymanger.controller;
import com.devch105.moneymanger.dto.IncomeDTO;
import com.devch105.moneymanger.service.IncomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
@Slf4j
public class IncomeController {
    private final IncomeService incomeService;
    
    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO incomeDTO) {
        log.info("Adding Income");
        IncomeDTO dto =incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getMonthIncomes(){
        log.info("Getting Incomes of Month");
        List<IncomeDTO> incomes = incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(incomes);
    }

    @DeleteMapping("{incomeId}")
    public ResponseEntity<Void> deleteIncomeforCurrentUser(@PathVariable Long incomeId){
        log.info("Deleting Income for By id");
        incomeService.deleteIncome(incomeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
