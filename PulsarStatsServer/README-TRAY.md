# System Monitor Server v2.0 - Sistem Tepsisi Sürümü

## 🎯 Yenilikler (v2.0)

### ✨ Sistem Tepsisi (System Tray) Desteği
- Uygulama artık Windows sistem tepsisinde çalışır
- Konsolsuz, arka planda sessiz çalışma
- Sağ tık menüsü ile kolay erişim

### ⚙️ Yeni Özellikler

#### 1. Sağ Tık Menüsü
- **📊 Sunucu Bilgisi**: Sunucu durumu ve bağlantı bilgileri
- **🌐 Test Sayfası Aç**: Web tarayıcısında test client'ı açar
- **📝 Logları Göster**: (Gelecek sürümde aktif)

#### 2. Ayarlar Menüsü
- **Windows ile Başlat**: Bilgisayar açıldığında otomatik başlat
- **Gizli Modda Çalış**: Sistem tepsisinde bile görünmez (tamamen gizli)

#### 3. Gizli Mod Özelliği
- Aktif edildiğinde sistem tepsisinde icon görünmez
- Windows yeniden başlatılsa bile gizli kalır
- Ayar kayıt defterinde (Registry) saklanır
- Kapatmak için: Görev Yöneticisi'nden sonlandırın ve tekrar başlatıp ayarı kapatın

## 🚀 Kullanım

### İlk Başlatma
1. `SystemMonitorServer.exe` çalıştırın
2. Sistem tepsisinde (saat yanında) uygulama ikonu belirecek
3. İkona sağ tıklayın ve menüyü görün

### Sunucu Bilgilerini Görme
- İkona **çift tıklayın** veya
- Sağ tık → **Sunucu Bilgisi**

### Otomatik Başlatma Ayarı
1. Sağ tık → **Ayarlar** → **Windows ile Başlat**
2. İşaretli olması otomatik başlatmayı aktif eder
3. Artık Windows açıldığında sunucu otomatik başlayacak

### Gizli Mod (Stealth Mode)
⚠️ **DİKKAT**: Bu modu aktif etmeden önce dikkatli okuyun!

1. Sağ tık → **Ayarlar** → **Gizli Modda Çalış**
2. Onay mesajını okuyun ve "Tamam" deyin
3. Artık sistem tepsisinde icon **görünmeyecek**
4. Sunucu arka planda çalışmaya devam edecek

**Gizli Modu Kapatmak İçin**:
1. Görev Yöneticisi'ni açın (Ctrl+Shift+Esc)
2. "SystemMonitorServer.exe" işlemini bulun ve sonlandırın
3. Uygulamayı normal şekilde tekrar başlatın
4. Sağ tık → Ayarlar → "Gizli Modda Çalış" işaretini kaldırın

### Uygulamayı Kapatma
- Sağ tık → **Çıkış**
- Onay mesajında "Evet" deyin

## 📡 Bağlantı Bilgileri

- **URL**: `http://[BILGISAYAR_IP]:5000`
- **SignalR Hub**: `/systemhub`
- **Güncelleme Aralığı**: 3 saniye
- **Platform**: Windows (.NET 9.0)

## 🔧 Teknik Detaylar

### Kayıt Defteri (Registry) Konumları

**Otomatik Başlatma**:
```
HKEY_CURRENT_USER\SOFTWARE\Microsoft\Windows\CurrentVersion\Run
Anahtar: SystemMonitor
Değer: "C:\...\SystemMonitorServer.exe"
```

**Gizli Mod Ayarı**:
```
HKEY_CURRENT_USER\SOFTWARE\SystemMonitor
Anahtar: HideOnStartup
Değer: 1 (gizli) / 0 (görünür)
```

### Özellikler
- **Self-contained**: .NET runtime dahil, kurulum gerektirmez
- **Single-file**: Tek EXE dosyası
- **Admin rights**: Sıcaklık sensörleri için yönetici yetkisi önerilir
- **Windows Forms**: Native Windows UI
- **ASP.NET Core**: Web API ve SignalR backend

## 📂 Dosya Boyutu

- **EXE**: ~100-105 MB (self-contained, tüm bağımlılıklar dahil)
- **Runtime**: .NET 9.0 Windows

## 🐛 Sorun Giderme

### Sistem tepsisinde icon görünmüyor
- Ayarlar'da "Gizli Mod" aktif mi kontrol edin
- Görev Yöneticisi'nde uygulama çalışıyor mu bakın

### Windows ile başlamıyor
- Yönetici olarak çalıştırıp ayarı tekrar aktif edin
- Registry'de `Run` anahtarını manuel kontrol edin

### Sıcaklık verileri gelmiyor
- Uygulamayı **Yönetici olarak çalıştır**
- Donanımınız sıcaklık sensörlerini desteklemeyebilir

### Android app bağlanamıyor
- Windows Firewall'da port 5000 açık mı kontrol edin
- Aynı WiFi ağında olduğunuzdan emin olun
- IP adresini doğru girdiğinizi kontrol edin

## 📝 Değişiklik Günlüğü

### v2.0.0 (October 12, 2025)
- ✨ Sistem tepsisi (system tray) desteği eklendi
- ✨ Windows otomatik başlatma özelliği
- ✨ Gizli mod (stealth mode) özelliği
- ✨ Sağ tık menüsü ile kolay yönetim
- 🔧 Konsolsuz çalışma (WinExe)
- 🔧 Registry tabanlı ayar saklama

### v1.1.0 (October 12, 2025)
- 🐛 Network binding düzeltildi (0.0.0.0:5000)
- 🐛 JSON serialization düzeltildi (camelCase)
- 📱 Android app ile uyumluluk sağlandı

### v1.0.0 (October 12, 2025)
- 🎉 İlk sürüm
- ✅ CPU, RAM, Disk, Sıcaklık, Network monitoring
- ✅ SignalR WebSocket desteği
- ✅ Self-contained EXE

## 📞 Destek

Sorun veya öneri için GitHub Issues kullanın.

---

**© 2025 System Monitor Server**  
Developed with ❤️ using .NET 9.0
