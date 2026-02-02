# ⚔️ NyxxMetin2 | Minecraft Upgrade & Blacksmith System

![Version](https://img.shields.io/badge/Version-1.0.0-gold)
![API](https://img.shields.io/badge/API-Paper/Spigot-blue)
![Java](https://img.shields.io/badge/Java-21+-red)

Minecraft dünyasına **Metin2 TR** ruhunu getiren, gelişmiş bir eşya yükseltme ve demirci sistemidir. Bu eklenti, klasik survival deneyimine risk, strateji ve büyük ödüller katar.

---

## 🔥 Temel Özellikler

### ⚒️ Gelişmiş Demirci Menüsü
- **Akıllı GUI:** `/demirci` komutuyla açılan menü, koyulan eşyayı tanır ve geliştirme durumuna göre dinamik buton (Örs/Bariyer) oluşturur.
- **Sınıf Kısıtlaması:** Sadece **Kılıç, Balta, Yay ve Zırh** (Kask, Göğüslük, vb.) parçaları geliştirilebilir.
- **Eşya Güvenliği:** Menü kapatıldığında veya internet kesildiğinde slottaki eşyalar anında oyuncuya iade edilir.

### 📜 Kutsal Kağıt (Blessing Scroll)
- **Kırılma Koruması:** Eşyanın yanmasını engeller.
- **Düşürme Mantığı:** Geliştirme başarısız olursa eşya yok olmaz, sadece seviyesi 1 puan düşer.

### 📢 Duyuru & Atmosfer
- **Efsanevi Basımlar:** +7, +8 ve +9 basımları tüm sunucuya duyurulur ve özel bir ses efekti çalar.
- **Görsel Efektler:** Başarılı basımlarda köylü mutluluk partikülleri, başarısızlıkta ise eşya kırılma sesleri eklenmiştir.

---

## ⚙️ Yapılandırma (config.yml)

Sistem tamamen özelleştirilebilir bir yapıya sahiptir:
- **Şans Oranları:** Her seviye için ayrı başarı yüzdesi.
- **Materyaller:** Her seviye için gereken eşya ve miktar (Örn: +8 için 1x Netherite Külçesi).
- **Duyuru Sınırı:** Hangi seviyeden sonra tüm sunucuya mesaj gideceğini belirleme.

---

## 🚀 Komutlar & Yetkiler

| Komut | Açıklama | Yetki |
| :--- | :--- | :--- |
| `/demirci` | Demirci menüsünü açar. | `nyxxmetin2.player` |
| `/nyxxadmin give <oyuncu>` | Belirtilen oyuncuya Kutsal Kağıt verir. | `nyxxmetin2.admin` |
| `/nyxxadmin reload` | Config dosyasını yeniler. | `nyxxmetin2.admin` |

---

## 🛠️ Geliştiriciler İçin
Proje Maven kullanılarak geliştirilmiştir. Derlemek için:
1. Projeyi klonlayın.
2. `mvn clean package` komutunu çalıştırın.
3. `/target` klasöründeki `.jar` dosyasını sunucunuza yükleyin.

---
**Nyxx** tarafından geliştirilmiştir.  
*Minecraft artık sadece bir blok oyunu değil, bir efsane yazma sanatı!*
