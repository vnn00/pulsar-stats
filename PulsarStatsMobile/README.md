# System Monitor Mobile (Android)

Native Android uygulaması - PC sistem izleme uygulaması.

## Özellikler

✅ **Gerçek Zamanlı İzleme**
- CPU kullanımı ve frekans
- RAM kullanımı (kullanılan/toplam)
- Disk kullanımı (tüm sürücüler)
- Sıcaklıklar (CPU, GPU, Anakart)
- Ağ hızları (indirme/yükleme)

✅ **Akıllı Bildirimler**
- Ayarlanabilir eşik değerleri
- CPU kullanım bildirimi (%50-100)
- RAM kullanım bildirimi (%50-100)
- CPU sıcaklık bildirimi (60-100°C)
- GPU sıcaklık bildirimi (60-100°C)
- Disk sıcaklık bildirimi (40-80°C)
- Ses ve titreşim ayarları

✅ **Kullanıcı Dostu**
- Material Design 3 arayüz
- Swipe-to-refresh
- Sunucu IP adresi kaydetme
- Otomatik yeniden bağlanma

## Gereksinimler

### Geliştirme
- Android Studio Hedgehog (2023.1.1) veya üzeri
- JDK 21 (LTS)
- Android SDK (API 24-34)
- Gradle 8.13 (wrapper dahil)

### Cihaz
- Android 7.0 (API 24) veya üzeri
- İnternet/WiFi bağlantısı (LAN)

## Kurulum & Build

### 1. Projeyi Aç
```bash
# Android Studio'da: File > Open > SystemMonitorMobile klasörünü seç
```

### 2. Gradle Sync
Android Studio otomatik olarak dependencies'leri indirecektir.

### 3. APK Build

#### Debug APK (Test için)
```bash
# Terminal'de:
cd SystemMonitorMobile
gradlew assembleDebug

# APK yeri: app\build\outputs\apk\debug\app-debug.apk
```

#### Release APK (Yayın için)
```bash
gradlew assembleRelease

# APK yeri: app\build\outputs\apk\release\app-release-unsigned.apk
```

### 4. Cihaza Yükleme

**USB ile:**
```bash
adb install app\build\outputs\apk\debug\app-debug.apk
```

**Manuel:**
1. APK dosyasını telefona kopyala
2. Dosya yöneticisinden APK'yı aç
3. "Bilinmeyen kaynaklardan yükleme"ye izin ver
4. Kur

## Kullanım

### İlk Çalıştırma

1. **Sunucu Başlat**
   - PC'de `SystemMonitorServer.exe` çalıştır
   - Sunucunun çalıştığı IP adresini not et (örn: `192.168.1.100`)

2. **Mobil Uygulamayı Aç**
   - IP adresi gir (örn: `192.168.1.100`)
   - Port: `5000` (default)
   - "Bağlan" butonuna tıkla

3. **İzlemeye Başla**
   - Bağlantı başarılı olunca ana ekran açılır
   - Tüm metrikler 3 saniyede bir güncellenir

### Ayarlar

**Bildirim Eşiklerini Ayarla:**
1. Sağ üst köşede ⚙️ simgesine tıkla
2. Her metrik için:
   - Bildirimi aç/kapa
   - Eşik değerini ayarla (slider ile)
3. Ses/Titreşim tercihlerini seç
4. "Kaydet" butonuna tıkla

**Sunucu Değiştir:**
1. Ayarlar > "Sunucu Adresini Değiştir"
2. Yeni IP adresi gir

### Özellikler

- **Swipe Down**: Bağlantıyı yenile
- **⚙️ İkonu**: Ayarlar ekranını aç
- **🔄 İkonu**: Manuel yenileme

## Sorun Giderme

### Bağlantı Hatası

**"Bağlantı başarısız!"**
- ✅ PC ve telefon aynı WiFi'ye bağlı mı?
- ✅ Sunucu çalışıyor mu?
- ✅ IP adresi doğru mu?
- ✅ Port 5000 açık mı?
- ✅ Windows Firewall sunucuyu engelliyor mu?

**IP Adresini Bulma (PC'de):**
```powershell
ipconfig
# "IPv4 Address" satırına bak (örn: 192.168.1.100)
```

### Bildirimler Gelmiyor

- ✅ Ayarlar > Bildirimler etkin mi?
- ✅ Android sistem ayarlarından uygulama bildirimleri açık mı?
- ✅ Eşik değerleri çok yüksek mı?
- ✅ Pil tasarrufu modu kapalı mı?

### Uygulamayı Arka Planda Çalıştırma

Android 12+:
1. Ayarlar > Uygulamalar > System Monitor
2. Pil > Kısıtlanmamış
3. Bildirimler > Tüm bildirimlere izin ver

## Teknik Detaylar

### Kullanılan Teknolojiler
- **Dil**: Java
- **UI**: Material Design 3
- **WebSocket**: SignalR Java Client 7.0
- **JSON**: Gson 2.10.1
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

### Mimari
```
App
├── Activities
│   ├── SplashActivity (IP giriş)
│   ├── MainActivity (monitoring)
│   └── SettingsActivity (ayarlar)
├── Services
│   └── MonitoringService (background)
├── Models
│   ├── SystemData
│   ├── CpuInfo, MemoryInfo, etc.
│   └── NotificationSettings
└── Utils
    ├── SignalRManager (WebSocket)
    └── PreferencesHelper (kayıt)
```

## Geliştirme Notları

### Dependencies Güncelleme
```gradle
// app/build.gradle
dependencies {
    implementation 'com.microsoft.signalr:signalr:7.0.0'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'com.google.android.material:material:1.11.0'
    // ...
}
```

### Build Variants
- **Debug**: Geliştirme için, ProGuard yok
- **Release**: Yayın için, ProGuard aktif

## Lisans
Bu proje özel kullanım içindir.

## Katkıda Bulunanlar
- Başak (Developer)

## Sürüm Geçmişi

### v1.0.0 (12 Ekim 2025)
- ✅ İlk sürüm
- ✅ Gerçek zamanlı izleme
- ✅ Bildirim sistemi
- ✅ Ayarlanabilir eşikler
- ✅ Material Design 3 UI

## İletişim
Sorularınız için GitHub Issues kullanabilirsiniz.

---

**Not:** Bu uygulama PC ile aynı WiFi ağında çalışacak şekilde tasarlanmıştır. İnternet üzerinden erişim için port forwarding ve güvenlik ayarları gereklidir.
