package com.devch105.moneymanger.service;

import com.devch105.moneymanger.dto.CategoryDTO;
import com.devch105.moneymanger.entity.CategoryEntity;
import com.devch105.moneymanger.entity.ProfileEntity;
import com.devch105.moneymanger.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;


    public CategoryDTO saveCategory(CategoryDTO dto) {

        ProfileEntity entity = profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(dto.getName(), entity.getId()) ){
            log.error("Error: ");
            throw new RuntimeException("Category with this name already exist");
        }
        CategoryEntity newCategory = toEntity(dto, entity);
        categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    public List<CategoryDTO> getCategoriesForCurrentProfile(){
        ProfileEntity entity = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntities = categoryRepository.findByProfileId(entity.getId());
        log.info("Category found for current user: {}", categoryEntities.toString());
        return categoryEntities.stream().map(this::toDTO).toList();
    }

    public List<CategoryDTO> getCategoriesByTypeForCurrentProfile(String type){
        ProfileEntity entity = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntities =  categoryRepository.findByTypeAndProfileId(type, entity.getId());
        log.info("Category found for current user By type : {}", categoryEntities.toString());
        return categoryEntities.stream().map(this::toDTO).toList();
    }



    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        System.out.println("See Category data : "+dto);
        ProfileEntity entity = profileService.getCurrentProfile();
         CategoryEntity categoryEntity = categoryRepository.findByIdAndProfileId(id, entity.getId()).orElseThrow(()-> new RuntimeException("Category not found"));
         categoryEntity.setName(dto.getName());
         categoryEntity.setIcon(dto.getIcon());
         if(categoryEntity.getTransactionCount()!=0L){
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transactions count must be zero to change category Type");
         }
         categoryEntity.setType(dto.getType());
         categoryRepository.save(categoryEntity);

         return toDTO(categoryEntity);
    }

    public void deleteCategoryById(Long id){
       try{
           ProfileEntity entity = profileService.getCurrentProfile();
           CategoryEntity categoryEntity = categoryRepository.findByIdAndProfileId(id, entity.getId()).orElseThrow(()-> new RuntimeException("Category not found"));
           if(categoryEntity.getTransactionCount()>0){
               throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete category that has transactions");
           }
           categoryRepository.delete(categoryEntity);
       }catch(Exception e){
           log.error(e.getMessage());
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
       }
    }


    public void incrementTransactionCount(CategoryEntity category) {
        Long count = category.getTransactionCount() == null
                ? 0L
                : category.getTransactionCount();

        category.setTransactionCount(count + 1);
        categoryRepository.save(category);
    }

    public void decrementTransactionCount(CategoryEntity category) {
        Long count = category.getTransactionCount() == null
                ? 0L
                : category.getTransactionCount();

        category.setTransactionCount(
                Math.max(0L, count - 1)
        );

        categoryRepository.save(category);
    }

    private CategoryEntity toEntity(CategoryDTO dto , ProfileEntity profileEntity) {
         return CategoryEntity
                .builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .type(dto.getType())
                .profile(profileEntity)
                 .transactionCount(0L)
                .build();

    }


    private CategoryDTO toDTO(CategoryEntity entity ) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .profileId(entity.getProfile()!=null?entity.getProfile().getId():null)
                .name(entity.getName())
                .icon(entity.getIcon())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .transactionCount(entity.getTransactionCount())
                .build();
    }
}
