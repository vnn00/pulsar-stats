using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using SystemMonitorServer.Hubs;
using SystemMonitorServer.Services;
using SystemMonitorServer;
using System.Runtime.InteropServices;

var builder = WebApplication.CreateBuilder(args);

var updateIntervalSeconds = builder.Configuration.GetValue<int>("Monitoring:UpdateIntervalSeconds", 1);

// Check if running on Windows
bool isWindows = RuntimeInformation.IsOSPlatform(OSPlatform.Windows);

if (isWindows)
{
    // Windows Forms initialization for system tray
    Application.EnableVisualStyles();
    Application.SetCompatibleTextRenderingDefault(false);
    
    // Disable console logging for cleaner tray experience
    builder.Logging.ClearProviders();
    builder.Logging.AddDebug();
}
else
{
    // Linux: Keep console logging enabled
    builder.Logging.AddConsole();
    builder.Logging.AddDebug();
}

// Configure Kestrel to listen on all network interfaces
builder.WebHost.ConfigureKestrel(options =>
{
    options.ListenAnyIP(5000); // 0.0.0.0:5000 - listen on all interfaces
});

// Add services to the container
builder.Services.AddSignalR(options =>
    {
        options.EnableDetailedErrors = true;
        options.MaximumReceiveMessageSize = 20 * 1024 * 1024; // Allow large payloads (screenshots)
    })
    .AddJsonProtocol(options =>
    {
        options.PayloadSerializerOptions.PropertyNamingPolicy = System.Text.Json.JsonNamingPolicy.CamelCase;
    });

builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy =>
    {
        policy.SetIsOriginAllowed(_ => true)
              .AllowAnyHeader()
              .AllowAnyMethod()
              .AllowCredentials();
    });
});

// Register MonitoringService as a hosted service
builder.Services.AddHostedService<MonitoringService>();

// Add controllers
builder.Services.AddControllers();

var app = builder.Build();

// Configure the HTTP request pipeline
app.UseCors();
app.UseRouting();

// Map SignalR Hub
app.MapHub<SystemMonitorHub>("/systemhub");

// Map API controllers
app.MapControllers();

// Health check endpoints
app.MapGet("/", () => new
{
    Service = "Pulsar Stats Server",
    Status = "Running",
    Version = "3.10.3",
    Platform = isWindows ? "Windows" : "Linux",
    SignalREndpoint = "/systemhub",
    UpdateInterval = $"{updateIntervalSeconds} second{(updateIntervalSeconds == 1 ? string.Empty : "s")}"
});

app.MapGet("/health", () => Results.Ok(new { status = "healthy", platform = isWindows ? "windows" : "linux" }));

// Platform-specific startup
if (isWindows)
{
    // Start web host in background
    var webHostTask = Task.Run(() => app.Run());

    // Create logger for TrayIconManager
    var loggerFactory = app.Services.GetRequiredService<ILoggerFactory>();
    var trayLogger = loggerFactory.CreateLogger<TrayIconManager>();

    // Initialize and run tray icon
    using (var trayIcon = new TrayIconManager(app, trayLogger))
    {
        // Run Windows Forms message loop
        Application.Run();
    }

    // Cleanup
    await app.StopAsync();
}
else
{
    // Linux: Run server directly with console output
    Console.WriteLine("╔════════════════════════════════════════════════════════╗");
    Console.WriteLine("║        Pulsar Stats Server - Linux Edition            ║");
    Console.WriteLine("╚════════════════════════════════════════════════════════╝");
    Console.WriteLine($"Server URL: http://0.0.0.0:5000");
    Console.WriteLine($"SignalR Hub: /systemhub");
    Console.WriteLine($"Update Interval: {updateIntervalSeconds} seconds");
    Console.WriteLine($"Platform: {RuntimeInformation.OSDescription}");
    Console.WriteLine("Press Ctrl+C to stop the server");
    Console.WriteLine();
    
    app.Run();
}
