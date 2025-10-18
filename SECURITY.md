# Security Policy

## Reporting a Vulnerability

If you discover a security vulnerability in Pulsar Stats, please report it by creating a GitHub issue with the "security" label. 

Please include:
- Description of the vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (if any)

## Security Best Practices

### For Users

1. **Network Security**
   - Run the server only on trusted local networks
   - Do not expose the server port (5066) to the internet
   - Use firewall rules to restrict access if needed

2. **Android App**
   - Only connect to trusted server IP addresses
   - Verify the server certificate if using HTTPS
   - Keep the app updated to the latest version

### For Developers

1. **Keystore Management**
   - Never commit `.jks` or `.keystore` files to version control
   - Store signing keys securely and separately
   - Use environment variables or secure vaults for sensitive data

2. **Dependencies**
   - Regularly update dependencies to patch security vulnerabilities
   - Review dependency security advisories
   - Use tools like `dotnet list package --vulnerable` and Gradle dependency checks

3. **Code Security**
   - Do not hardcode passwords, API keys, or tokens
   - Validate and sanitize all user inputs
   - Use secure communication protocols (HTTPS/WSS) when possible

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 3.10.x  | :white_check_mark: |
| < 3.10  | :x:                |

## Known Security Considerations

1. **Local Network Only**: This application is designed for local network use only. Do not expose it to the public internet without proper security measures (authentication, HTTPS, etc.)

2. **Administrator Privileges**: The Windows server requires administrator privileges for temperature monitoring. This is necessary to access hardware sensors.

3. **Screenshot Feature**: The screenshot feature captures the entire screen, which may include sensitive information. Use with caution.

## Future Security Enhancements

- [ ] Add authentication/authorization
- [ ] HTTPS/WSS support
- [ ] API rate limiting
- [ ] Encrypted communication
- [ ] User access control
