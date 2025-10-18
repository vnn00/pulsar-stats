using System.Diagnostics;
using Microsoft.Win32;

namespace SystemMonitorServer;

public class TrayIconManager : IDisposable
{
    private readonly NotifyIcon _notifyIcon;
    private readonly ContextMenuStrip _contextMenu;
    private readonly WebApplication _webApp;
    private readonly ILogger<TrayIconManager> _logger;
    private bool _isHidden = false;
    private const string RegistryKey = @"SOFTWARE\SystemMonitor";
    private const string HiddenValueName = "HideOnStartup";

    public TrayIconManager(WebApplication webApp, ILogger<TrayIconManager> logger)
    {
        _webApp = webApp;
        _logger = logger;
        
        // Always show the tray icon (removed hidden mode feature to prevent confusion)
        _isHidden = false;
        
        _contextMenu = CreateContextMenu();
        _notifyIcon = CreateNotifyIcon();
        
        // Show startup notification
        ShowBalloonTip("System Monitor", "Sunucu sistem tepsisinde çalışıyor.", ToolTipIcon.Info);
    }

    private NotifyIcon CreateNotifyIcon()
    {
        var icon = new NotifyIcon
        {
            Icon = SystemIcons.Application, // Varsayılan uygulama ikonu
            ContextMenuStrip = _contextMenu,
            Text = "System Monitor Server",
            Visible = true // Always visible by default, even in hidden mode the icon should show
        };

        icon.DoubleClick += (s, e) => ShowServerInfo();
        
        return icon;
    }

    private ContextMenuStrip CreateContextMenu()
    {
        var menu = new ContextMenuStrip();
        
        // Sunucu Bilgisi
        var infoItem = new ToolStripMenuItem("📊 Sunucu Bilgisi");
        infoItem.Click += (s, e) => ShowServerInfo();
        menu.Items.Add(infoItem);
        
        menu.Items.Add(new ToolStripSeparator());
        
        // Tarayıcıda Aç
        var browserItem = new ToolStripMenuItem("🌐 Test Sayfası Aç");
        browserItem.Click += (s, e) => OpenInBrowser();
        menu.Items.Add(browserItem);
        
        // Logları Göster
        var logsItem = new ToolStripMenuItem("📝 Logları Göster");
        logsItem.Click += (s, e) => ShowLogs();
        menu.Items.Add(logsItem);
        
        menu.Items.Add(new ToolStripSeparator());
        
        // Ayarlar
        var settingsItem = new ToolStripMenuItem("⚙️ Ayarlar");
        
        // Otomatik Başlat
        var autoStartItem = new ToolStripMenuItem("Windows ile Başlat");
        autoStartItem.CheckOnClick = true;
        autoStartItem.Checked = IsAutoStartEnabled();
        autoStartItem.Click += (s, e) => ToggleAutoStart(autoStartItem.Checked);
        settingsItem.DropDownItems.Add(autoStartItem);
        
        menu.Items.Add(settingsItem);
        
        menu.Items.Add(new ToolStripSeparator());
        
        // Çıkış
        var exitItem = new ToolStripMenuItem("❌ Çıkış");
        exitItem.Click += (s, e) => ExitApplication();
        menu.Items.Add(exitItem);
        
        return menu;
    }

    private void ShowServerInfo()
    {
        var message = $"System Monitor Server\n\n" +
                     $"✅ Durum: Çalışıyor\n" +
                     $"🌐 URL: http://localhost:5000\n" +
                     $"📡 SignalR Hub: /systemhub\n" +
                     $"🔄 Güncelleme: 3 saniye\n\n" +
                     $"Bağlantı için Android uygulamasında\n" +
                     $"sunucu IP adresinizi kullanın.";
        
        MessageBox.Show(message, "System Monitor - Sunucu Bilgisi", 
            MessageBoxButtons.OK, MessageBoxIcon.Information);
    }

    private void OpenInBrowser()
    {
        try
        {
            var testHtmlPath = Path.Combine(AppContext.BaseDirectory, "test-client.html");
            if (File.Exists(testHtmlPath))
            {
                Process.Start(new ProcessStartInfo
                {
                    FileName = testHtmlPath,
                    UseShellExecute = true
                });
            }
            else
            {
                // Test dosyası yoksa localhost'u aç
                Process.Start(new ProcessStartInfo
                {
                    FileName = "http://localhost:5000",
                    UseShellExecute = true
                });
            }
        }
        catch (Exception ex)
        {
            MessageBox.Show($"Tarayıcı açılamadı: {ex.Message}", "Hata", 
                MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private void ShowLogs()
    {
        MessageBox.Show(
            "Log dosyaları şu anda konsol çıktısında görüntüleniyor.\n\n" +
            "Gelecek sürümlerde log dosyası desteği eklenecek.",
            "Loglar", MessageBoxButtons.OK, MessageBoxIcon.Information);
    }

    [System.Runtime.Versioning.SupportedOSPlatform("windows")]
    private bool IsAutoStartEnabled()
    {
        try
        {
            using var key = Registry.CurrentUser.OpenSubKey(@"SOFTWARE\Microsoft\Windows\CurrentVersion\Run", false);
            return key?.GetValue("SystemMonitor") != null;
        }
        catch
        {
            return false;
        }
    }

    [System.Runtime.Versioning.SupportedOSPlatform("windows")]
    private void ToggleAutoStart(bool enable)
    {
        try
        {
            using var key = Registry.CurrentUser.OpenSubKey(@"SOFTWARE\Microsoft\Windows\CurrentVersion\Run", true);
            if (key != null)
            {
                if (enable)
                {
                    var exePath = Process.GetCurrentProcess().MainModule?.FileName ?? "";
                    key.SetValue("SystemMonitor", $"\"{exePath}\"");
                    ShowBalloonTip("Otomatik Başlatma", "Windows başlangıcında otomatik başlatma aktif.", ToolTipIcon.Info);
                }
                else
                {
                    key.DeleteValue("SystemMonitor", false);
                    ShowBalloonTip("Otomatik Başlatma", "Windows başlangıcında otomatik başlatma devre dışı.", ToolTipIcon.Info);
                }
            }
        }
        catch (Exception ex)
        {
            MessageBox.Show($"Otomatik başlatma ayarı değiştirilemedi: {ex.Message}", 
                "Hata", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private bool LoadHiddenState()
    {
        try
        {
            using var key = Registry.CurrentUser.OpenSubKey(RegistryKey, false);
            if (key != null)
            {
                var value = key.GetValue(HiddenValueName);
                return value is int intValue && intValue == 1;
            }
        }
        catch (Exception ex)
        {
            _logger.LogWarning(ex, "Gizli mod durumu okunamadı");
        }
        return false;
    }

    private void SaveHiddenState(bool hidden)
    {
        try
        {
            using var key = Registry.CurrentUser.CreateSubKey(RegistryKey);
            key.SetValue(HiddenValueName, hidden ? 1 : 0);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Gizli mod durumu kaydedilemedi");
            MessageBox.Show($"Ayar kaydedilemedi: {ex.Message}", 
                "Hata", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private void ToggleHiddenMode(bool hide)
    {
        _isHidden = hide;
        SaveHiddenState(hide);
        
        if (hide)
        {
            _notifyIcon.Visible = false;
            MessageBox.Show(
                "Gizli mod aktif edildi!\n\n" +
                "Uygulama artık sistem tepsisinde gözükmeyecek.\n" +
                "Uygulamayı kapatmak için Görev Yöneticisi'ni kullanın.\n\n" +
                "Gizli modu devre dışı bırakmak için:\n" +
                "1. Görev Yöneticisi'nden uygulamayı kapatın\n" +
                "2. Uygulamayı normal şekilde başlatın\n" +
                "3. Ayarlar'dan 'Gizli Modda Çalış' seçeneğini kaldırın",
                "Gizli Mod Aktif", MessageBoxButtons.OK, MessageBoxIcon.Warning);
        }
        else
        {
            _notifyIcon.Visible = true;
            ShowBalloonTip("Gizli Mod", "Uygulama artık sistem tepsisinde görünüyor.", ToolTipIcon.Info);
        }
    }

    private void ShowBalloonTip(string title, string message, ToolTipIcon icon)
    {
        if (!_isHidden && _notifyIcon.Visible)
        {
            _notifyIcon.ShowBalloonTip(3000, title, message, icon);
        }
    }

    private void ExitApplication()
    {
        var result = MessageBox.Show(
            "System Monitor sunucusunu kapatmak istediğinizden emin misiniz?",
            "Çıkış Onayı",
            MessageBoxButtons.YesNo,
            MessageBoxIcon.Question);
        
        if (result == DialogResult.Yes)
        {
            _logger.LogInformation("Kullanıcı sunucuyu kapattı");
            Application.Exit();
        }
    }

    public void Dispose()
    {
        _notifyIcon?.Dispose();
        _contextMenu?.Dispose();
    }
}
