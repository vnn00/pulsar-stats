using System.Diagnostics;
using System.Management;
using SystemMonitorServer.Models;

namespace SystemMonitorServer.Services;

public class CpuMonitor
{
    private PerformanceCounter? _cpuCounter;
    private PerformanceCounter[]? _coreCounters;
    private readonly int _coreCount;
    private string? _processorName;

    public CpuMonitor()
    {
        _coreCount = Environment.ProcessorCount;
        InitializeCounters();
        GetProcessorName();
    }

    private void InitializeCounters()
    {
        try
        {
            _cpuCounter = new PerformanceCounter("Processor", "% Processor Time", "_Total");
            _cpuCounter.NextValue(); // İlk okuma atılır

            // Her çekirdek için counter
            _coreCounters = new PerformanceCounter[_coreCount];
            for (int i = 0; i < _coreCount; i++)
            {
                _coreCounters[i] = new PerformanceCounter("Processor", "% Processor Time", i.ToString());
                _coreCounters[i].NextValue();
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"CPU Counter initialization error: {ex.Message}");
        }
    }

    private void GetProcessorName()
    {
        try
        {
            using var searcher = new ManagementObjectSearcher("SELECT Name FROM Win32_Processor");
            foreach (var obj in searcher.Get())
            {
                _processorName = obj["Name"]?.ToString()?.Trim();
                break;
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Processor name error: {ex.Message}");
            _processorName = "Unknown";
        }
    }

    public async Task<CpuInfo> GetCpuInfoAsync()
    {
        return await Task.Run(() =>
        {
            var cpuInfo = new CpuInfo
            {
                CoreCount = _coreCount,
                ProcessorName = _processorName
            };

            try
            {
                // Toplam CPU kullanımı
                if (_cpuCounter != null)
                {
                    cpuInfo.UsagePercent = Math.Round(_cpuCounter.NextValue(), 2);
                }

                // Çekirdek bazında kullanım
                if (_coreCounters != null)
                {
                    cpuInfo.CoreUsages = new List<double>();
                    foreach (var counter in _coreCounters)
                    {
                        cpuInfo.CoreUsages.Add(Math.Round(counter.NextValue(), 2));
                    }
                }

                // CPU Frekansı (WMI)
                try
                {
                    using var searcher = new ManagementObjectSearcher("SELECT CurrentClockSpeed FROM Win32_Processor");
                    foreach (var obj in searcher.Get())
                    {
                        cpuInfo.FrequencyMHz = Convert.ToDouble(obj["CurrentClockSpeed"]);
                        break;
                    }
                }
                catch { }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"CPU monitoring error: {ex.Message}");
            }

            return cpuInfo;
        });
    }

    public void Dispose()
    {
        _cpuCounter?.Dispose();
        if (_coreCounters != null)
        {
            foreach (var counter in _coreCounters)
            {
                counter?.Dispose();
            }
        }
    }
}
