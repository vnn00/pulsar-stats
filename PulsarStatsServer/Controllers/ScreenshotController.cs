using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;
using SystemMonitorServer.Hubs;
using System.Drawing;
using System.Drawing.Imaging;

namespace SystemMonitorServer.Controllers;

[ApiController]
[Route("api/[controller]")]
public class ScreenshotController : ControllerBase
{
    private readonly IHubContext<SystemMonitorHub> _hubContext;
    private readonly ILogger<ScreenshotController> _logger;

    public ScreenshotController(
        IHubContext<SystemMonitorHub> hubContext,
        ILogger<ScreenshotController> logger)
    {
        _hubContext = hubContext;
        _logger = logger;
    }

    /// <summary>
    /// Captures current screen and sends via SignalR to all connected clients
    /// </summary>
    [HttpPost("capture")]
    public async Task<IActionResult> CaptureScreen()
    {
        try
        {
            _logger.LogInformation("Screenshot capture requested");

            // Get primary screen bounds
            var bounds = Screen.PrimaryScreen.Bounds;
            
            // Create bitmap with screen size
            using var bitmap = new Bitmap(bounds.Width, bounds.Height, PixelFormat.Format32bppArgb);
            using var graphics = Graphics.FromImage(bitmap);
            
            // Capture screen
            graphics.CopyFromScreen(
                bounds.X, 
                bounds.Y, 
                0, 
                0, 
                bounds.Size, 
                CopyPixelOperation.SourceCopy);

            // Convert to base64 (JPEG for smaller size)
            using var memoryStream = new MemoryStream();
            
            // Save as JPEG with quality 85 for good balance
            var jpegEncoder = ImageCodecInfo.GetImageEncoders()
                .First(codec => codec.FormatID == ImageFormat.Jpeg.Guid);
            
            var encoderParameters = new EncoderParameters(1);
            encoderParameters.Param[0] = new EncoderParameter(
                System.Drawing.Imaging.Encoder.Quality, 
                85L);
            
            bitmap.Save(memoryStream, jpegEncoder, encoderParameters);
            
            var base64Image = Convert.ToBase64String(memoryStream.ToArray());

            // Create data object
            var screenshotData = new
            {
                timestamp = DateTime.Now,
                width = bounds.Width,
                height = bounds.Height,
                format = "jpeg",
                data = base64Image
            };

            // Send to all connected clients via SignalR
            await _hubContext.Clients.All.SendAsync("ReceiveScreenshot", screenshotData);

            _logger.LogInformation(
                "Screenshot captured and sent: {Width}x{Height}, Size: {Size} KB",
                bounds.Width,
                bounds.Height,
                memoryStream.Length / 1024);

            return Ok(new
            {
                success = true,
                message = "Screenshot captured and sent to clients",
                width = bounds.Width,
                height = bounds.Height,
                sizeKB = memoryStream.Length / 1024
            });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Failed to capture screenshot");
            return StatusCode(500, new
            {
                success = false,
                message = "Failed to capture screenshot",
                error = ex.Message
            });
        }
    }

    /// <summary>
    /// Get screenshot as direct image response (for testing)
    /// </summary>
    [HttpGet("capture")]
    public IActionResult CaptureScreenDirect()
    {
        try
        {
            var bounds = Screen.PrimaryScreen.Bounds;
            using var bitmap = new Bitmap(bounds.Width, bounds.Height, PixelFormat.Format32bppArgb);
            using var graphics = Graphics.FromImage(bitmap);
            
            graphics.CopyFromScreen(bounds.X, bounds.Y, 0, 0, bounds.Size, CopyPixelOperation.SourceCopy);

            using var memoryStream = new MemoryStream();
            bitmap.Save(memoryStream, ImageFormat.Jpeg);
            
            return File(memoryStream.ToArray(), "image/jpeg");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Failed to capture screenshot");
            return StatusCode(500, "Failed to capture screenshot");
        }
    }
}
