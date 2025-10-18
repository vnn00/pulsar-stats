namespace SystemMonitorServer.Models;

public class TemperatureInfo
{
    public double? CpuTemp { get; set; }
    public double? GpuTemp { get; set; }
    public double? MotherboardTemp { get; set; }
    public List<DiskTemperature>? DiskTemps { get; set; }
}

public class DiskTemperature
{
    public string? DiskName { get; set; }
    public double Temperature { get; set; }
}
