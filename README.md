# CloudDisk - 移动端云盘应用

CloudDisk 是一款基于 Android 平台开发的简易云存储客户端。它集成了用户管理、文件浏览、多媒体预览及传输管理等核心功能，支持连接私有 WebDAV 服务（如 CloudDrive2）及自定义后端。

## 🚀 核心功能

### 1. 用户系统
- **动态开屏**：集成 Android 12+ Splash Screen 接口。
- **登录注册**：支持通过后端 API 进行用户身份校验与注册。
- **专属空间**：每个用户登录后自动进入其专属的云端根目录，隔离用户数据。
- **账号注销**：一键清除本地缓存并同步删除云端个人文件夹。

### 2. 文件管理
- **文件浏览**：支持递归目录访问，文件夹优先排序。
- **本地索引**：使用 **Room 数据库** 对云端文件进行本地索引，实现秒级关键词搜索。
- **批量操作**：支持文件的批量删除、移动及新建文件夹。
- **容量监控**：实时计算并显示 1TB 专属空间的已用比例。

### 3. 多媒体中心
- **音频播放器**：
    - 支持后台播放。
    - 唱片旋转动画效果。
    - 顺序、单曲循环、循环播放三种模式。
    - 交互式进度条及播放列表管理。
- **视频预览**：集成 **ExoPlayer (Media3)**，支持横屏全屏播放及 WebDAV 身份认证。
- **图片查看器**：集成 **PhotoView**，支持手势缩放及左右滑动翻页。

### 4. 传输任务
- **上传管理**：支持断点续传式的上传进度显示及云空间余量预校验。
- **下载管理**：调用系统级 **DownloadManager**，确保任务在后台稳定完成。
- **分类清单**：独立的上传/下载任务列表，方便追踪进度。

## 🛠️ 技术栈

- **网络请求**：OkHttp 4.12.0
- **数据库**：Room Persistence Library 2.6.1
- **图片加载**：Glide 4.16.0
- **视频播放**：androidx.media3 (ExoPlayer) 1.2.1
- **手势交互**：PhotoView 2.3.0
- **UI 组件**：Material Components, ConstraintLayout, SwipeRefreshLayout
- **语言**：Java / Kotlin

## ⚙️ 配置指南

在运行项目前，请根据你的服务器环境修改 `app/src/main/java/com/example/clouddisk/Config.java`：

```java
public class Config {
    public static final String SERVER_IP = "你的服务器IP";
    public static final String CD2_PORT = "19798"; // WebDAV端口
    public static final String BACKEND_PORT = "3000"; // 后端API端口
    
    public static final String WEBDAV_USER = "admin";
    public static final String WEBDAV_PASS = "你的密码";
    
    public static final String CD2_TOKEN = "你的Token";
}
```

## 📂 项目结构

- `MainActivity`: 登录及开屏入口。
- `RegisterActivity`: 用户注册界面。
- `FileListActivity`: 应用核心控制器，处理文件列表、播放器逻辑及导航。
- `AppDatabase`: Room 数据库定义，管理 `User` 和 `FileIndex`。
- `TransferTask`: 传输任务模型。

---
*本项目仅供学习与交流使用。*
