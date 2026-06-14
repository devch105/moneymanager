package com.devch105.moneymanger.repository;

import com.devch105.moneymanger.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByProfileId(Long profileId);
   Optional<CategoryEntity> findByIdAndProfileId(Long id , Long profileId);
   List<CategoryEntity> findByTypeAndProfileId(String type , Long profileId);
   Boolean existsByNameAndProfileId(String Name , Long profileId);
   Boolean deleteByIdAndProfileId(Long id , Long profileId);
}
