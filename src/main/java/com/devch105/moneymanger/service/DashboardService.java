package com.devch105.moneymanger.service;

import com.devch105.moneymanger.dto.ExpenseDTO;
import com.devch105.moneymanger.dto.IncomeDTO;
import com.devch105.moneymanger.dto.RecentTransactionDTO;
import com.devch105.moneymanger.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;


@Service
@RequiredArgsConstructor
public class DashboardService {
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;


    public Map<String,Object>  getDashboardData(){
       ProfileEntity currentProfile =  profileService.getCurrentProfile();
       Map<String,Object> map = new LinkedHashMap<>();
       List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
       List<ExpenseDTO> latestExpenses = expenseService.getlatest5ExpensesForCurrentUser();
        List<RecentTransactionDTO>  recentTransaction =  concat(latestIncomes.stream().map(income ->
               RecentTransactionDTO.builder()
                       .id(income.getId())
                       .profileId(currentProfile.getId())
                       .name(income.getName())
                       .icon(income.getIcon())
                       .amount(income.getAmount())
                       .date(income.getDate())
                       .updatedAt(income.getUpdatedAt())
                       .createdAt(income.getCreatedAt())
                       .type("income")
                       .build()),
               latestExpenses.stream().map(expense ->
                          RecentTransactionDTO.builder()
                                  .id(expense.getId())
                                  .name(expense.getName())
                                  .icon(expense.getIcon())
                                  .amount(expense.getAmount())
                                  .date(expense.getDate())
                                  .type("expense")
                                  .profileId(currentProfile.getId())
                                  .createdAt(expense.getCreatedAt())
                                  .updatedAt(expense.getUpdatedAt())
                                  .build()
       )).sorted((a,b)->{
           int cmp = b.getDate().compareTo(a.getDate());
           if(cmp==0 && a.getCreatedAt()!=null && b.getCreatedAt()!=null){
               return a.getCreatedAt().compareTo(b.getCreatedAt());
           }
           return cmp;

       }).collect(Collectors.toList());
        map.put("totalBalance",incomeService.totalIncomemForCurrentUser().subtract(expenseService.totalExpensesForCurrentUser()));
        map.put("totalIncome",incomeService.totalIncomemForCurrentUser());
        map.put("totalExpense",expenseService.totalExpensesForCurrentUser());
        map.put("recent5Expenses",latestExpenses);
        map.put("recent5Incomes",latestIncomes);
        map.put("recent5Transactions",recentTransaction);
        return map;
    }
}
