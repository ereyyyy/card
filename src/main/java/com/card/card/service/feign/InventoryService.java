package com.card.card.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", url = "http://localhost:8086/api")
public interface InventoryService {
    @PostMapping(path = "/create-inventory")
    void createInventory(@RequestBody String request);

    @PostMapping(path = "/increase-inventory-count")
    void increaseProductCount(@RequestBody String request);

    @PostMapping(path = "/decrease-inventory-count")
    void decreaseProductCount(@RequestBody String request);

    @PostMapping(path = "/get-inventory-count")
    Long getProductCount(@RequestBody String request);
}
