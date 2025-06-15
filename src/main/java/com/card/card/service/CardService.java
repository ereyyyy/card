package com.card.card.service;

import com.card.card.model.request.ProductRequest;
import com.card.card.model.request.UserRequest;

import java.util.List;

public interface CardService {

    void addProduct(ProductRequest request, UserRequest userRequest);

    void removeProduct(ProductRequest productRequest, UserRequest userRequest);

    void createUser(UserRequest request);

    List<String> buyProducts(String userName);

    Long getOrderId(String userName);
}
