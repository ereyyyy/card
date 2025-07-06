package com.card.card.service.impl;

import com.card.card.model.entitiy.CardEntity;
import com.card.card.model.repository.CardRepository;
import com.card.card.model.request.AddProductRequest;
import com.card.card.model.request.ProductRequest;
import com.card.card.model.request.RemoveProductRequest;
import com.card.card.model.request.UserRequest;
import com.card.card.model.response.CartSummaryResponse;
import com.card.card.service.CardService;
import com.card.card.service.feign.InventoryService;
import com.card.card.service.feign.ProductService;
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
    private final ProductService productService;

    @Override
    public void addProduct(ProductRequest productRequest, UserRequest userRequest) {
        CardEntity entity = repository.findByOwner(userRequest.getName());
        if (entity == null) {
            log.error("Kullanıcı kartı bulunamadı: " + userRequest.getName());
            throw new RuntimeException("Kullanıcı kartı bulunamadı: " + userRequest.getName());
        }
        
        // Ürün adı kontrolü
        String productName = productRequest.getProductName();
        if (productName == null || productName.trim().isEmpty()) {
            log.error("Ürün adı boş olamaz");
            throw new RuntimeException("Ürün adı boş olamaz");
        }
        
        // Ürün adını temizle (başındaki ve sonundaki boşlukları kaldır)
        productName = productName.trim();
        
        // Önce ürünün geçerli olup olmadığını kontrol edelim
        boolean productValid = false;
        try {
            // Product Service'ten ürün fiyatını kontrol edelim
            Long price = productService.checkPrice(productName);
            if (price != null && price > 0) {
                productValid = true;
                log.info("Ürün geçerli bulundu: " + productName + ", fiyat: " + price);
            }
        } catch (Exception e) {
            log.error("Ürün geçersiz: " + productName + ", hata: " + e.getMessage());
            throw new RuntimeException("Ürün bulunamadı: " + productName);
        }
        
        // Sadece geçerli ürünler sepete eklenir
        if (productValid) {
            List<String> productList = entity.getHeldProducts();
            if (productList == null) {
                productList = new ArrayList<>();
            }
            productList.add(productName);
            entity.setHeldProducts(productList);
            repository.save(entity);
            
            // Inventory Service çağrısını try-catch ile saralım
            try {
                inventoryService.decreaseProductCount(productName);
                log.info("Ürün stoku azaltıldı: " + productName);
            } catch (Exception e) {
                log.error("Inventory Service hatası - ürün: " + productName + ", hata: " + e.getMessage());
                // Stok hatası olsa bile sepet işlemi tamamlanır
            }
        }
    }

    @Override
    public void removeProduct(ProductRequest productRequest, UserRequest userRequest) {
        CardEntity entity = repository.findByOwner(userRequest.getName());
        if (entity == null) {
            log.error("Kullanıcı kartı bulunamadı: " + userRequest.getName());
            throw new RuntimeException("Kullanıcı kartı bulunamadı: " + userRequest.getName());
        }
        
        List<String> productList = entity.getHeldProducts();
        if (productList == null) {
            productList = new ArrayList<>();
        }
        
        // Ürün sepette var mı kontrol edelim
        if(productList.contains(productRequest.getProductName())) {
            productList.remove(productRequest.getProductName());
            entity.setHeldProducts(productList);
            repository.save(entity);
            
            // Inventory Service çağrısını try-catch ile saralım
            try {
                inventoryService.increaseProductCount(productRequest.getProductName());
                log.info("Ürün stoku artırıldı: " + productRequest.getProductName());
            } catch (Exception e) {
                log.error("Inventory Service hatası - ürün: " + productRequest.getProductName() + ", hata: " + e.getMessage());
                // Stok hatası olsa bile sepet işlemi tamamlanır
            }
        } else {
            log.warn("Ürün sepette bulunamadı: " + productRequest.getProductName());
            throw new RuntimeException("Ürün sepette bulunamadı: " + productRequest.getProductName());
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
        if (entity == null) {
            log.warn("Kullanıcı kartı bulunamadı: " + userName);
            return new ArrayList<>();
        }
        
        List<String> response = entity.getHeldProducts() != null ? entity.getHeldProducts() : new ArrayList<>();
        entity.setHeldProducts(new ArrayList<>());
        repository.save(entity);
        return response;
    }

    @Override
    public Long getOrderId(String userName) {
        CardEntity entity = repository.findByOwner(userName);
        if (entity == null) {
            log.warn("Kullanıcı kartı bulunamadı: " + userName);
            return null;
        }
        return entity.getId();
    }

    @Override
    public List<String> getCart(String userName) {
        CardEntity entity = repository.findByOwner(userName);
        if (entity == null) {
            log.warn("Kullanıcı kartı bulunamadı: " + userName);
            return new ArrayList<>();
        }
        return entity.getHeldProducts() != null ? entity.getHeldProducts() : new ArrayList<>();
    }

    @Override
    public Double getCartTotal(String userName) {
        CardEntity entity = repository.findByOwner(userName);
        if (entity == null) {
            log.warn("Kullanıcı kartı bulunamadı: " + userName);
            return 0.0;
        }
        
        List<String> products = entity.getHeldProducts();
        if (products == null || products.isEmpty()) {
            return 0.0;
        }
        
        double total = 0.0;
        for (String productName : products) {
            try {
                Long price = productService.checkPrice(productName);
                if (price != null) {
                    total += price;
                }
            } catch (Exception e) {
                log.error("Ürün fiyatı alınamadı: " + productName, e);
            }
        }
        
        return total;
    }

    @Override
    public CartSummaryResponse getCartSummary(String userName) {
        CardEntity entity = repository.findByOwner(userName);
        if (entity == null) {
            log.warn("Kullanıcı kartı bulunamadı: " + userName);
            return new CartSummaryResponse(userName, new ArrayList<>(), 0.0, 0);
        }
        
        List<String> products = entity.getHeldProducts();
        if (products == null) {
            products = new ArrayList<>();
        }
        
        Double totalAmount = getCartTotal(userName);
        Integer itemCount = products.size();
        
        return new CartSummaryResponse(userName, products, totalAmount, itemCount);
    }

    @Override
    public void addProduct(AddProductRequest request) {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName(request.getProductName());
        
        UserRequest userRequest = new UserRequest();
        userRequest.setName(request.getUserName());
        userRequest.setEmail(request.getEmail());
        userRequest.setAddresses(request.getAddresses());
        userRequest.setUserType(request.getUserType() != null ? 
            com.card.card.model.enums.UserType.valueOf(request.getUserType()) : null);
        userRequest.setPassword(request.getPassword());
        
        addProduct(productRequest, userRequest);
    }
    
    @Override
    public void removeProduct(RemoveProductRequest request) {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName(request.getProductName());
        
        UserRequest userRequest = new UserRequest();
        userRequest.setName(request.getUserName());
        userRequest.setEmail(request.getEmail());
        userRequest.setAddresses(request.getAddresses());
        userRequest.setUserType(request.getUserType() != null ? 
            com.card.card.model.enums.UserType.valueOf(request.getUserType()) : null);
        userRequest.setPassword(request.getPassword());
        
        removeProduct(productRequest, userRequest);
    }
}
