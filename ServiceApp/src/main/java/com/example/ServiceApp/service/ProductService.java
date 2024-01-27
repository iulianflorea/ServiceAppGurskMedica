package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.ProducerDto;
import com.example.ServiceApp.dto.ProductDto;
import com.example.ServiceApp.entity.Producer;
import com.example.ServiceApp.entity.Product;
import com.example.ServiceApp.mapper.ProducerMapper;
import com.example.ServiceApp.mapper.ProductMapper;
import com.example.ServiceApp.repository.ProducerRepository;
import com.example.ServiceApp.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProducerRepository producerRepository;
    private final ProducerMapper producerMapper;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProducerRepository producerRepository, ProducerMapper producerMapper, ProductRepository productRepository, ProductMapper productMapper) {
        this.producerRepository = producerRepository;
        this.producerMapper = producerMapper;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }


    public ProductDto save(ProductDto productDto) {
        Product productToBeSaved = productMapper.toProduct(productDto);
        Producer producer = findProducerById(productDto.getProducer());
        productToBeSaved.setProducerId(producer.getId());
        if(productDto.getId() == null) {
            Product productSaved = productRepository.save(productToBeSaved);
            return productMapper.toDto(productSaved);
        }else {
            update(productDto);
        }
        return productMapper.toDto(productToBeSaved);
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
        product.setQuantity(productDto.getQuantity());
        product.setProducerId(product.getProducerId());
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }



}
