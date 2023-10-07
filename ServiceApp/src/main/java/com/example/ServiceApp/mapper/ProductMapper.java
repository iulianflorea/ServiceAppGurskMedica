package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.ProducerDto;
import com.example.ServiceApp.dto.ProductDto;
import com.example.ServiceApp.entity.Producer;
import com.example.ServiceApp.entity.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ProductMapper {

    public Product toProduct(ProductDto productDto) {
        return Product.builder()
                .name(productDto.getName())
                .cod(productDto.getCod())
                .build();
    }

    public ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .cod(product.getCod())
                .name(product.getName())
                .producer(product.getProducer().getId())
                .build();
    }

    public List<ProductDto> toDtoList(List<Product> productList) {
        List<ProductDto> productDtoList = new ArrayList<>();
        for (Product product : productList) {
            ProductDto productDto = toDto(product);
            productDtoList.add(productDto);
        }
        return productDtoList;
    }
}
