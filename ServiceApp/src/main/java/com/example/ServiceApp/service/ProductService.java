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

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private EmailService emailService;


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



    public ResponseEntity<ProductDto> saveOrUpdateProduct(Long id, String name, String cod, Long producer, Double price, Integer quantity, MultipartFile image) {
        Product product;
        int oldQuantity = 0;

        if (id != null) {
            // UPDATE
            Optional<Product> optionalProduct = productRepository.findById(id);
            if (optionalProduct.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            product = optionalProduct.get();
            oldQuantity = product.getQuantity();  // memorăm cantitatea veche
        } else {
            // CREATE
            product = new Product();
        }

        product.setName(name);
        product.setCod(cod);
        product.setQuantity(quantity);
        product.setProducerId(producer);
        product.setPrice(price);

        // Image handling
        if (image != null && !image.isEmpty()) {
            String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("C:/Users/iulia/ServiceApp/ServiceAppGurskMedica/uploads/");
//            Path uploadPath = Paths.get("/uploads/");
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

        // Trimitem email dacă cantitatea a scăzut
        if (id != null && quantity < oldQuantity) {
            int scadere = oldQuantity - quantity;
            emailService.sendProductUpdateEmail("service@gurskmedica.ro", cod, scadere);
        }

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
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }


    public List<ProductDto> search(String keyword) {
        List<Product> productList = productRepository.searchProduct(keyword);
        return productMapper.toDtoList(productList);
    }

    public void updateQuantityByCode(String cod, int quantity) {
        Optional<Product> optionalProduct = productRepository.findByCodIgnoreCase(cod.trim());

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setQuantity(quantity);
            productRepository.save(product);
        } else {
            throw new RuntimeException("Produsul nu a fost găsit");
        }
    }



}
