package com.card.card.service.feign;

import com.card.card.model.request.CreateProductRequest;
import com.card.card.model.response.CreateProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service", url = "http://localhost:8085")
public interface ProductService {

    @PostMapping(path = "/create-product")
    CreateProductResponse createProduct(@RequestBody CreateProductRequest request);

    @PostMapping(path = "/check-product-price")
    Long checkPrice(@RequestBody String name);
}
