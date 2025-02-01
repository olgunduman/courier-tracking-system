# Courier Tracking Application

Bu proje, kuryelerin coğrafi konum bilgilerini (zaman, kurye, enlem, boylam) akış halinde alarak, belirli mağaza (Migros) lokasyonlarına girişlerini loglayan ve her kuryenin toplam kat ettiği mesafeyi sorgulama imkanı sağlayan bir RESTful web uygulamasıdır. Uygulama, Java ve Spring Boot kullanılarak geliştirilmiş olup, H2 veritabanı in-memory olarak kullanılmaktadır.

## Özellikler

- **Mağaza Girişi Loglama:**
    - Kuryenin, Migros mağazalarının 100 metre yarıçapına girdiğinde bu giriş loglanır.
    - Aynı mağazaya 1 dakikadan az sürede yapılan tekrar girişler loglanmaz.

- **Toplam Mesafe Sorgulama:**
    - Herhangi bir kurye için toplam kat edilen mesafe sorgulanabilir.

- **Asenkron İşleme:**
    - Gelen konum güncellemeleri, bir kuyruk yapısı kullanılarak asenkron olarak işlenir. Bu sayede yüksek trafikli durumlarda da sistem yanıt verebilir.

- **Tasarım Desenleri:**
    - **Singleton Pattern:** Uygulama boyunca tek örnek olarak yönetilen servisler (StoreService, CourierQueueService) kullanılır.
    - **Producer-Consumer (Observer) Pattern:** Gelen konum güncellemeleri Controller’dan kuyruğa eklenir ve arka planda işçi thread’ler tarafından işlenir.
    - **Command Pattern (Benzeri Yaklaşım):** İş mantığı, CourierLocationCommandProcessor sınıfında izole edilmiştir.

## Teknolojiler

- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database (in-memory)
- Maven
- Docker & Docker Compose
- SLF4J (Loglama için)

## Kurulum ve Çalıştırma

### 1. Yerel Ortamda Çalıştırma

#### a. Gerekli Ön Koşullar
- Java 17 veya üstü
- Maven

#### b. Projeyi Derleme

Terminal veya komut istemcisinde projenizin kök dizinine giderek aşağıdaki komutları çalıştırın:

```bash
mvn clean package

java -jar target/courier-tracking-system-0.0.1-SNAPSHOT.jar
```

#### c. Uygulaayı docker ile run edebilmek için aşağıdaki komutları sıryla çalıştırın:

```bash
docker build -t courier-tracking-system .

docker run -p 8080:8080 courier-tracking-system
```