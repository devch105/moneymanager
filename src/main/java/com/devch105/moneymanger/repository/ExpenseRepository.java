package com.devch105.moneymanger.repository;

import com.devch105.moneymanger.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    // select * from tbl_expenses where profile_id=1 order by date desc
   List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);
   List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);
   @Query("select sum(e.amount) from ExpenseEntity e where e.profile.id = :profileId")
   BigDecimal findTotalExpenseByProfileId(@Param("profileId")Long profileId);
   List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate , LocalDate endDate, String keyword, Sort sort);
   List<ExpenseEntity>  findByProfileIdAndDateBetween(Long ProfileId, LocalDate startDate , LocalDate endDate);
}
