package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.ProductDto;
import com.example.ServiceApp.entity.Producer;
import com.example.ServiceApp.entity.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductMapper {

    public Product toProduct(ProductDto productDto, Producer producer) {
        return Product.builder()
                .name(productDto.getName())
                .cod(productDto.getCod())
                .producerId(producer).build();
    }

    public ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .cod(product.getCod())
                .name(product.getName())
                .build();
    }
}
