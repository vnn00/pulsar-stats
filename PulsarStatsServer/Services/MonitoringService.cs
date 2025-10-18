using Microsoft.AspNetCore.SignalR;
using SystemMonitorServer.Hubs;
using SystemMonitorServer.Models;
using System.Text.Json;

namespace SystemMonitorServer.Services;

public class MonitoringService : BackgroundService
{
    private readonly IHubContext<SystemMonitorHub> _hubContext;
    private readonly ILogger<MonitoringService> _logger;
    private readonly JsonSerializerOptions _jsonOptions;
    private readonly CpuMonitor _cpuMonitor;
    private readonly MemoryMonitor _memoryMonitor;
    private readonly DiskMonitor _diskMonitor;
    private readonly HardwareSensorMonitor _hardwareSensorMonitor;
    private readonly NetworkMonitor _networkMonitor;
    private readonly int _updateIntervalSeconds;

    public MonitoringService(
        IHubContext<SystemMonitorHub> hubContext,
        ILogger<MonitoringService> logger,
        IConfiguration configuration)
    {
        _hubContext = hubContext;
        _logger = logger;
        _cpuMonitor = new CpuMonitor();
        _memoryMonitor = new MemoryMonitor();
        _diskMonitor = new DiskMonitor();
        _hardwareSensorMonitor = new HardwareSensorMonitor();
        _networkMonitor = new NetworkMonitor();
        
        // JSON serialization options (camelCase)
        _jsonOptions = new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
            WriteIndented = false
        };
        
        _updateIntervalSeconds = configuration.GetValue<int>("Monitoring:UpdateIntervalSeconds", 1); // Changed from 3 to 1 - clients can throttle
        _logger.LogInformation($"MonitoringService initialized with {_updateIntervalSeconds}s update interval");
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        _logger.LogInformation("MonitoringService is starting...");

        // Admin kontrolü
        CheckAdminRights();

        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                // TEST: Basit bir ping event gönder
                await _hubContext.Clients.All.SendAsync("Ping", "Hello from server!");
                _logger.LogInformation("🏓 Ping event sent");
                
                var systemData = await CollectSystemDataAsync();
                await BroadcastSystemDataAsync(systemData);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error collecting or broadcasting system data");
            }

            await Task.Delay(TimeSpan.FromSeconds(_updateIntervalSeconds), stoppingToken);
        }

        _logger.LogInformation("MonitoringService is stopping...");
    }

    private async Task<SystemData> CollectSystemDataAsync()
    {
        // Tüm verileri paralel topla
        var cpuTask = _cpuMonitor.GetCpuInfoAsync();
        var memoryTask = _memoryMonitor.GetMemoryInfoAsync();
        var diskTask = _diskMonitor.GetDiskInfoAsync();
        var temperatureTask = _hardwareSensorMonitor.GetTemperatureInfoAsync();
        var networkTask = _networkMonitor.GetNetworkInfoAsync();
        var gpuTask = _hardwareSensorMonitor.GetGpuInfoAsync();

        await Task.WhenAll(cpuTask, memoryTask, diskTask, temperatureTask, networkTask, gpuTask);

        var cpuInfo = await cpuTask;
        
        return new SystemData
        {
            Timestamp = DateTime.Now,
            CpuInfo = cpuInfo,
            MemoryInfo = await memoryTask,
            DiskInfo = await diskTask,
            TemperatureInfo = await temperatureTask,
            NetworkInfo = await networkTask,
            GpuInfo = await gpuTask,
            SystemInfo = GetSystemInfo(cpuInfo)
        };
    }

    private SystemInfo GetSystemInfo(CpuInfo? cpuInfo)
    {
        // Get total RAM
        var totalMemoryGB = Math.Round(GC.GetGCMemoryInfo().TotalAvailableMemoryBytes / (1024.0 * 1024.0 * 1024.0), 2);
        
        return new SystemInfo
        {
            OsVersion = Environment.OSVersion.ToString(),
            ComputerName = Environment.MachineName,
            UserName = Environment.UserName,
            Uptime = TimeSpan.FromMilliseconds(Environment.TickCount64),
            CpuArchitecture = Environment.Is64BitOperatingSystem ? "x64" : "x86",
            TotalRamGB = totalMemoryGB,
            CpuCores = cpuInfo?.CoreCount ?? Environment.ProcessorCount
        };
    }

    private async Task BroadcastSystemDataAsync(SystemData data)
    {
        try
        {
            var clientCount = _hubContext.Clients.All != null ? "Available" : "NULL";
            _logger.LogInformation($"📡 Broadcasting to clients (Clients.All: {clientCount})");
            _logger.LogInformation($"📊 Data: CPU={data.CpuInfo?.UsagePercent:F1}%, RAM={data.MemoryInfo?.UsedGB:F1}GB");
            
            // CRITICAL FIX: SignalR Java Client için JSON string olarak gönder!
            string jsonData = JsonSerializer.Serialize(data, _jsonOptions);
            _logger.LogInformation($"📦 JSON Length: {jsonData.Length} chars");
            
            await _hubContext.Clients.All.SendAsync("ReceiveSystemUpdate", jsonData);
            
            _logger.LogInformation("✅ Broadcast completed successfully");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "❌ Error broadcasting system data");
        }
    }

    private void CheckAdminRights()
    {
        try
        {
            var isAdmin = System.Security.Principal.WindowsIdentity.GetCurrent()
                .Owner?.IsWellKnown(System.Security.Principal.WellKnownSidType.BuiltinAdministratorsSid) ?? false;

            if (!isAdmin)
            {
                _logger.LogWarning("⚠️  Application is NOT running with administrator privileges!");
                _logger.LogWarning("⚠️  Hardware sensors (temperatures) may not be available.");
                _logger.LogWarning("⚠️  Please run as administrator for full functionality.");
            }
            else
            {
                _logger.LogInformation("✓ Application is running with administrator privileges");
            }
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error checking admin rights");
        }
    }

    public override void Dispose()
    {
        _cpuMonitor?.Dispose();
        _memoryMonitor?.Dispose();
        _hardwareSensorMonitor?.Dispose();
        base.Dispose();
    }
}
