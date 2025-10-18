namespace SystemMonitorServer.Models;

public class GpuInfo
{
    public double UsagePercent { get; set; }
    public string? GpuName { get; set; }
    public double MemoryUsedMB { get; set; }
    public double MemoryTotalMB { get; set; }
    public double MemoryUsagePercent { get; set; }
}
