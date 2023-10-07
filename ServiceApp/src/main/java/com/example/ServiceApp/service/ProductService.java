package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.ProductDto;
import com.example.ServiceApp.entity.Producer;
import com.example.ServiceApp.entity.Product;
import com.example.ServiceApp.mapper.ProductMapper;
import com.example.ServiceApp.repository.ProducerRepository;
import com.example.ServiceApp.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductService {
    private final ProducerRepository producerRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProducerRepository producerRepository, ProductRepository productRepository, ProductMapper productMapper) {
        this.producerRepository = producerRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }


    public Product save(ProductDto productDto) {
        Product productToBeSaved = productMapper.toProduct(productDto);
        Producer producer = findProducerById(productDto.getProducer());
        productToBeSaved.setProducer(producer);
        return productRepository.save(productToBeSaved);
    }

    public Producer findProducerById(Long id) {
        return producerRepository.findById(id).orElseThrow();
    }

    public ProductDto findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        return productMapper.toDto(product);
    }

    public List<ProductDto> findAll(){
        List<Product> products = productRepository.findAll();
        return productMapper.toDtoList(products);
    }

    public Product update(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getId()).orElseThrow();
        product.setCod(productDto.getCod());
        product.setName(productDto.getName());
        product.setProducer(product.getProducer());
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }



}
