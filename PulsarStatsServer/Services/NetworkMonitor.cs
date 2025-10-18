using System.Diagnostics;
using System.Net.NetworkInformation;
using SystemMonitorServer.Models;

namespace SystemMonitorServer.Services;

public class NetworkMonitor
{
    private long _lastBytesReceived;
    private long _lastBytesSent;
    private DateTime _lastCheck;

    public NetworkMonitor()
    {
        InitializeBaseline();
    }

    private void InitializeBaseline()
    {
        try
        {
            var interfaces = NetworkInterface.GetAllNetworkInterfaces()
                .Where(ni => ni.OperationalStatus == OperationalStatus.Up &&
                            ni.NetworkInterfaceType != NetworkInterfaceType.Loopback);

            foreach (var ni in interfaces)
            {
                var stats = ni.GetIPv4Statistics();
                _lastBytesReceived += stats.BytesReceived;
                _lastBytesSent += stats.BytesSent;
            }

            _lastCheck = DateTime.Now;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Network baseline error: {ex.Message}");
        }
    }

    public async Task<NetworkInfo> GetNetworkInfoAsync()
    {
        return await Task.Run(() =>
        {
            var networkInfo = new NetworkInfo
            {
                Adapters = new List<NetworkAdapter>()
            };

            try
            {
                var now = DateTime.Now;
                var timeDiff = (now - _lastCheck).TotalSeconds;

                long currentBytesReceived = 0;
                long currentBytesSent = 0;

                var interfaces = NetworkInterface.GetAllNetworkInterfaces()
                    .Where(ni => ni.OperationalStatus == OperationalStatus.Up &&
                                ni.NetworkInterfaceType != NetworkInterfaceType.Loopback);

                foreach (var ni in interfaces)
                {
                    var stats = ni.GetIPv4Statistics();
                    currentBytesReceived += stats.BytesReceived;
                    currentBytesSent += stats.BytesSent;

                    networkInfo.Adapters.Add(new NetworkAdapter
                    {
                        Name = ni.Name,
                        Status = ni.OperationalStatus.ToString(),
                        BytesReceived = stats.BytesReceived,
                        BytesSent = stats.BytesSent
                    });
                }

                // Hız hesaplama (MB/s)
                if (timeDiff > 0)
                {
                    var downloadBytes = currentBytesReceived - _lastBytesReceived;
                    var uploadBytes = currentBytesSent - _lastBytesSent;

                    networkInfo.DownloadSpeedMBps = Math.Round((downloadBytes / timeDiff) / (1024.0 * 1024.0), 2);
                    networkInfo.UploadSpeedMBps = Math.Round((uploadBytes / timeDiff) / (1024.0 * 1024.0), 2);
                }

                _lastBytesReceived = currentBytesReceived;
                _lastBytesSent = currentBytesSent;
                _lastCheck = now;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Network monitoring error: {ex.Message}");
            }

            return networkInfo;
        });
    }
}
