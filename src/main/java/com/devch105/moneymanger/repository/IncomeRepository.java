package com.devch105.moneymanger.repository;

import com.devch105.moneymanger.entity.IncomeEntity;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    // select * from tbl_income where profile_id=1 order by date desc
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);
    @Query("select sum(i.amount) from IncomeEntity i where i.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId")Long profileId);
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate , LocalDate endDate, String keyword, Sort sort);
    List<IncomeEntity>  findByProfileIdAndDateBetween(Long ProfileId, LocalDate startDate , LocalDate endDate);
}
