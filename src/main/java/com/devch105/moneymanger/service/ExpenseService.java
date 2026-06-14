package com.devch105.moneymanger.service;

import com.devch105.moneymanger.dto.ExpenseDTO;
import com.devch105.moneymanger.entity.CategoryEntity;
import com.devch105.moneymanger.entity.ExpenseEntity;
import com.devch105.moneymanger.entity.ProfileEntity;
import com.devch105.moneymanger.repository.CategoryRepository;
import com.devch105.moneymanger.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    // add new expense to DB
    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        ProfileEntity profile =  profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findByIdAndProfileId(expenseDTO.getCategoryId(), profile.getId()).orElseThrow(()-> new RuntimeException("Category not found"));
        ExpenseEntity entity = toEntity(expenseDTO, category, profile);
        expenseRepository.save(entity);
        return toDTO(entity);
    }

    // reterive expenses for current month or start date or enddate
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile =  profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate =  now.withDayOfMonth(1);
        LocalDate endDate =  now.withDayOfMonth(now.lengthOfMonth());

       List<ExpenseEntity> entities =  expenseRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);

       return entities.stream().map(this::toDTO).toList();
    }

    public  void  deleteExpense(Long ExpenseId){
       ProfileEntity profile =  profileService.getCurrentProfile();
        ExpenseEntity entity = expenseRepository.findById(ExpenseId).orElseThrow(()-> new RuntimeException("Expense not found"));
        if(!entity.getProfile().getId().equals(profile.getId())){
           throw new RuntimeException("UnAuthorized to Delete this Expense");
        }
        expenseRepository.delete(entity);
    }

























    private ExpenseDTO toDTO(ExpenseEntity entity) {
        return ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory()!=null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory()!=null ? entity.getCategory().getName() : "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    private ExpenseEntity toEntity(ExpenseDTO dto, CategoryEntity category, ProfileEntity profile) {
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }
}
