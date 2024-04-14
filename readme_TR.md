# kredinbizde (DefineX Final Project)

## Proje Açıklaması

Bu proje, kredi sektöründe faaliyet gösteren bankalar arasında ortak bir veritabanını kullanarak, müşteri bilgilerini çekmek ve kredi kartı veya kredi başvurularını işlemek amacıyla geliştirilmiştir. Merkezi bir veritabanı ve Akbank gibi servisler aracılığıyla bankalar, müşteri bilgilerine erişebilir ve başvuruları işleyebilirler. Proje, gateway üzerinden yönlendirme yapar, arka planda API'ler aracılığıyla iletişim kurar ve MySQL veritabanını kullanır. Projede RabbitMQ kullanımına örnek olması amacıyla veritabanında yapılan her işlem RabbitMQ aracılığıyla log bilgilerini notification-service'e iletir ve bir MySQL veritabanında bu bilgileri saklanır. Bu sebeple sorgular biraz zaman almaktadır. Dilerseniz bu satırları comment satırına alıp, daha hızlı sorgular gerçekleştirebilirsiniz. "kredinbizde-service" ve "akbank-service" için service katmanında gerekli olan tüm unit testler başarıyla çalışmaktadır.

### Projeyi Çalıştırma Adımları

1. "kredinbizde-service", "akbank-service" ve "notification-service" dizinlerindeki docker-compose dosyalarını çalıştırarak gerekli veritabanı ve servislerin kurulumunu gerçekleştirin.
2. İlk olarak, "kredinbizde-discovery" projesini çalıştırın.(Euroka sunucusu)
3. "kredinbizde-gw" (gateway) projesini çalıştırın.
4. "kredinbizde-service" projesini çalıştırın. Veritabanları ve gerekli servislerin hazır ve çalışıyor olduğundan emin olun.
5. "akbank-service" projesini çalıştırın.
6. "notification-service" projesini çalıştırın. Bu servis, rabbitMQ tarafından gelen log mesajlarını tüketici olarak alan ve bunları MySQL veritabanına kaydeder.
7. Projeler sorunsuz çalışıyorsa, readme dosyası gibi ana dizinde bulunan "postman_queries.json" dosyasındaki örnek sorguları Postman'e aktararak sorgularınızı gerçekleştirebilirsiniz.

#### Görevler

- "kredinbizde-discovery" Euroka sunucusunun olduğu projedir.
- "kredinbizde-gw" projelerin birbiriyle iletişimini kolaylaştıran ve routing yapan Gateway projesidir.
- "akbank-service" ise Kredi Kartı ve Kredi başvurusu yapmak ve bunları sorgulamak için kullanılan servistir. Bir kullanıcın adına başvuru yapabilmek için, o kullanıcının ilgili bankanın müşterisi olması gerekmektedir.
- "kredinbizde-service" kullanıcı ve banka bilgilerinin MySQL veritabanında saklandığı ve yönetildiği ana projedir. 
- "notification-service" RabbitMQ aracılığıyla gelen log mesajlarını tüketen ve bunları MySQL veritabanına kaydeden bir servistir.

##### Notlar

- "akbank-service" içindeki sorguların doğru çalışabilmesi için, "kredinbizde" veritabanında bulunan "banks" tablosundaki Akbank'ın ID'sinin, "akbank-service" projesinde manuel olarak ayarlanması gerekmektedir. Varsayılan ID değeri 2'dir. Yani, Akbank'ın ID'si veritabanında 2 değilse, sorgular yanlış çalışabilir veya hata alabilirsiniz.
- delete user özelliği sadece akbank-service'deki unit test için yaratılan objelerin silinmesi için eklenmiştir. Herhangi bir uygulamada (özellikle önemli ve canlı projelerde) veritabanından silme özelliği yerine, active boolean'ı tutmak ve onu değiştirmek daha doğru bir yaklaşım olacaktır.