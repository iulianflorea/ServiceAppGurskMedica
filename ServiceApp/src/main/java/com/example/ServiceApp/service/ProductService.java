package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.ProductDto;
import com.example.ServiceApp.entity.Producer;
import com.example.ServiceApp.entity.Product;
import com.example.ServiceApp.mapper.ProductMapper;
import com.example.ServiceApp.repository.ProductRepository;

import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }


    public Product save(Product product){
       return productRepository.save(product);
    }


    public Product create(ProductDto productDto, Producer producer) {
        return save(productMapper.toProduct(productDto, producer));
    }
}
