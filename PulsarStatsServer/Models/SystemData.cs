namespace SystemMonitorServer.Models;

public class SystemData
{
    public DateTime Timestamp { get; set; }
    public CpuInfo? CpuInfo { get; set; }
    public MemoryInfo? MemoryInfo { get; set; }
    public List<DiskInfo>? DiskInfo { get; set; }
    public TemperatureInfo? TemperatureInfo { get; set; }
    public NetworkInfo? NetworkInfo { get; set; }
    public SystemInfo? SystemInfo { get; set; }
    public GpuInfo? GpuInfo { get; set; }
}

public class SystemInfo
{
    public string? OsVersion { get; set; }
    public string? ComputerName { get; set; }
    public string? UserName { get; set; }
    public TimeSpan Uptime { get; set; }
    public string? CpuArchitecture { get; set; }
    public double TotalRamGB { get; set; }
    public int CpuCores { get; set; }
}
