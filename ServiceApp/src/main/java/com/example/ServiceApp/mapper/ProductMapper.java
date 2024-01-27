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
                .quantity(productDto.getQuantity())
                .producerId(productDto.getProducer())
                .build();
    }

    public ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .cod(product.getCod())
                .name(product.getName())
                .quantity(product.getQuantity())
                .producer(product.getProducerId())
                .producerName(getProducerName(product))
                .build();
    }

    private String getProducerName(Product product) {
        Producer producer = product.getProducer();
        return (producer != null) ? producer.getName() : null;
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
