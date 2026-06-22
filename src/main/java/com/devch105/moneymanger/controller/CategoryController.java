package com.devch105.moneymanger.controller;

import com.devch105.moneymanger.dto.CategoryDTO;
import com.devch105.moneymanger.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO){
        try{
            CategoryDTO saveCategory = categoryService.saveCategory(categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(saveCategory);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(){
        List<CategoryDTO> categoryList = categoryService.getCategoriesForCurrentProfile();
        log.info("Category List: {}", categoryList);
        return ResponseEntity.status(HttpStatus.OK).body(categoryList);
    }


    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByType(@PathVariable String type){
       List<CategoryDTO> categoryDTOList = categoryService.getCategoriesByTypeForCurrentProfile(type);
       log.info("Category List By Type: {}", categoryDTOList);
       return ResponseEntity.status(HttpStatus.OK).body(categoryDTOList);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable  Long categoryId, @RequestBody CategoryDTO categoryDTO){
        try{
          CategoryDTO updateDto =  categoryService.updateCategory(categoryId, categoryDTO);
          log.info("Category Updated: {}", updateDto);
            return ResponseEntity.status(HttpStatus.OK).body(updateDto);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable  Long categoryId){
       try{
           categoryService.deleteCategoryById(categoryId);
           log.info("Category Service  deleted: {}", categoryId);
           return ResponseEntity.status(HttpStatus.OK).body("Category deleted") ;
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Category delete failed");
       }
    }

}
