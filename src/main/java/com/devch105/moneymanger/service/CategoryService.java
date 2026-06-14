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
        ProfileEntity entity = profileService.getCurrentProfile();
         CategoryEntity categoryEntity = categoryRepository.findByIdAndProfileId(id, entity.getId()).orElseThrow(()-> new RuntimeException("Category not found"));

         categoryEntity.setName(dto.getName());
         categoryEntity.setIcon(dto.getIcon());
         categoryRepository.save(categoryEntity);

         return toDTO(categoryEntity);
    }

    public void deleteCategoryById(Long id){
       try{
           ProfileEntity entity = profileService.getCurrentProfile();
           CategoryEntity categoryEntity = categoryRepository.findByIdAndProfileId(id, entity.getId()).orElseThrow(()-> new RuntimeException("Category not found"));
           categoryRepository.delete(categoryEntity);
       }catch(Exception e){
           log.error(e.getMessage());
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
       }
    }



    private CategoryEntity toEntity(CategoryDTO dto , ProfileEntity profileEntity) {
         return CategoryEntity
                .builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .type(dto.getType())
                .profile(profileEntity)
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
                .build();
    }
}
