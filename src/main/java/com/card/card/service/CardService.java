package com.card.card.service;

import com.card.card.model.request.AddProductRequest;
import com.card.card.model.request.ProductRequest;
import com.card.card.model.request.RemoveProductRequest;
import com.card.card.model.request.UserRequest;
import com.card.card.model.response.CartSummaryResponse;

import java.util.List;

public interface CardService {

    void addProduct(ProductRequest request, UserRequest userRequest);

    void removeProduct(ProductRequest productRequest, UserRequest userRequest);

    void createUser(UserRequest request);

    List<String> buyProducts(String userName);

    Long getOrderId(String userName);
    
    List<String> getCart(String userName);
    
    Double getCartTotal(String userName);
    
    CartSummaryResponse getCartSummary(String userName);
    
    void addProduct(AddProductRequest request);
    
    void removeProduct(RemoveProductRequest request);
}
