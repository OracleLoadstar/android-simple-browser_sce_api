# APK 签名配置指南

本文档说明如何为项目配置 APK 签名，以便自动构建已签名的 Release APK。

## 概述

项目使用固定的签名配置对 Release APK 进行签名。签名密钥通过 GitHub Secrets 安全管理，不会暴露在代码仓库中。

## 一、生成签名密钥库

### 1.1 使用 keytool 生成密钥库

在命令行中运行以下命令：

```bash
keytool -genkey -v -keystore release.keystore -alias release \
  -keyalg RSA -keysize 2048 -validity 10000
```

**参数说明：**
- `release.keystore`: 密钥库文件名
- `release`: 密钥别名
- `RSA`: 密钥算法
- `2048`: 密钥长度
- `10000`: 有效期（天数，约27年）

### 1.2 填写密钥信息

命令会提示你输入以下信息：

1. **密钥库密码**（keystore password）：请使用强密码
2. **密钥密码**（key password）：可以与密钥库密码相同
3. **姓名、组织等信息**：根据实际情况填写

**重要提示：** 
- 请妥善保管密钥库文件和密码
- 如果丢失密钥库，将无法更新已发布的应用
- 建议将密钥库文件和密码存储在安全的地方（如密码管理器）

## 二、配置 GitHub Secrets

### 2.1 将密钥库编码为 base64

**Linux/macOS:**

```bash
base64 release.keystore | tr -d '\n' > keystore.base64.txt
```

**Windows PowerShell:**

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore")) | Out-File keystore.base64.txt
```

### 2.2 在 GitHub 中添加 Secrets

1. 打开仓库页面
2. 点击 **Settings** > **Secrets and variables** > **Actions**
3. 点击 **New repository secret** 添加以下 secrets：

| Secret 名称 | 值 | 说明 |
|------------|---|------|
| `KEYSTORE_BASE64` | `keystore.base64.txt` 的内容 | 密钥库文件的 base64 编码 |
| `KEYSTORE_PASSWORD` | 你设置的密钥库密码 | 密钥库密码 |
| `KEY_ALIAS` | `release` | 密钥别名（与生成时使用的一致） |
| `KEY_PASSWORD` | 你设置的密钥密码 | 密钥密码 |

### 2.3 验证配置

配置完成后，推送一个新的 tag 来触发自动构建：

```bash
git tag v1.0.1
git push origin v1.0.1
```

检查 GitHub Actions 工作流是否成功运行，并生成已签名的 Release APK。

## 三、本地构建已签名版本

如果需要在本地构建已签名的 Release APK：

### 3.1 创建 keystore.properties 文件

在项目根目录创建 `keystore.properties` 文件（参考 `keystore.properties.template`）：

```properties
storeFile=release.keystore
storePassword=你的密钥库密码
keyAlias=release
keyPassword=你的密钥密码
```

### 3.2 将密钥库文件放到项目根目录

将 `release.keystore` 文件复制到项目根目录。

### 3.3 构建 Release APK

```bash
./gradlew assembleRelease
```

构建完成后，已签名的 APK 位于：
```
app/build/outputs/apk/release/app-release.apk
```

**注意：** `keystore.properties` 和 `*.keystore` 文件已添加到 `.gitignore`，不会被提交到仓库。

## 四、安全建议

1. **永远不要将密钥库文件或密码提交到代码仓库**
2. **定期备份密钥库文件**（存储在安全的位置）
3. **使用强密码**（至少16位，包含大小写字母、数字和特殊字符）
4. **限制对 GitHub Secrets 的访问权限**
5. **如果密钥库泄露，立即生成新的密钥库并重新发布应用**

## 五、故障排查

### 5.1 签名失败

如果构建时出现签名错误，检查：

1. GitHub Secrets 是否正确配置
2. `KEYSTORE_BASE64` 是否包含完整的 base64 编码（没有换行符）
3. 密码和别名是否正确
4. 密钥库文件是否有效

### 5.2 验证 APK 签名

使用以下命令验证 APK 是否已正确签名：

```bash
# 查看签名信息
keytool -printcert -jarfile app-release.apk

# 或使用 apksigner
apksigner verify --verbose app-release.apk
```

### 5.3 本地构建时找不到密钥库

确保：
1. `keystore.properties` 文件存在且路径正确
2. `release.keystore` 文件在 `storeFile` 指定的位置
3. 文件权限允许读取

## 六、更新签名配置

如果需要更换签名密钥：

1. 生成新的密钥库文件
2. 更新 GitHub Secrets 中的所有相关值
3. 注意：更换签名后，用户需要卸载旧版本才能安装新版本

**建议：** 除非绝对必要，否则不要更换签名密钥。
