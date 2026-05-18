const fs = require("fs");
const path = require("path");
const { execSync } = require("child_process");

function loadPptxGenJs() {
  try {
    return require("pptxgenjs");
  } catch (error) {
    const globalRoot = execSync("npm root -g", { encoding: "utf8" }).trim();
    return require(path.join(globalRoot, "pptxgenjs"));
  }
}

const pptxgen = loadPptxGenJs();

const pptx = new pptxgen();
pptx.layout = "LAYOUT_16x9";
pptx.author = "SOLO AI Assistant";
pptx.company = "SOLO";
pptx.subject = "Smart Cloud Disk 项目汇报";
pptx.title = "Smart Cloud Disk 项目汇报";
pptx.lang = "zh-CN";
pptx.theme = {
  headFontFace: "Microsoft YaHei",
  bodyFontFace: "Microsoft YaHei",
  lang: "zh-CN",
};

const C = {
  navy: "102A43",
  deep: "0F172A",
  teal: "0F766E",
  mint: "14B8A6",
  sky: "E6FFFB",
  cyan: "CFFAFE",
  sand: "F8FAFC",
  white: "FFFFFF",
  slate: "475569",
  muted: "64748B",
  border: "D8E3EC",
  gold: "F59E0B",
  rose: "E11D48",
  green: "15803D",
};

const ROOT = __dirname;
const outputDir = path.join(ROOT, "deliverables");
const outputFile = path.join(outputDir, "Smart-Cloud-Disk_项目汇报.pptx");
const appIcon = path.join(
  ROOT,
  "app",
  "src",
  "main",
  "my_ic-playstore.png"
);

if (!fs.existsSync(outputDir)) {
  fs.mkdirSync(outputDir, { recursive: true });
}

function shadow(opacity = 0.14) {
  return {
    type: "outer",
    color: "000000",
    blur: 4,
    offset: 2,
    angle: 45,
    opacity,
  };
}

function addFooter(slide, page, text = "Smart Cloud Disk | Android 云盘项目汇报") {
  slide.addText(text, {
    x: 0.45,
    y: 5.18,
    w: 7.6,
    h: 0.2,
    fontSize: 9,
    color: C.muted,
    margin: 0,
  });
  slide.addText(String(page).padStart(2, "0"), {
    x: 9.05,
    y: 5.1,
    w: 0.45,
    h: 0.25,
    fontSize: 10,
    color: C.teal,
    bold: true,
    align: "right",
    margin: 0,
  });
}

function addSectionHeader(slide, eyebrow, title, subtitle) {
  slide.addText(eyebrow, {
    x: 0.55,
    y: 0.42,
    w: 2.4,
    h: 0.22,
    fontSize: 11,
    color: C.teal,
    bold: true,
    charSpacing: 1.6,
    margin: 0,
  });
  slide.addText(title, {
    x: 0.55,
    y: 0.68,
    w: 5.8,
    h: 0.48,
    fontSize: 25,
    color: C.deep,
    bold: true,
    margin: 0,
  });
  if (subtitle) {
    slide.addText(subtitle, {
      x: 0.55,
      y: 1.1,
      w: 8.8,
      h: 0.34,
      fontSize: 11,
      color: C.muted,
      margin: 0,
    });
  }
}

function addCard(slide, x, y, w, h, title, body, accent = C.teal) {
  slide.addShape(pptx.ShapeType.rect, {
    x,
    y,
    w,
    h,
    fill: { color: C.white },
    line: { color: C.border, width: 1 },
    shadow: shadow(),
  });
  slide.addShape(pptx.ShapeType.rect, {
    x,
    y,
    w: 0.08,
    h,
    fill: { color: accent },
    line: { color: accent, width: 0 },
  });
  slide.addText(title, {
    x: x + 0.22,
    y: y + 0.18,
    w: w - 0.34,
    h: 0.28,
    fontSize: 14,
    color: C.deep,
    bold: true,
    margin: 0,
  });
  slide.addText(body, {
    x: x + 0.22,
    y: y + 0.52,
    w: w - 0.34,
    h: h - 0.62,
    fontSize: 10.5,
    color: C.slate,
    valign: "top",
    breakLine: false,
    margin: 0,
    fit: "shrink",
  });
}

function addKpi(slide, x, y, w, h, value, label, tone = C.teal) {
  slide.addShape(pptx.ShapeType.rect, {
    x,
    y,
    w,
    h,
    fill: { color: C.white },
    line: { color: C.border, width: 1 },
    shadow: shadow(0.12),
  });
  slide.addText(value, {
    x: x + 0.16,
    y: y + 0.12,
    w: w - 0.28,
    h: 0.4,
    fontSize: 24,
    bold: true,
    color: tone,
    margin: 0,
    align: "center",
  });
  slide.addText(label, {
    x: x + 0.16,
    y: y + 0.56,
    w: w - 0.28,
    h: 0.24,
    fontSize: 10,
    color: C.muted,
    margin: 0,
    align: "center",
  });
}

function addPill(slide, x, y, w, text, fillColor, textColor = C.deep) {
  slide.addShape(pptx.ShapeType.roundRect, {
    x,
    y,
    w,
    h: 0.34,
    rectRadius: 0.06,
    fill: { color: fillColor },
    line: { color: fillColor, width: 0 },
  });
  slide.addText(text, {
    x,
    y: y + 0.07,
    w,
    h: 0.18,
    fontSize: 9.5,
    bold: true,
    color: textColor,
    align: "center",
    margin: 0,
  });
}

function addNumberStep(slide, x, y, title, body, idx) {
  slide.addShape(pptx.ShapeType.ellipse, {
    x,
    y,
    w: 0.36,
    h: 0.36,
    fill: { color: C.teal },
    line: { color: C.teal, width: 0 },
  });
  slide.addText(String(idx), {
    x,
    y: y + 0.07,
    w: 0.36,
    h: 0.16,
    fontSize: 10,
    color: C.white,
    bold: true,
    align: "center",
    margin: 0,
  });
  slide.addText(title, {
    x: x + 0.5,
    y: y - 0.01,
    w: 2.2,
    h: 0.2,
    fontSize: 12.5,
    color: C.deep,
    bold: true,
    margin: 0,
  });
  slide.addText(body, {
    x: x + 0.5,
    y: y + 0.2,
    w: 2.2,
    h: 0.5,
    fontSize: 9.5,
    color: C.slate,
    margin: 0,
    fit: "shrink",
  });
}

function addBulletList(slide, items, opts) {
  const runs = [];
  items.forEach((item, idx) => {
    runs.push({
      text: item,
      options: { bullet: true, breakLine: idx < items.length - 1 },
    });
  });
  slide.addText(runs, {
    x: opts.x,
    y: opts.y,
    w: opts.w,
    h: opts.h,
    fontSize: opts.fontSize || 11.5,
    color: opts.color || C.slate,
    breakLine: false,
    margin: 0,
    paraSpaceAfterPt: 10,
    valign: "top",
    fit: "shrink",
  });
}

function addCover() {
  const slide = pptx.addSlide();
  slide.background = { color: C.navy };

  slide.addShape(pptx.ShapeType.rect, {
    x: 5.95,
    y: 0,
    w: 4.05,
    h: 5.63,
    fill: { color: "0B2037" },
    line: { color: "0B2037", width: 0 },
  });
  slide.addShape(pptx.ShapeType.rect, {
    x: 0.65,
    y: 4.6,
    w: 1.1,
    h: 0.08,
    fill: { color: C.mint },
    line: { color: C.mint, width: 0 },
  });

  slide.addText("Smart Cloud Disk", {
    x: 0.7,
    y: 0.82,
    w: 4.9,
    h: 0.34,
    fontSize: 14,
    color: "A5F3FC",
    bold: true,
    charSpacing: 2,
    margin: 0,
  });
  slide.addText("项目汇报", {
    x: 0.68,
    y: 1.25,
    w: 4.2,
    h: 0.8,
    fontSize: 28,
    color: C.white,
    bold: true,
    margin: 0,
  });
  slide.addText("面向私有云盘场景的 Android 移动端文件管理应用", {
    x: 0.72,
    y: 2.12,
    w: 4.7,
    h: 0.44,
    fontSize: 13,
    color: "DDEAF7",
    margin: 0,
    fit: "shrink",
  });
  slide.addText(
    "聚焦用户空间隔离、本地秒级搜索、多媒体预览、上传下载管理与 2FA 安全增强",
    {
      x: 0.72,
      y: 2.7,
      w: 4.7,
      h: 0.56,
      fontSize: 11.5,
      color: "B8C7D9",
      margin: 0,
      fit: "shrink",
    }
  );

  addPill(slide, 0.72, 3.55, 1.15, "Android", "DFF7F5", C.teal);
  addPill(slide, 1.98, 3.55, 1.15, "WebDAV", "E0F2FE", "075985");
  addPill(slide, 3.24, 3.55, 0.92, "Room", "FEF3C7", "92400E");
  addPill(slide, 4.28, 3.55, 0.8, "AI", "FCE7F3", "9D174D");

  slide.addShape(pptx.ShapeType.rect, {
    x: 6.45,
    y: 0.78,
    w: 2.75,
    h: 2.75,
    fill: { color: "173857" },
    line: { color: "173857", width: 0 },
    shadow: shadow(0.2),
  });
  if (fs.existsSync(appIcon)) {
    slide.addImage({
      path: appIcon,
      x: 6.93,
      y: 1.22,
      w: 1.8,
      h: 1.8,
    });
  }

  addCard(
    slide,
    6.35,
    3.78,
    3.0,
    0.92,
    "汇报重点",
    "产品定位、核心功能、技术架构、工程亮点与后续规划",
    C.mint
  );

  slide.addText(`生成时间  ${new Date().toLocaleDateString("zh-CN")}`, {
    x: 0.72,
    y: 4.93,
    w: 2.2,
    h: 0.18,
    fontSize: 9.5,
    color: "B8C7D9",
    margin: 0,
  });
}

function addOverview() {
  const slide = pptx.addSlide();
  slide.background = { color: C.sand };
  addSectionHeader(
    slide,
    "PROJECT SNAPSHOT",
    "项目概览",
    "以 Android 客户端为载体，连接私有 WebDAV 与后端服务，完成云端文件管理闭环。"
  );

  slide.addShape(pptx.ShapeType.rect, {
    x: 0.56,
    y: 1.6,
    w: 4.2,
    h: 2.72,
    fill: { color: C.white },
    line: { color: C.border, width: 1 },
    shadow: shadow(),
  });
  slide.addText("项目定位", {
    x: 0.8,
    y: 1.87,
    w: 1.2,
    h: 0.22,
    fontSize: 14,
    bold: true,
    color: C.deep,
    margin: 0,
  });
  slide.addText(
    "CloudDisk 是一款面向私有云盘场景的移动端应用。它将登录注册、文件浏览、上传下载、多媒体预览与本地索引整合到统一入口，兼顾可用性、性能与扩展性。",
    {
      x: 0.8,
      y: 2.18,
      w: 3.7,
      h: 0.78,
      fontSize: 11.5,
      color: C.slate,
      margin: 0,
      fit: "shrink",
    }
  );
  addBulletList(
    slide,
    [
      "用户登录后直接进入个人专属目录，完成数据隔离",
      "Room 本地索引支撑秒级搜索与容量统计",
      "支持图片、音频、视频等多媒体内容就地预览",
      "上传下载任务可视化，体验接近真实云盘产品",
    ],
    { x: 0.82, y: 3.12, w: 3.62, h: 0.95, fontSize: 10.5 }
  );

  addKpi(slide, 5.1, 1.72, 1.85, 1.05, "Android", "客户端平台", C.teal);
  addKpi(slide, 7.08, 1.72, 1.85, 1.05, "WebDAV", "远端存储协议", "075985");
  addKpi(slide, 5.1, 2.97, 1.85, 1.05, "Room", "本地索引与缓存", "7C3AED");
  addKpi(slide, 7.08, 2.97, 1.85, 1.05, "1TB", "单用户空间配额", C.gold);

  addFooter(slide, 2);
}

function addFeaturePanorama() {
  const slide = pptx.addSlide();
  slide.background = { color: C.white };
  addSectionHeader(
    slide,
    "FEATURE LANDSCAPE",
    "功能全景",
    "功能模块围绕“账号、文件、媒体、传输、搜索、安全”六条主线展开。"
  );

  addCard(slide, 0.62, 1.65, 2.8, 1.12, "用户系统", "开屏路由、登录注册、账号注销、2FA 管理", C.teal);
  addCard(slide, 3.6, 1.65, 2.8, 1.12, "文件管理", "目录递归浏览、批量删除/移动、新建文件夹、容量监控", "2563EB");
  addCard(slide, 6.58, 1.65, 2.8, 1.12, "多媒体中心", "图片缩放翻页、音频后台播放、视频横屏全屏预览", "7C3AED");

  addCard(slide, 0.62, 3.0, 2.8, 1.12, "传输任务", "上传进度、下载管理、上传前容量校验、任务列表追踪", C.gold);
  addCard(slide, 3.6, 3.0, 2.8, 1.12, "本地索引", "Room 保存文件索引，支撑关键词搜索与容量统计", C.green);
  addCard(slide, 6.58, 3.0, 2.8, 1.12, "AI 助手", "基于本地索引选择关联文件，对接后端智能问答接口", C.rose);

  slide.addText("核心能力分布（按 README 列出的能力项统计）", {
    x: 0.68,
    y: 4.48,
    w: 3.8,
    h: 0.2,
    fontSize: 10.5,
    color: C.muted,
    bold: true,
    margin: 0,
  });
  slide.addChart(
    pptx.ChartType.bar,
    [
      {
        name: "能力项",
        labels: ["用户系统", "文件管理", "多媒体", "传输管理"],
        values: [4, 4, 3, 3],
      },
    ],
    {
      x: 0.65,
      y: 4.68,
      w: 8.7,
      h: 0.62,
      barDir: "col",
      showLegend: false,
      showTitle: false,
      showValue: true,
      dataLabelPosition: "outEnd",
      chartColors: [C.teal],
      valAxisMinVal: 0,
      valAxisMaxVal: 5,
      valAxisMajorUnit: 1,
      catAxisLabelColor: C.muted,
      valAxisLabelColor: C.muted,
      valGridLine: { color: "E2E8F0", size: 0.5 },
      catGridLine: { color: "FFFFFF", size: 0.5 },
      chartArea: { fill: { color: C.white }, border: { color: C.white } },
      showCatName: true,
    }
  );

  addFooter(slide, 3);
}

function addFlow() {
  const slide = pptx.addSlide();
  slide.background = { color: C.sand };
  addSectionHeader(
    slide,
    "CORE FLOW",
    "核心业务流程",
    "从登录到文件消费形成闭环，用户既能管理文件，也能围绕文件进行搜索、播放与智能交互。"
  );

  slide.addShape(pptx.ShapeType.line, {
    x: 0.95,
    y: 2.52,
    w: 8.08,
    h: 0,
    line: { color: "9BD7D0", width: 2 },
  });

  addNumberStep(slide, 0.82, 2.33, "登录与验证", "支持登录、注册与二次验证，完成身份校验。", 1);
  addNumberStep(slide, 2.45, 1.86, "进入用户目录", "登录后自动定位到个人根目录，保证用户空间隔离。", 2);
  addNumberStep(slide, 4.08, 2.33, "静默索引", "递归扫描 WebDAV 目录，将元数据写入 Room。", 3);
  addNumberStep(slide, 5.7, 1.86, "搜索与预览", "就地完成关键词搜索、图片/音频/视频预览。", 4);
  addNumberStep(slide, 7.34, 2.33, "传输与协同", "上传下载任务可视化，并可与 AI 助手结合文件问答。", 5);

  addCard(
    slide,
    0.82,
    3.65,
    4.02,
    1.08,
    "流程价值",
    "将账号、文件、传输、媒体和检索打通为统一体验，减少页面切换与重复请求。",
    C.teal
  );
  addCard(
    slide,
    5.0,
    3.65,
    4.02,
    1.08,
    "设计取向",
    "本地索引负责速度，远端 WebDAV 负责一致性，系统下载器负责稳定性。",
    "2563EB"
  );

  addFooter(slide, 4);
}

function addArchitecture() {
  const slide = pptx.addSlide();
  slide.background = { color: C.white };
  addSectionHeader(
    slide,
    "TECH ARCHITECTURE",
    "技术架构",
    "架构以 Android 本地能力为核心，叠加网络访问、本地数据库与系统服务，保证高频场景稳定响应。"
  );

  const layers = [
    { y: 1.55, h: 0.72, color: "E0F2FE", title: "表现层", body: "Splash / Main / Register / FileList / Chat 等 Activity 与 RecyclerView 列表" },
    { y: 2.43, h: 0.72, color: "DFF7F5", title: "业务层", body: "登录注册、文件列表控制、播放器逻辑、2FA、上传下载任务调度" },
    { y: 3.31, h: 0.72, color: "ECFCCB", title: "数据层", body: "OkHttp 访问后端与 WebDAV，Room 存储用户与文件索引" },
    { y: 4.19, h: 0.72, color: "FEF3C7", title: "基础设施层", body: "DownloadManager、Media3 ExoPlayer、MediaPlayer、Glide、PhotoView" },
  ];

  layers.forEach((layer) => {
    slide.addShape(pptx.ShapeType.rect, {
      x: 0.72,
      y: layer.y,
      w: 6.0,
      h: layer.h,
      fill: { color: layer.color },
      line: { color: C.white, width: 0 },
    });
    slide.addText(layer.title, {
      x: 0.94,
      y: layer.y + 0.19,
      w: 1.2,
      h: 0.2,
      fontSize: 13.5,
      color: C.deep,
      bold: true,
      margin: 0,
    });
    slide.addText(layer.body, {
      x: 2.0,
      y: layer.y + 0.16,
      w: 4.42,
      h: 0.3,
      fontSize: 10.5,
      color: C.slate,
      margin: 0,
      fit: "shrink",
    });
  });

  addCard(slide, 7.05, 1.66, 2.3, 0.76, "网络能力", "OkHttp 负责 WebDAV 与后端 API 访问", C.teal);
  addCard(slide, 7.05, 2.58, 2.3, 0.76, "数据能力", "Room 维护用户表与文件索引表", "2563EB");
  addCard(slide, 7.05, 3.5, 2.3, 0.76, "媒体能力", "Media3 + MediaPlayer + Glide + PhotoView", "7C3AED");
  addCard(slide, 7.05, 4.42, 2.3, 0.42, "工程配置", "Gradle 构建 + BuildConfig 注入凭据", C.gold);

  addFooter(slide, 5);
}

function addHighlights() {
  const slide = pptx.addSlide();
  slide.background = { color: C.sand };
  addSectionHeader(
    slide,
    "PRODUCT HIGHLIGHTS",
    "核心亮点",
    "亮点集中体现在用户隔离、性能体验、传输稳定性与安全增强四个方面。"
  );

  addCard(slide, 0.62, 1.63, 4.1, 0.98, "用户空间隔离", "以用户名作为根目录视图边界，回退导航自动防越界，保证不同用户数据互不干扰。", C.teal);
  addCard(slide, 0.62, 2.8, 4.1, 0.98, "本地秒级搜索", "WebDAV 元数据写入 Room，本地检索绕开高频网络往返，同时复用索引做容量统计。", "2563EB");
  addCard(slide, 0.62, 3.97, 4.1, 0.98, "稳定传输体验", "下载接入系统 DownloadManager，上传前校验空间余量，任务列表可实时反馈进度。", C.gold);

  slide.addShape(pptx.ShapeType.rect, {
    x: 5.05,
    y: 1.62,
    w: 4.3,
    h: 3.36,
    fill: { color: C.white },
    line: { color: C.border, width: 1 },
    shadow: shadow(),
  });
  slide.addText("安全与体验增强", {
    x: 5.32,
    y: 1.9,
    w: 1.8,
    h: 0.24,
    fontSize: 14,
    bold: true,
    color: C.deep,
    margin: 0,
  });
  addBulletList(
    slide,
    [
      "登录、改密、注销流程均可接入 2FA 验证",
      "图片支持缩放和滑动相册，视频支持横屏全屏",
      "音频支持后台播放、播放列表与多种播放模式",
      "内置 AI 助手入口，支持按文件上下文扩展智能问答",
    ],
    { x: 5.33, y: 2.28, w: 3.5, h: 1.35, fontSize: 10.6 }
  );

  slide.addText("亮点总结", {
    x: 5.32,
    y: 3.88,
    w: 1.1,
    h: 0.2,
    fontSize: 11.5,
    bold: true,
    color: C.teal,
    margin: 0,
  });
  slide.addText("产品能力完整度高，既满足课程/学习项目展示，也具备继续演进为实用型云盘客户端的基础。", {
    x: 5.32,
    y: 4.16,
    w: 3.48,
    h: 0.52,
    fontSize: 10.4,
    color: C.slate,
    margin: 0,
    fit: "shrink",
  });

  addFooter(slide, 6);
}

function addRoadmap() {
  const slide = pptx.addSlide();
  slide.background = { color: C.white };
  addSectionHeader(
    slide,
    "DELIVERY & ROADMAP",
    "工程现状与后续规划",
    "当前版本已经具备可运行、可演示、可扩展的基础，下一步重点在配置治理、体验细化与平台能力升级。"
  );

  addCard(slide, 0.68, 1.65, 2.82, 1.18, "当前完成度", "基础账号体系、文件管理、多媒体播放、任务管理、本地索引、在线更新均已具备。", C.teal);
  addCard(slide, 0.68, 3.02, 2.82, 1.18, "工程可交付性", "Gradle 构建明确，APK 命名规则清晰，支持通过配置注入账号与 Token。", "2563EB");

  addCard(slide, 3.72, 1.65, 2.58, 1.18, "优化方向 1", "将 Config 与 URL 常量进一步统一到 BuildConfig，减少环境切换成本。", C.gold);
  addCard(slide, 3.72, 3.02, 2.58, 1.18, "优化方向 2", "面向 Android 10+ 逐步升级存储访问方案，降低旧权限依赖。", C.green);

  addCard(slide, 6.52, 1.65, 2.58, 1.18, "优化方向 3", "把 AI 接口、错误处理与限额提示配置化，提升上线可维护性。", C.rose);
  addCard(slide, 6.52, 3.02, 2.58, 1.18, "优化方向 4", "针对大目录场景引入索引队列化与批处理，进一步优化性能。", "7C3AED");

  slide.addText("建议汇报时的收束话术", {
    x: 0.72,
    y: 4.58,
    w: 1.8,
    h: 0.2,
    fontSize: 11.5,
    bold: true,
    color: C.deep,
    margin: 0,
  });
  slide.addText(
    "本项目已完成从账号到文件消费的核心闭环，既展示了 Android 客户端开发能力，也体现了对真实云盘产品体验和系统设计的理解。",
    {
      x: 0.72,
      y: 4.82,
      w: 8.52,
      h: 0.26,
      fontSize: 10.6,
      color: C.slate,
      margin: 0,
      fit: "shrink",
    }
  );

  addFooter(slide, 7);
}

function addClosing() {
  const slide = pptx.addSlide();
  slide.background = { color: C.deep };

  slide.addText("结论", {
    x: 0.7,
    y: 0.78,
    w: 1.1,
    h: 0.36,
    fontSize: 18,
    color: "A5F3FC",
    bold: true,
    margin: 0,
  });
  slide.addText("Smart Cloud Disk 已具备完整汇报价值", {
    x: 0.7,
    y: 1.3,
    w: 5.8,
    h: 0.58,
    fontSize: 26,
    color: C.white,
    bold: true,
    margin: 0,
  });
  slide.addText("它不是单一功能 Demo，而是具备产品逻辑、工程组织和扩展能力的移动端云盘应用。", {
    x: 0.72,
    y: 2.0,
    w: 5.7,
    h: 0.42,
    fontSize: 12.5,
    color: "DDEAF7",
    margin: 0,
    fit: "shrink",
  });

  addCard(slide, 0.72, 2.85, 2.7, 1.08, "产品层面", "覆盖账号、文件、媒体、传输、搜索、安全等关键场景。", C.mint);
  addCard(slide, 3.58, 2.85, 2.7, 1.08, "技术层面", "Android 原生能力与网络、数据库、播放器等组件协同清晰。", "2563EB");
  addCard(slide, 6.44, 2.85, 2.7, 1.08, "演进层面", "具备继续接入更多存储能力、智能能力和工程规范的空间。", C.gold);

  slide.addText("谢谢观看", {
    x: 0.72,
    y: 4.78,
    w: 1.6,
    h: 0.24,
    fontSize: 16,
    color: C.white,
    bold: true,
    margin: 0,
  });
  slide.addText("可直接用于课程答辩、项目汇报或阶段成果展示", {
    x: 0.72,
    y: 5.04,
    w: 3.9,
    h: 0.18,
    fontSize: 10.5,
    color: "B8C7D9",
    margin: 0,
  });
}

addCover();
addOverview();
addFeaturePanorama();
addFlow();
addArchitecture();
addHighlights();
addRoadmap();
addClosing();

pptx.writeFile({ fileName: outputFile }).then(() => {
  console.log(`PPT generated: ${outputFile}`);
});
