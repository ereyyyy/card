# Shopping Card Microservice

## 📋 Genel Bakış
Bu mikroservis, e-ticaret sistemlerinde kullanıcıların ürün ekleyip çıkarabildiği, sepetini görüntüleyip toplam tutarı görebildiği ve sipariş verebildiği bir Shopping Card (Sepet) altyapısı sunar. Mikroservis mimarisiyle, Product, Inventory ve User Auth gibi diğer servislerle REST üzerinden haberleşir.

---

## 🏗️ Teknolojiler
- Java 17
- Spring Boot 3.x
- Spring Data JPA (PostgreSQL)
- Spring Cloud OpenFeign
- Lombok
- SpringDoc OpenAPI (Swagger UI)
- Maven

---

## 📦 Katmanlar ve Sorumluluklar

### Controller
- Sadece HTTP request/response yönetir, business logic içermez.
- Request'i uygun service metoduna yönlendirir.
- Hata yönetimi (400/500) ve başarılı işlemler için uygun response döner.

### Service
- Tüm iş mantığı burada.
- Sepete ürün ekleme/çıkarma, ürün validasyonu, stok güncelleme, toplam tutar hesaplama, sipariş işlemleri burada.
- Dış servislerle (Product, Inventory) Feign Client ile haberleşir.
- Null ve hata kontrolleri içerir.

### Model
- **Entity:** CardEntity (id, owner, heldProducts)
- **Request:** AddProductRequest, RemoveProductRequest, UserRequest, ProductRequest
- **Response:** CartSummaryResponse
- **Enum:** UserType (CUSTOMER, PRODUCER)
- **Repository:** CardRepository (owner'a göre kart bulma)

### Feign Client'lar
- **ProductService:** Ürün fiyatı ve ürün oluşturma için
- **InventoryService:** Stok artırma/azaltma/sorgulama için

---

## 🚀 Başlıca Özellikler
- Kullanıcı oluşturma (`/api/new-user`)
- Sepete ürün ekleme (`/api/add-product`)
- Sepetten ürün çıkarma (`/api/remove-product`)
- Sepet görüntüleme (`/api/cart/{userName}`)
- Sepet toplam tutarı (`/api/cart/total/{userName}`)
- Detaylı sepet özeti (`/api/cart/summary/{userName}`)
- Sipariş verme (`/api/place-order`)
- Sipariş ID'si alma (`/api/get-order-id`)

---

## 🔄 İş Akışı (Senaryo ve Adımlar)

### 1. Kullanıcı Oluşturma
- Kullanıcı `/api/new-user` endpointine gerekli bilgileri gönderir.
- Service katmanı yeni bir CardEntity oluşturur ve veritabanına kaydeder.
- Her kullanıcıya bir sepet (kart) atanır.

### 2. Sepete Ürün Ekleme
- Kullanıcı `/api/add-product` endpointine ürün adı ve kullanıcı bilgileriyle istek atar.
- Service katmanı:
    1. Kullanıcıya ait kartı bulur.
    2. **Product Service**'e GET `/api/check-product-price?name=...` ile ürünün varlığını ve fiyatını sorar.
    3. Ürün geçerliyse sepete ekler.
    4. **Inventory Service**'e POST `/api/decrease-inventory-count` ile stok azaltma isteği gönderir.
    5. Sepet güncellenir ve kaydedilir.
    6. Hatalı ürün adı veya stok hatası varsa uygun hata mesajı döner.

### 3. Sepetten Ürün Çıkarma
- Kullanıcı `/api/remove-product` endpointine ürün adı ve kullanıcı bilgileriyle istek atar.
- Service katmanı:
    1. Kullanıcıya ait kartı bulur.
    2. Ürün sepette varsa çıkarır.
    3. **Inventory Service**'e POST `/api/increase-inventory-count` ile stok artırma isteği gönderir.
    4. Sepet güncellenir ve kaydedilir.
    5. Ürün sepette yoksa hata mesajı döner.

### 4. Sepeti Görüntüleme
- Kullanıcı `/api/cart/{userName}` endpointine GET isteği atar.
- Service katmanı ilgili kullanıcının kartındaki ürün listesini döner.

### 5. Sepet Toplam Tutarı
- Kullanıcı `/api/cart/total/{userName}` endpointine GET isteği atar.
- Service katmanı:
    1. Sepetteki her ürün için **Product Service**'ten fiyat bilgisini çeker.
    2. Tüm fiyatları toplayıp toplam tutarı döner.

### 6. Detaylı Sepet Özeti
- Kullanıcı `/api/cart/summary/{userName}` endpointine GET isteği atar.
- Service katmanı:
    1. Sepetteki ürünleri ve toplam tutarı hesaplar.
    2. Ürün sayısı, toplam tutar ve ürün listesini döner.

### 7. Sipariş Verme
- Kullanıcı `/api/place-order` endpointine kullanıcı adı ile POST isteği atar.
- Service katmanı:
    1. Sepetteki ürünleri döner.
    2. Sepeti temizler (heldProducts boşaltılır).
    3. Sepet kaydedilir.

### 8. Sipariş ID'si Alma
- Kullanıcı `/api/get-order-id` endpointine kullanıcı adı ile POST isteği atar.
- Service katmanı ilgili kullanıcının kartının ID'sini döner.

---

## 🔗 Dış Servislerle Entegrasyon
- **Product Service:**
  - GET `/api/check-product-price?name=...` (ürün fiyatı ve varlığı kontrolü)
  - POST `/api/create-product` (yeni ürün oluşturma)
- **Inventory Service:**
  - POST `/api/decrease-inventory-count` (stok azaltma)
  - POST `/api/increase-inventory-count` (stok artırma)
  - POST `/api/get-inventory-count` (stok sorgulama)
- **User Auth Service:**
  - Kullanıcı doğrulama ve bilgi çekme için (token ile)

---

## 🛡️ Hata Yönetimi ve Güvenlik
- Tüm servislerde null pointer ve invalid data kontrolleri var.
- Hatalı ürün adı, stok hatası, kullanıcı bulunamama gibi durumlarda uygun hata mesajı ve HTTP kodu döner.
- Feign Client ile yapılan dış servis çağrılarında try-catch ile hata yönetimi sağlanır.
- Controller'da business logic yok, sadece yönlendirme ve hata yönetimi var.

---

## 🧪 Örnek Kullanım

### Kullanıcı oluşturma
```json
POST /api/new-user
{
  "name": "john",
  "email": "john@example.com",
  "addresses": "adres",
  "userType": "CUSTOMER",
  "password": "1234"
}
```

### Ürün ekleme
```json
POST /api/add-product
{
  "productName": "konsol",
  "userName": "john",
  "email": "john@example.com",
  "addresses": "adres",
  "userType": "CUSTOMER",
  "password": "1234"
}
```

### Sepeti görüntüleme
```
GET /api/cart/john
```

### Sepet toplam tutarı
```
GET /api/cart/total/john
```

### Detaylı sepet özeti
```
GET /api/cart/summary/john
```

### Sipariş verme
```
POST /api/place-order
"john"
```

---

## 🛠️ Geliştirme ve Test
- Proje Java 17 ile derlenmeli ve çalıştırılmalı.
- Tüm endpointler Swagger UI üzerinden test edilebilir:  
  `http://localhost:8083/swagger-ui.html`
- Hatalı isteklerde anlamlı hata mesajı ve uygun HTTP kodu döner.

---

## 💡 Gelecek için İyileştirme Fikirleri
- Ürün miktarı desteği (aynı üründen birden fazla ekleme)
- Sepet temizleme
- Sepet geçmişi ve sipariş geçmişi
- Token tabanlı kimlik doğrulama zorunluluğu
- Kampanya/indirim kodu desteği
- Event-driven mimari (sipariş sonrası event fırlatma)
- Redis ile cache desteği

---

## 📄 Lisans
MIT

## 📋 Proje Genel Bakış

**Shopping Card Microservice**, e-ticaret sistemlerinde kullanıcıların sepet yönetimi işlemlerini gerçekleştiren Spring Boot tabanlı bir mikroservistir. Kullanıcıların ürün ekleme, çıkarma, sepet görüntüleme, toplam tutar hesaplama ve sipariş verme işlemlerini yönetir.

## 🏗️ Mimari Yapı

### Teknolojiler
- **Java 24**
- **Spring Boot 3.3.0**
- **Spring Data JPA**
- **PostgreSQL**
- **Spring Cloud OpenFeign**
- **Lombok**
- **SpringDoc OpenAPI**

### Mikroservis Mimarisi
Bu servis, aşağıdaki mikroservislerle entegre çalışır:
- **User Authentication & Management Service** (Port: 8084)
- **Product Service** (Port: 8085)
- **Inventory Service** (Port: 8086)

## 🚀 Özellikler

### ✅ Mevcut Özellikler
- ✅ Kullanıcı oluşturma
- ✅ Sepete ürün ekleme
- ✅ Sepetten ürün çıkarma
- ✅ Sepet görüntüleme
- ✅ Sepet toplam tutarı hesaplama
- ✅ Detaylı sepet özeti
- ✅ Sipariş verme
- ✅ Sipariş ID'si alma
- ✅ Stok entegrasyonu
- ✅ Ürün fiyat entegrasyonu
- ✅ Null güvenlik kontrolleri
- ✅ Hata yönetimi

## 📊 Veri Modeli

### CardEntity
```java
@Entity
@Table(name = "cards")
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String owner;
    
    @Column
    List<String> heldProducts;
}
```

### UserRequest
```java
public class UserRequest {
    private String name;
    private String email;
    private String addresses;
    private UserType userType; // CUSTOMER, PRODUCER
    private String password;
}
```

### UserType Enum
```java
public enum UserType {
    CUSTOMER,
    PRODUCER
}
```

## 🔌 API Endpoints

### Base URL: `http://localhost:8083/api`

| HTTP Method | Endpoint | Açıklama | Request Body | Response |
|-------------|----------|----------|--------------|----------|
| POST | `/add-product` | Sepete ürün ekleme | `ProductRequest`, `UserRequest` | - |
| POST | `/remove-product` | Sepetten ürün çıkarma | `ProductRequest`, `UserRequest` | - |
| POST | `/new-user` | Yeni kullanıcı oluşturma | `UserRequest` | - |
| POST | `/place-order` | Sipariş verme | `String userName` | `List<String>` |
| POST | `/get-order-id` | Sipariş ID'si alma | `String userName` | `Long` |
| GET | `/cart/{userName}` | Sepet görüntüleme | - | `List<String>` |
| GET | `/cart/total/{userName}` | Sepet toplam tutarı | - | `Double` |
| GET | `/cart/summary/{userName}` | Detaylı sepet özeti | - | `CartSummaryResponse` |

## 📝 API Detayları

### 1. Kullanıcı Oluşturma
```bash
POST /api/new-user
Content-Type: application/json

{
    "name": "john_doe",
    "email": "john@example.com",
    "addresses": "123 Main St, City",
    "userType": "CUSTOMER",
    "password": "password123"
}
```

### 2. Sepete Ürün Ekleme
```bash
POST /api/add-product
Content-Type: application/json

{
    "productName": "iPhone 15"
}
```

### 3. Sepet Görüntüleme
```bash
GET /api/cart/john_doe
```

**Response:**
```json
[
    "iPhone 15",
    "MacBook Pro",
    "AirPods"
]
```

### 4. Sepet Toplam Tutarı
```bash
GET /api/cart/total/john_doe
```

**Response:**
```json
1250.0
```

### 5. Detaylı Sepet Özeti
```bash
GET /api/cart/summary/john_doe
```

**Response:**
```json
{
    "userName": "john_doe",
    "products": ["iPhone 15", "MacBook Pro", "AirPods"],
    "totalAmount": 1250.0,
    "itemCount": 3
}
```

### 6. Sipariş Verme
```bash
POST /api/place-order
Content-Type: application/json

"john_doe"
```

**Response:**
```json
[
    "iPhone 15",
    "MacBook Pro", 
    "AirPods"
]
```

## 🔗 Dış Servis Entegrasyonları

### Product Service (Port: 8085)
- **Ürün fiyat sorgulama**: `POST /check-product-price`
- **Ürün oluşturma**: `POST /create-product`

### Inventory Service (Port: 8086)
- **Stok azaltma**: `POST /decrease-inventory-count`
- **Stok artırma**: `POST /increase-inventory-count`
- **Stok sorgulama**: `POST /get-inventory-count`

## 🛡️ Güvenlik ve Hata Yönetimi

### Null Güvenlik Kontrolleri
- Tüm metodlarda kullanıcı kartı null kontrolü
- Ürün listesi null kontrolü
- Uygun hata mesajları ve log kayıtları

### Hata Senaryoları
- **Var olmayan kullanıcı**: Boş liste/0.0 döner
- **Boş sepet**: 0.0 toplam, boş liste
- **Product Service erişim hatası**: Log kaydı, hesaplamaya devam eder

## 🗄️ Veritabanı

### PostgreSQL Konfigürasyonu
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

### Tablo Yapısı
```sql
CREATE TABLE cards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner VARCHAR(255),
    held_products TEXT
);
```

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- Java 24
- Maven 3.6+
- PostgreSQL
- Docker (opsiyonel)

### Adımlar

1. **Projeyi klonlayın**
```bash
git clone <repository-url>
cd card
```

2. **Veritabanını hazırlayın**
```bash
# PostgreSQL'de veritabanı oluşturun
createdb postgres
```

3. **Bağımlılıkları yükleyin**
```bash
mvn clean install
```

4. **Uygulamayı çalıştırın**
```bash
mvn spring-boot:run
```

5. **API Dokümantasyonuna erişin**
```
http://localhost:8083/swagger-ui.html
```

## 🧪 Test Senaryoları

### 1. Kullanıcı Oluşturma ve Sepet İşlemleri
```bash
# 1. Kullanıcı oluştur
POST /api/new-user
{"name": "test_user", "email": "test@example.com", "userType": "CUSTOMER"}

# 2. Ürün ekle
POST /api/add-product
{"productName": "Test Product"}

# 3. Sepeti görüntüle
GET /api/cart/test_user

# 4. Toplam tutarı hesapla
GET /api/cart/total/test_user

# 5. Sipariş ver
POST /api/place-order
"test_user"
```

### 2. Hata Senaryoları
```bash
# Var olmayan kullanıcı
GET /api/cart/nonexistent_user
# Response: []

# Boş sepet toplamı
GET /api/cart/total/empty_user
# Response: 0.0
```

## 📈 Performans ve Ölçeklenebilirlik

### Mevcut Durum
- ✅ Temel CRUD işlemleri
- ✅ Mikroservis entegrasyonu
- ✅ Null güvenlik kontrolleri
- ✅ Hata yönetimi

### Gelecek İyileştirmeler
- 🔄 Cache sistemi (Redis)
- 🔄 Event-driven mimari
- 🔄 Rate limiting
- 🔄 Token tabanlı kimlik doğrulama
- 🔄 Ürün miktarı desteği
- 🔄 Sepet geçmişi
- 🔄 Kampanya/indirim sistemi

## 🐛 Bilinen Sorunlar

- Product Service erişim hatası durumunda fiyat hesaplama devam eder
- Ürün miktarı desteği henüz mevcut değil
- Token tabanlı kimlik doğrulama henüz implement edilmedi

## 📞 İletişim

Proje hakkında sorularınız için:
- **Email**: [your-email@example.com]
- **Repository**: [repository-url]

## 📄 Lisans

Bu proje [MIT License](LICENSE) altında lisanslanmıştır.

---

**Son Güncelleme**: 2024
**Versiyon**: 1.0.0 