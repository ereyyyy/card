package com.card.card.controller;

import com.card.card.model.request.AddProductRequest;
import com.card.card.model.request.RemoveProductRequest;
import com.card.card.model.request.UserRequest;
import com.card.card.model.response.CartSummaryResponse;
import com.card.card.service.impl.CardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CardController {

    private final CardServiceImpl cardService;

    @PostMapping(path = "/add-product")
    public ResponseEntity<String> addProduct(@RequestBody AddProductRequest request) {
        try {
            cardService.addProduct(request);
            return ResponseEntity.ok("Ürün sepete eklendi: " + request.getProductName());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Beklenmeyen hata: " + e.getMessage());
        }
    }

    @PostMapping(path = "/remove-product")
    public ResponseEntity<String> removeProduct(@RequestBody RemoveProductRequest request) {
        try {
            cardService.removeProduct(request);
            return ResponseEntity.ok("Ürün sepetten çıkarıldı: " + request.getProductName());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Beklenmeyen hata: " + e.getMessage());
        }
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

    @GetMapping(path = "/cart/{userName}")
    public ResponseEntity<List<String>> getCart(@PathVariable String userName) {
        return ResponseEntity.ok(cardService.getCart(userName));
    }

    @GetMapping(path = "/cart/total/{userName}")
    public ResponseEntity<Double> getCartTotal(@PathVariable String userName) {
        return ResponseEntity.ok(cardService.getCartTotal(userName));
    }

    @GetMapping(path = "/cart/summary/{userName}")
    public ResponseEntity<CartSummaryResponse> getCartSummary(@PathVariable String userName) {
        return ResponseEntity.ok(cardService.getCartSummary(userName));
    }
}
