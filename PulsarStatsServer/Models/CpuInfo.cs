namespace SystemMonitorServer.Models;

public class CpuInfo
{
    public double UsagePercent { get; set; }
    public double FrequencyMHz { get; set; }
    public List<double>? CoreUsages { get; set; }
    public int CoreCount { get; set; }
    public string? ProcessorName { get; set; }
}
