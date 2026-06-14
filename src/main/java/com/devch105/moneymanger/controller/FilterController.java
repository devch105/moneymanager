package com.devch105.moneymanger.controller;

import com.devch105.moneymanger.dto.ExpenseDTO;
import com.devch105.moneymanger.dto.FilterDTO;
import com.devch105.moneymanger.dto.IncomeDTO;
import com.devch105.moneymanger.service.ExpenseService;
import com.devch105.moneymanger.service.IncomeService;
import com.devch105.moneymanger.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class FilterController {
    private  final ExpenseService expenseService;
    private final IncomeService incomeService;


    @GetMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filterDTO){
        // fields to pass In
        LocalDate startDate = filterDTO.getStartDate()!=null?filterDTO.getStartDate():LocalDate.of(2000, 1, 1);
        LocalDate endDate = filterDTO.getEndDate()!=null?filterDTO.getEndDate():LocalDate.now();
         String keyword = filterDTO.getKeyword()!=null?filterDTO.getKeyword():"";
         String sortField = filterDTO.getSortField()!=null?filterDTO.getSortField():"date";
         String sortOrder = filterDTO.getSortOrder()!=null?filterDTO.getSortOrder():"desc";
        Sort.Direction direction = sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);

        if(filterDTO.getType().equals("income")){
           List<IncomeDTO> list =  incomeService.filterIncomes(startDate,endDate,keyword,sort);
           return ResponseEntity.ok(list);
        } else if (filterDTO.getType().equals("expense")) {
            List<ExpenseDTO> list = expenseService.filterExpenses(startDate,endDate,keyword,sort);
            return ResponseEntity.ok(list);
        }else{
            return  ResponseEntity.badRequest().body("Invalid filter type Must be income or expense");
        }
    }
}
