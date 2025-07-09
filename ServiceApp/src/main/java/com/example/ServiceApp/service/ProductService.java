package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.dto.ProducerDto;
import com.example.ServiceApp.dto.ProductDto;
import com.example.ServiceApp.entity.InterventionSheet;
import com.example.ServiceApp.entity.Producer;
import com.example.ServiceApp.entity.Product;
import com.example.ServiceApp.mapper.ProducerMapper;
import com.example.ServiceApp.mapper.ProductMapper;
import com.example.ServiceApp.repository.ProducerRepository;
import com.example.ServiceApp.repository.ProductRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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


    public ResponseEntity<ProductDto> saveOrUpdateProduct(Long id, String name, String cod, Long producer, Integer quantity, MultipartFile image) {
        Product product;

        if (id != null) {
            // UPDATE
            Optional<Product> optionalProduct = productRepository.findById(id);
            if (optionalProduct.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            product = optionalProduct.get();
        } else {
            // CREATE
            product = new Product();
        }

        product.setName(name);
        product.setCod(cod);
        product.setQuantity(quantity);
        product.setProducerId(producer);

        // Image handling
        if (image != null && !image.isEmpty()) {
            String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(imageName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImageName(imageName);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        productRepository.save(product);
        ProductDto dto = productMapper.toDto(product);
        return ResponseEntity.ok(dto);
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
        product.setProducerId(productDto.getProducer());
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }


    public List<ProductDto> search(String keyword) {
        List<Product> productList = productRepository.searchProduct(keyword);
        return productMapper.toDtoList(productList);
    }



}
