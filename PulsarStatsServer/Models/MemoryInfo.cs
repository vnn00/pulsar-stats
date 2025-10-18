namespace SystemMonitorServer.Models;

public class MemoryInfo
{
    public double TotalGB { get; set; }
    public double UsedGB { get; set; }
    public double AvailableGB { get; set; }
    public double UsagePercent { get; set; }
    public double SwapTotalGB { get; set; }
    public double SwapUsedGB { get; set; }
    public double SwapUsagePercent { get; set; }
}
