package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.dto.ProducerDto;
import com.example.ServiceApp.dto.ProductDto;
import com.example.ServiceApp.dto.ProductScanDto;
import com.example.ServiceApp.entity.Producer;
import com.example.ServiceApp.entity.Product;
import com.example.ServiceApp.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
//@PostMapping
//    public ProductDto create(@RequestBody ProductDto productDto) {
//        return productService.save(productDto);
//    }

    @PostMapping
    public ResponseEntity<ProductDto> saveOrUpdateProduct(
            @RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam String cod,
            @RequestParam Integer quantity,
            @RequestParam Long producerName,
            @RequestParam Double price,
            @RequestParam(required = false) MultipartFile image
    ) {
        return productService.saveOrUpdateProduct(id,name, cod, producerName, price, quantity, image);
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

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> search(@RequestParam String keyword) {
        List<ProductDto> productDtoList = productService.search(keyword);
        return new ResponseEntity<>(productDtoList, HttpStatus.OK);
    }

//    @PostMapping("/scan")
//    public ResponseEntity<String> updateProductQuantityFromScan(@RequestBody ProductScanDto scanDTO) {
//        try {
//            productService.updateQuantityByCode(scanDTO.getCod(), scanDTO.getQuantity());
//            return ResponseEntity.ok("Product quantity updated");
//        } catch (RuntimeException ex) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//        }
//    }

    @PostMapping("/scan")
    public ResponseEntity<Map<String, String>> updateProductQuantityFromScan(@RequestBody ProductScanDto scanDTO) {
        try {
            productService.updateQuantityByCode(scanDTO.getCod(), scanDTO.getQuantity());
            return ResponseEntity.ok(Collections.singletonMap("message", "Product quantity updated"));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", ex.getMessage()));
        }
    }

}
