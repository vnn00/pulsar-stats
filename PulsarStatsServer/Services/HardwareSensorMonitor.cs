using LibreHardwareMonitor.Hardware;
using SystemMonitorServer.Models;

namespace SystemMonitorServer.Services;

public class HardwareSensorMonitor : IDisposable
{
    private readonly Computer _computer;
    private bool _isInitialized;

    public HardwareSensorMonitor()
    {
        _computer = new Computer
        {
            IsCpuEnabled = true,
            IsGpuEnabled = true,
            IsMemoryEnabled = true,
            IsMotherboardEnabled = true,
            IsStorageEnabled = true,
            IsNetworkEnabled = true
        };

        try
        {
            _computer.Open();
            _isInitialized = true;
            Console.WriteLine("LibreHardwareMonitor initialized successfully");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"LibreHardwareMonitor initialization error: {ex.Message}");
            Console.WriteLine("Running without admin rights? Some sensors may not be available.");
            _isInitialized = false;
        }
    }

    public async Task<TemperatureInfo> GetTemperatureInfoAsync()
    {
        return await Task.Run(() =>
        {
            var tempInfo = new TemperatureInfo
            {
                DiskTemps = new List<DiskTemperature>()
            };

            if (!_isInitialized)
            {
                return tempInfo;
            }

            try
            {
                foreach (var hardware in _computer.Hardware)
                {
                    hardware.Update();

                    switch (hardware.HardwareType)
                    {
                        case HardwareType.Cpu:
                            tempInfo.CpuTemp = GetAverageTemperature(hardware, SensorType.Temperature);
                            break;

                        case HardwareType.GpuNvidia:
                        case HardwareType.GpuAmd:
                        case HardwareType.GpuIntel:
                            if (tempInfo.GpuTemp == null)
                            {
                                tempInfo.GpuTemp = GetAverageTemperature(hardware, SensorType.Temperature);
                            }
                            break;

                        case HardwareType.Motherboard:
                            if (tempInfo.MotherboardTemp == null)
                            {
                                tempInfo.MotherboardTemp = GetAverageTemperature(hardware, SensorType.Temperature);
                            }
                            break;

                        case HardwareType.Storage:
                            var diskTemp = GetAverageTemperature(hardware, SensorType.Temperature);
                            if (diskTemp.HasValue)
                            {
                                tempInfo.DiskTemps.Add(new DiskTemperature
                                {
                                    DiskName = hardware.Name,
                                    Temperature = diskTemp.Value
                                });
                            }
                            break;
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Temperature monitoring error: {ex.Message}");
            }

            return tempInfo;
        });
    }

    public async Task<GpuInfo?> GetGpuInfoAsync()
    {
        return await Task.Run(() =>
        {
            if (!_isInitialized)
            {
                return null;
            }

            try
            {
                foreach (var hardware in _computer.Hardware)
                {
                    hardware.Update();

                    if (hardware.HardwareType == HardwareType.GpuNvidia ||
                        hardware.HardwareType == HardwareType.GpuAmd ||
                        hardware.HardwareType == HardwareType.GpuIntel)
                    {
                        var gpuInfo = new GpuInfo
                        {
                            GpuName = hardware.Name
                        };

                        // DEBUG: Log all GPU sensors to find correct memory sensor
                        Console.WriteLine($"🔍 GPU: {hardware.Name} ({hardware.HardwareType})");
                        foreach (var sensor in hardware.Sensors)
                        {
                            Console.WriteLine($"  Sensor: {sensor.SensorType} | {sensor.Name} | {sensor.Value}");
                        }

                        foreach (var sensor in hardware.Sensors)
                        {
                            if (sensor.Value.HasValue)
                            {
                                switch (sensor.SensorType)
                                {
                                    case SensorType.Load when sensor.Name.Contains("GPU Core", StringComparison.OrdinalIgnoreCase) ||
                                                               sensor.Name.Contains("D3D", StringComparison.OrdinalIgnoreCase):
                                        gpuInfo.UsagePercent = Math.Round((double)sensor.Value.Value, 1);
                                        break;

                                    // DEDICATED memory only - look for "Dedicated" keyword
                                    case SensorType.SmallData when sensor.Name.Contains("Dedicated", StringComparison.OrdinalIgnoreCase) && 
                                                                   sensor.Name.Contains("Used", StringComparison.OrdinalIgnoreCase):
                                        gpuInfo.MemoryUsedMB = Math.Round((double)sensor.Value.Value, 1);
                                        Console.WriteLine($"✅ GPU Memory Used (Dedicated): {gpuInfo.MemoryUsedMB} MB from sensor '{sensor.Name}'");
                                        break;

                                    case SensorType.SmallData when sensor.Name.Contains("Dedicated", StringComparison.OrdinalIgnoreCase) && 
                                                                   sensor.Name.Contains("Total", StringComparison.OrdinalIgnoreCase):
                                        gpuInfo.MemoryTotalMB = Math.Round((double)sensor.Value.Value, 1);
                                        Console.WriteLine($"✅ GPU Memory Total (Dedicated): {gpuInfo.MemoryTotalMB} MB from sensor '{sensor.Name}'");
                                        break;

                                    // Fallback: If no "Dedicated" sensor, use generic "Memory" but log warning
                                    case SensorType.SmallData when !sensor.Name.Contains("Shared", StringComparison.OrdinalIgnoreCase) &&
                                                                   sensor.Name.Contains("Memory Used", StringComparison.OrdinalIgnoreCase) && 
                                                                   gpuInfo.MemoryUsedMB == 0:
                                        gpuInfo.MemoryUsedMB = Math.Round((double)sensor.Value.Value, 1);
                                        Console.WriteLine($"⚠️ GPU Memory Used (Fallback): {gpuInfo.MemoryUsedMB} MB from sensor '{sensor.Name}' - May include shared memory!");
                                        break;

                                    case SensorType.SmallData when !sensor.Name.Contains("Shared", StringComparison.OrdinalIgnoreCase) &&
                                                                   sensor.Name.Contains("Memory Total", StringComparison.OrdinalIgnoreCase) && 
                                                                   gpuInfo.MemoryTotalMB == 0:
                                        gpuInfo.MemoryTotalMB = Math.Round((double)sensor.Value.Value, 1);
                                        Console.WriteLine($"⚠️ GPU Memory Total (Fallback): {gpuInfo.MemoryTotalMB} MB from sensor '{sensor.Name}' - May include shared memory!");
                                        break;
                                }
                            }
                        }

                        // Calculate memory usage percent
                        if (gpuInfo.MemoryTotalMB > 0)
                        {
                            gpuInfo.MemoryUsagePercent = Math.Round((gpuInfo.MemoryUsedMB / gpuInfo.MemoryTotalMB) * 100, 1);
                        }

                        // Return first GPU found
                        if (gpuInfo.UsagePercent > 0 || !string.IsNullOrEmpty(gpuInfo.GpuName))
                        {
                            return gpuInfo;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"GPU monitoring error: {ex.Message}");
            }

            return null;
        });
    }

    private double? GetAverageTemperature(IHardware hardware, SensorType sensorType)
    {
        var temps = new List<double>();

        foreach (var sensor in hardware.Sensors)
        {
            if (sensor.SensorType == sensorType && sensor.Value.HasValue)
            {
                temps.Add((double)sensor.Value.Value);
            }
        }

        // Alt donanımları da kontrol et
        foreach (var subHardware in hardware.SubHardware)
        {
            subHardware.Update();
            foreach (var sensor in subHardware.Sensors)
            {
                if (sensor.SensorType == sensorType && sensor.Value.HasValue)
                {
                    temps.Add((double)sensor.Value.Value);
                }
            }
        }

        return temps.Count > 0 ? Math.Round(temps.Average(), 1) : null;
    }

    public void Dispose()
    {
        try
        {
            _computer?.Close();
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Dispose error: {ex.Message}");
        }
    }
}
