# Android Simple Browser

A simplified Android browser application that launches directly to api.oraclestar.cn with WebView implementation. The app features enhanced support for CAPTCHA verification and passkey authentication, running in landscape mode.

## Features

### WebView Implementation
- Built-in Chromium-based WebView
- Desktop mode enabled by default
- Enhanced CAPTCHA support
- Basic passkey support
- Forced landscape orientation
- Fixed URL: api.oraclestar.cn

### Desktop Mode (WebView)
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
2. The app automatically opens api.oraclestar.cn in landscape mode
3. Navigate and interact with the website using the WebView
4. All WebView features are available (downloads, long-press menus, etc.)

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

## 本地构建与 CI 配置（快速指南）

如果你在本地或 CI 上构建时遇到“SDK location not found”之类的错误，请按下面步骤配置 Android SDK 路径：

1) 在仓库根目录创建或编辑 `local.properties`，写入你的 SDK 路径（示例）：

```properties
# local.properties 示例（将路径替换为你本地 Android SDK 位置）
sdk.dir=/home/youruser/Android/Sdk
```

2) 或者在构建前在 shell 中设置环境变量（只对当前会话有效）：

```bash
export ANDROID_HOME=/home/youruser/Android/Sdk
export PATH="$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools"
./gradlew assembleDebug
```

3) 在 CI（GitHub Actions）中，你需要确保 runner 安装了 Android SDK。下面是一个最小示例工作流片段（放在 `.github/workflows/android.yml`）：

```yaml
name: Android CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
      - name: Set up Android SDK
        uses: android-actions/setup-android@v2
        with:
          api-level: 34
          components: build-tools;34.0.0,platforms;android-34
      - name: Build
        run: ./gradlew assembleDebug --no-daemon
```

FIDO2 / WebAuthn（Passkeys）额外说明

- 如果你要把 Google Play Services 的 FIDO2 集成到 `GeckoViewActivity` 中：
  - 在 `app/build.gradle` 的 `dependencies` 中添加（示例，替换为合适版本）：

```groovy
// 示例（替换 VERSION）
implementation 'com.google.android.gms:play-services-fido:VERSION'
```

- 然后在 Activity 中使用 `Fido.getFido2ApiClient(this)` 获取注册/签名 Intent，并使用 `ActivityResultLauncher` 启动，最终将 attestation/assertion bytes 回填给页面或 Gecko。
- 注意：完整实现需要 Android SDK 与相应的 Google Play Services 库在 CI 上可用；在本地请先配置 `local.properties` 或 `ANDROID_HOME`。

如果你需要，我可以把完整的 FIDO2 集成示例代码（含 Gradle 依赖修改）提交到仓库的单独分支，由 CI 在你的环境中完成编译验证。

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
