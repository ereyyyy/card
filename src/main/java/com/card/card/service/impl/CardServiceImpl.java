package com.card.card.service.impl;

import com.card.card.model.entitiy.CardEntity;
import com.card.card.model.repository.CardRepository;
import com.card.card.model.request.ProductRequest;
import com.card.card.model.request.UserRequest;
import com.card.card.service.CardService;
import com.card.card.service.feign.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository repository;
    private final InventoryService inventoryService;

    @Override
    public void addProduct(ProductRequest productRequest, UserRequest userRequest) {
        CardEntity entity = repository.findByOwner(userRequest.getName());
        List<String> productList = entity.getHeldProducts();
        productList.add(productRequest.getProductName());
        entity.setHeldProducts(productList);
        repository.save(entity);
        inventoryService.decreaseProductCount(productRequest.getProductName());
    }

    @Override
    public void removeProduct(ProductRequest productRequest, UserRequest userRequest) {
        CardEntity entity = repository.findByOwner(userRequest.getName());
        List<String> productList = entity.getHeldProducts();
        if(productList.contains(productRequest.getProductName())) {
            productList.remove(productRequest.getProductName());
            entity.setHeldProducts(productList);
            repository.save(entity);
            inventoryService.increaseProductCount(productRequest.getProductName());
        }
    }

    @Override
    public void createUser(UserRequest request) {
        CardEntity entity = new CardEntity();
        entity.setOwner(request.getName());
        entity.setHeldProducts(new ArrayList<>());
        repository.save(entity);
    }

    @Override
    public List<String> buyProducts(String userName) {
        CardEntity entity = repository.findByOwner(userName);
        List<String> response = entity.getHeldProducts();
        entity.setHeldProducts(new ArrayList<>());
        repository.save(entity);
        return response;
    }

    @Override
    public Long getOrderId(String userName) {
        CardEntity entity = repository.findByOwner(userName);
        return entity.getId();
    }
}
