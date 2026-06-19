package com.devch105.moneymanger.service;

import com.devch105.moneymanger.dto.IncomeDTO;
import com.devch105.moneymanger.entity.CategoryEntity;


import com.devch105.moneymanger.entity.IncomeEntity;
import com.devch105.moneymanger.entity.ProfileEntity;
import com.devch105.moneymanger.repository.CategoryRepository;

import com.devch105.moneymanger.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    // add new expense to DB
    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        ProfileEntity profile =  profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findByIdAndProfileId(incomeDTO.getCategoryId(), profile.getId()).orElseThrow(()-> new RuntimeException("Category not found"));
        IncomeEntity entity = toEntity(incomeDTO, category, profile);
        incomeRepository.save(entity);
        return toDTO(entity);
    }

    // reterive expenses for current month or start date or enddate
    public List<IncomeDTO> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile =  profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate =  now.withDayOfMonth(1);
        LocalDate endDate =  now.withDayOfMonth(now.lengthOfMonth());

        List<IncomeEntity> entities =  incomeRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);

        return entities.stream().map(this::toDTO).toList();
    }

    public void deleteIncome(Long incomeId) {
        System.out.println("Deleting income id = " + incomeId);

        ProfileEntity profile = profileService.getCurrentProfile();

        IncomeEntity entity = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        System.out.println("Found income = " + entity.getId());

        if (!entity.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("UnAuthorized to Delete this Expense");
        }

        incomeRepository.delete(entity);

        System.out.println("Deleted income = " + incomeId);
    }



    public List<IncomeDTO> getLatest5IncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity > getLatest5Incomes =  incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return getLatest5Incomes.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalIncomemForCurrentUser(){
        ProfileEntity profile =  profileService.getCurrentProfile();
        return  incomeRepository.findTotalIncomeByProfileId(profile.getId());
    }

    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate , String keyword, Sort sort){
       ProfileEntity profile = profileService.getCurrentProfile();
       List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,keyword,sort);
       return list.stream().map(this::toDTO).toList();
    }



























    private IncomeDTO toDTO(IncomeEntity entity) {
        return IncomeDTO.builder()
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
    private IncomeEntity toEntity(IncomeDTO dto, CategoryEntity category, ProfileEntity profile) {
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }
}
