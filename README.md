# Android Simple Browser

A simple Android browser application with enhanced support for CAPTCHA verification and passkey authentication.

## Features

### Desktop Mode
- Spoofs Windows Chrome user agent
- Simulates 1920x1080 desktop resolution
- Hides mobile touch features

### Enhanced CAPTCHA Support
- Full cookie support including third-party cookies
- Application cache and database storage
- Mixed content mode for loading resources
- Automatic media playback for audio/video CAPTCHAs
- Geolocation support for location-based verification

### Passkey/WebAuthn Support
- WebAuthn protocol support
- Biometric authentication (fingerprint, face recognition)
- Safe browsing enabled for secure context
- Automatic permission granting for WebView

## Permissions

The app requests the following runtime permissions:
- **Camera**: For QR code scanning and biometric verification
- **Microphone**: For voice verification
- **Fine Location**: For location-based verification
- **Coarse Location**: For location-based verification

All permissions are optional - the app will continue to work even if some are denied, but certain features may not be available.

## Usage

1. Launch the app
2. Grant permissions when requested (recommended for best experience)
3. Enter a URL in the input field
4. Tap "生成快捷方式" (Create Shortcut) to create a home screen shortcut
5. The shortcut will open the website in desktop mode with full verification support

## Technical Details

### Enhanced WebView Settings
- JavaScript enabled
- DOM storage enabled
- Cookies (including third-party) enabled
- Application cache enabled
- Mixed content mode allowed
- Geolocation enabled
- Media autoplay enabled
- Safe browsing enabled

### Permission Handling
The app automatically grants WebView permission requests for:
- Geolocation
- Camera
- Microphone
- Other resources required for WebAuthn

## Building

```bash
./gradlew build
```

## Requirements
- Android SDK 24 (Android 7.0) or higher
- Target SDK: 34 (Android 14)

## Privacy

- Permissions are only used to support web page functionality
- No data collection or transmission to third-party servers
- All data is stored locally on the device

## Security Considerations

**Important Security Notes:**

To support CAPTCHA and passkey functionality, this app implements the following permission policies:

- **Automatic WebView Permission Granting**: The app automatically grants camera, microphone, and geolocation permissions requested by web pages
- **Mixed Content Mode**: Allows HTTPS pages to load HTTP resources, which may reduce security
- **Recommendations**:
  - Only visit trusted websites
  - For production use, consider implementing an origin whitelist mechanism
  - Regularly review app permission usage

## License

This project is open source.
