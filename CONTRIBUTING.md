# Contributing to Pulsar Stats

First off, thank you for considering contributing to Pulsar Stats! It's people like you that make this project better.

## Code of Conduct

This project and everyone participating in it is governed by respect and professionalism. By participating, you are expected to uphold this standard.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues to avoid duplicates. When you create a bug report, include as many details as possible:

- **Use a clear and descriptive title**
- **Describe the exact steps to reproduce the problem**
- **Provide specific examples**
- **Describe the behavior you observed and what you expected**
- **Include screenshots if applicable**
- **Include your environment details:**
  - OS version (Windows 10/11, Linux distro)
  - .NET version
  - Android version (for mobile client)
  - Server/Client versions

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion:

- **Use a clear and descriptive title**
- **Provide a detailed description of the suggested enhancement**
- **Explain why this enhancement would be useful**
- **List some examples of how it would be used**

### Pull Requests

1. **Fork the repository**
2. **Create a new branch** from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes** following the coding standards below
4. **Test your changes** thoroughly
5. **Commit your changes** with clear, descriptive commit messages:
   ```bash
   git commit -m "Add: Feature description"
   ```
6. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```
7. **Submit a Pull Request** with a clear title and description

## Development Setup

### Server (Windows/Linux)

1. Install .NET 9.0 SDK
2. Clone the repository
3. Navigate to `SystemMonitorServer` directory
4. Run `dotnet restore`
5. Run `dotnet build`

### Android Client

1. Install Android Studio
2. Install Java 17 JDK
3. Navigate to `SystemMonitorMobile` directory
4. Open in Android Studio
5. Sync Gradle
6. Build the project

### Website

1. Navigate to `website` directory
2. Open `index.html` in a browser
3. No build process required (vanilla HTML/CSS/JS)

## Coding Standards

### C# (.NET Server)

- Follow Microsoft's C# coding conventions
- Use meaningful variable and method names
- Add XML documentation comments for public methods
- Keep methods focused and concise
- Use async/await for asynchronous operations
- Handle exceptions appropriately

Example:
```csharp
/// <summary>
/// Retrieves CPU usage percentage
/// </summary>
/// <returns>CPU usage as a float between 0 and 100</returns>
public async Task<float> GetCpuUsageAsync()
{
    try
    {
        // Implementation
    }
    catch (Exception ex)
    {
        _logger.LogError(ex, "Failed to retrieve CPU usage");
        throw;
    }
}
```

### Java (Android)

- Follow Android's Java style guide
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Keep Activities and Fragments focused
- Use ViewBinding instead of findViewById
- Handle Activity lifecycle properly

Example:
```java
/**
 * Connects to the monitoring server
 * @param serverUrl The server URL (e.g., "192.168.1.100:5066")
 */
private void connectToServer(String serverUrl) {
    try {
        // Implementation
    } catch (Exception e) {
        Log.e(TAG, "Connection failed", e);
        showError("Failed to connect to server");
    }
}
```

### JavaScript (Website)

- Use modern ES6+ syntax
- Use meaningful variable names (camelCase)
- Add comments for complex logic
- Keep functions pure when possible
- Avoid global variables

Example:
```javascript
/**
 * Animates fade-in effect for elements
 * @param {Element} element - The DOM element to animate
 */
function fadeIn(element) {
    element.classList.add('fade-in-visible');
}
```

## Commit Message Guidelines

Use clear, descriptive commit messages following this format:

- **Add:** When adding new features
  - `Add: Linux server support`
- **Fix:** When fixing bugs
  - `Fix: Connection timeout on slow networks`
- **Update:** When updating existing features
  - `Update: Improve CPU usage calculation`
- **Refactor:** When refactoring code
  - `Refactor: Simplify monitoring service logic`
- **Docs:** When updating documentation
  - `Docs: Add Linux installation guide`
- **Style:** When making style changes
  - `Style: Format code according to standards`

## Testing

- Test your changes on both Windows and Linux (for server changes)
- Test on multiple Android versions (if applicable)
- Ensure existing functionality is not broken
- Add unit tests for new features when possible

## Documentation

- Update README.md if you change functionality
- Add inline comments for complex logic
- Update API documentation if you add/modify endpoints
- Include code examples in documentation

## Questions?

Feel free to open an issue with the label "question" if you have any questions about contributing.

## License

By contributing to Pulsar Stats, you agree that your contributions will be licensed under the MIT License.

---

**Thank you for contributing to Pulsar Stats!** 🌟
