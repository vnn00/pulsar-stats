using Microsoft.AspNetCore.SignalR;
using SystemMonitorServer.Models;

namespace SystemMonitorServer.Hubs;

public class SystemMonitorHub : Hub
{
    public override async Task OnConnectedAsync()
    {
        await Clients.Caller.SendAsync("Connected", "Sunucuya bağlandınız!");
        Console.WriteLine($"Client connected: {Context.ConnectionId}");
        await base.OnConnectedAsync();
    }

    public override async Task OnDisconnectedAsync(Exception? exception)
    {
        Console.WriteLine($"Client disconnected: {Context.ConnectionId}");
        await base.OnDisconnectedAsync(exception);
    }

    public async Task SendSystemUpdate(SystemData data)
    {
        await Clients.All.SendAsync("ReceiveSystemUpdate", data);
    }
}
