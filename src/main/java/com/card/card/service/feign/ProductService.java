package com.card.card.service.feign;

import com.card.card.model.request.CreateProductRequest;
import com.card.card.model.response.CreateProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ProductService Feign Client
 *
 * Bu arayüz, Shopping Card mikroservisinin Product Service mikroservisi ile REST üzerinden haberleşmesini sağlar.
 *
 * Kullanım Senaryosu:
 * - Sepetteki ürünlerin fiyatını öğrenmek için kullanılır.
 * - Yeni ürün oluşturmak için kullanılır.
 *
 * Bağlandığı Servis:
 *   http://localhost:8085
 *
 * Sağlanan Endpointler:
 *
 * 1. createProduct
 *    - POST /create-product
 *    - Request: CreateProductRequest
 *    - Response: CreateProductResponse
 *    - Açıklama: Yeni bir ürün oluşturmak için kullanılır.
 *
 * 2. checkPrice
 *    - GET /check-product-price
 *    - Request: String (ürün adı)
 *    - Response: Long (ürünün fiyatı)
 *    - Açıklama: Ürün adını göndererek ürünün fiyatını sorgular.
 *
 * Örnek Kullanım (Service içinde):
 *   Long fiyat = productService.checkPrice("iPhone 15");
 *
 * Not: Feign Client otomatik olarak Spring Cloud tarafından enjekte edilir.
 */
@FeignClient(name = "product-service", url = "http://localhost:8085/api")
public interface ProductService {

    @PostMapping(path = "/create-product")
    CreateProductResponse createProduct(@RequestBody CreateProductRequest request);

    @GetMapping(path = "/check-product-price")
    Long checkPrice(@RequestParam("name") String name);
}