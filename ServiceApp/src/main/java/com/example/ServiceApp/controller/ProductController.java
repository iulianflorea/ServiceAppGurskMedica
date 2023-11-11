package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.ProducerDto;
import com.example.ServiceApp.dto.ProductDto;
import com.example.ServiceApp.entity.Producer;
import com.example.ServiceApp.entity.Product;
import com.example.ServiceApp.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
@PostMapping
    public ProductDto create(@RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @GetMapping("/getAll")
    public List<ProductDto> getAll() {
        return productService.findAll();
    }

    @GetMapping("/getById/{id}")
    public ProductDto getById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PutMapping
    public Product update(@RequestBody ProductDto productDto) {
        return productService.update(productDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
