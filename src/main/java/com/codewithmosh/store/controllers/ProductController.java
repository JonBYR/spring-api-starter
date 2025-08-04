package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.dtos.RegisterProductDto;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.mappers.ProductMapper;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<ProductDto> getAllProducts(@RequestParam(required = false, name = "categoryId")  Byte categoryId) {
        List<Product> products;
        if (categoryId == null) {
            products = productRepository.findAllWithCategories();
        }
        else {
            products = productRepository.findAllByCategory_Id(categoryId);
        }
        return products.stream().map(productMapper::toDto).toList();
    }

    @GetMapping("/{id}")
     public ResponseEntity<ProductDto> GetProduct(@PathVariable Long id) {
         var product =  productRepository.findById(id).orElse(null);
         if(product == null) {
             return ResponseEntity.notFound().build();
         }
         else {
             var productDto = productMapper.toDto(product);
             return ResponseEntity.ok(productDto);
         }
     }
     @PostMapping
     public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto, UriComponentsBuilder uriBuilder) {
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if(category == null) {
            return ResponseEntity.badRequest().build();
        }
        var product = productMapper.toEntity(productDto);
        product.setCategory(category);
        productRepository.save(product);
        productDto.setId(product.getId());
        var uri = uriBuilder.path("/products/{id}").buildAndExpand(productDto.getId()).toUri();
        return ResponseEntity.created(uri).body(productDto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);
        if(product == null) {
            return ResponseEntity.notFound().build();
        }
        else
        {
            productRepository.delete(product);
            return  ResponseEntity.noContent().build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if(category == null) {
            return ResponseEntity.badRequest().build();
        }
        var product = productRepository.findById(id).orElse(null);
        if(product == null) {
            return ResponseEntity.notFound().build();
        }
        else {
            productMapper.update(productDto, product);
            product.setCategory(category);
            productRepository.save(product);
            productDto.setId(product.getId());
            return ResponseEntity.ok(productDto);
        }
    }
}
