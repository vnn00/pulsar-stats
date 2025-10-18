using SystemMonitorServer.Models;

namespace SystemMonitorServer.Services;

public class DiskMonitor
{
    public async Task<List<DiskInfo>> GetDiskInfoAsync()
    {
        return await Task.Run(() =>
        {
            var diskInfoList = new List<DiskInfo>();

            try
            {
                var drives = DriveInfo.GetDrives();

                foreach (var drive in drives)
                {
                    try
                    {
                        if (drive.IsReady)
                        {
                            var totalGB = drive.TotalSize / (1024.0 * 1024.0 * 1024.0);
                            var freeGB = drive.AvailableFreeSpace / (1024.0 * 1024.0 * 1024.0);
                            var usedGB = totalGB - freeGB;

                            var diskInfo = new DiskInfo
                            {
                                DriveName = drive.Name,
                                VolumeLabel = string.IsNullOrEmpty(drive.VolumeLabel) ? "Local Disk" : drive.VolumeLabel,
                                TotalGB = Math.Round(totalGB, 2),
                                FreeGB = Math.Round(freeGB, 2),
                                UsedGB = Math.Round(usedGB, 2),
                                UsagePercent = totalGB > 0 ? Math.Round((usedGB / totalGB) * 100, 2) : 0,
                                DriveType = drive.DriveType.ToString()
                            };

                            diskInfoList.Add(diskInfo);
                        }
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine($"Drive {drive.Name} monitoring error: {ex.Message}");
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Disk monitoring error: {ex.Message}");
            }

            return diskInfoList;
        });
    }
}
