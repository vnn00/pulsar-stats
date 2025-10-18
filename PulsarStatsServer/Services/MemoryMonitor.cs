using System.Diagnostics;
using System.Management;
using SystemMonitorServer.Models;

namespace SystemMonitorServer.Services;

public class MemoryMonitor
{
    private PerformanceCounter? _availableMemoryCounter;
    private PerformanceCounter? _committedBytesCounter;

    public MemoryMonitor()
    {
        InitializeCounters();
    }

    private void InitializeCounters()
    {
        try
        {
            _availableMemoryCounter = new PerformanceCounter("Memory", "Available MBytes");
            _availableMemoryCounter.NextValue();
            
            _committedBytesCounter = new PerformanceCounter("Memory", "Committed Bytes");
            _committedBytesCounter.NextValue();
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Memory Counter initialization error: {ex.Message}");
        }
    }

    public async Task<MemoryInfo> GetMemoryInfoAsync()
    {
        return await Task.Run(() =>
        {
            var memoryInfo = new MemoryInfo();

            try
            {
                // Toplam fiziksel bellek (GB)
                var totalMemoryBytes = GC.GetGCMemoryInfo().TotalAvailableMemoryBytes;
                memoryInfo.TotalGB = Math.Round(totalMemoryBytes / (1024.0 * 1024.0 * 1024.0), 2);

                // Kullanılabilir bellek (GB)
                if (_availableMemoryCounter != null)
                {
                    var availableMB = _availableMemoryCounter.NextValue();
                    memoryInfo.AvailableGB = Math.Round(availableMB / 1024.0, 2);
                }

                // Kullanılan bellek
                memoryInfo.UsedGB = Math.Round(memoryInfo.TotalGB - memoryInfo.AvailableGB, 2);

                // Kullanım yüzdesi
                if (memoryInfo.TotalGB > 0)
                {
                    memoryInfo.UsagePercent = Math.Round((memoryInfo.UsedGB / memoryInfo.TotalGB) * 100, 2);
                }

                // Swap/Page File bilgisi
                try
                {
                    using (var searcher = new ManagementObjectSearcher("SELECT * FROM Win32_PageFileUsage"))
                    {
                        double totalSwapMB = 0;
                        double usedSwapMB = 0;

                        foreach (ManagementObject obj in searcher.Get())
                        {
                            var allocated = Convert.ToDouble(obj["AllocatedBaseSize"]);
                            var current = Convert.ToDouble(obj["CurrentUsage"]);
                            
                            totalSwapMB += allocated;
                            usedSwapMB += current;
                        }

                        if (totalSwapMB > 0)
                        {
                            memoryInfo.SwapTotalGB = Math.Round(totalSwapMB / 1024.0, 2);
                            memoryInfo.SwapUsedGB = Math.Round(usedSwapMB / 1024.0, 2);
                            memoryInfo.SwapUsagePercent = Math.Round((usedSwapMB / totalSwapMB) * 100, 2);
                        }
                    }
                }
                catch (Exception swapEx)
                {
                    Console.WriteLine($"Swap memory monitoring error: {swapEx.Message}");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Memory monitoring error: {ex.Message}");
            }

            return memoryInfo;
        });
    }

    public void Dispose()
    {
        _availableMemoryCounter?.Dispose();
        _committedBytesCounter?.Dispose();
    }
}
