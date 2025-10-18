namespace SystemMonitorServer.Models;

public class DiskInfo
{
    public string? DriveName { get; set; }
    public string? VolumeLabel { get; set; }
    public double TotalGB { get; set; }
    public double UsedGB { get; set; }
    public double FreeGB { get; set; }
    public double UsagePercent { get; set; }
    public string? DriveType { get; set; }
}
