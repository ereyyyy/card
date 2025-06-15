package com.card.card.controller;

import com.card.card.model.request.ProductRequest;
import com.card.card.model.request.UserRequest;
import com.card.card.service.impl.CardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CardController {

    private final CardServiceImpl cardService;

    @PostMapping(path = "/add-product")
    public void addProduct(@RequestBody ProductRequest productRequest, @RequestBody UserRequest request) {
        cardService.addProduct(productRequest, request);
    }

    @PostMapping(path = "/remove-product")
    public void removeProduct(@RequestBody ProductRequest productRequest, @RequestBody UserRequest request) {
        cardService.removeProduct(productRequest, request);
    }

    @PostMapping(path = "/new-user")
    public void createUser(@RequestBody UserRequest request) {
        cardService.createUser(request);
    }


    @PostMapping(path = "/place-order")
    public ResponseEntity<List<String>> placeOrder(@RequestBody String user) {
        return ResponseEntity.ok(cardService.buyProducts(user));
    }

    @PostMapping(path = "/get-order-id")
    public ResponseEntity<Long> getOrderId(@RequestBody String user) {
        return ResponseEntity.ok(cardService.getOrderId(user));
    }
}
