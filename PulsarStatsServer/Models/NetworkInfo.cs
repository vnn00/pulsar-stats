namespace SystemMonitorServer.Models;

public class NetworkInfo
{
    public double DownloadSpeedMBps { get; set; }
    public double UploadSpeedMBps { get; set; }
    public List<NetworkAdapter>? Adapters { get; set; }
}

public class NetworkAdapter
{
    public string? Name { get; set; }
    public string? Status { get; set; }
    public double BytesReceived { get; set; }
    public double BytesSent { get; set; }
}
