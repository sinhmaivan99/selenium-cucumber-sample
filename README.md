# 🔐 CRM Login Automation Test

Automation test cho chức năng **Đăng nhập** của hệ thống CRM ([crm.anhtester.com](https://crm.anhtester.com/admin/authentication)).

---

## 📋 Mục lục

- [Yêu cầu hệ thống](#-yêu-cầu-hệ-thống)
- [Cài đặt & Chạy test](#-cài-đặt--chạy-test)
- [Cấu trúc project](#-cấu-trúc-project)
- [Giải thích từng file](#-giải-thích-từng-file)
- [Danh sách Test Cases](#-danh-sách-test-cases)
- [Allure Report](#-allure-report)
- [Design Patterns](#-design-patterns)
- [Cách thêm test mới](#-cách-thêm-test-mới)
- [FAQ](#-faq)

---

## 🛠 Yêu cầu hệ thống

| Công cụ | Phiên bản tối thiểu | Kiểm tra |
|---------|---------------------|----------|
| **Java JDK** | 17+ | `java -version` |
| **Maven** | 3.8+ | `mvn -version` |
| **Google Chrome** | Bản mới nhất | Tự động, không cần cài driver |

> **Lưu ý:** Project dùng Selenium Manager (từ Selenium 4.6+) nên **không cần** tải ChromeDriver thủ công. Selenium sẽ tự tải driver phù hợp với phiên bản Chrome của bạn.

---

## 🚀 Cài đặt & Chạy test

### 1. Clone project

```bash
git clone <repository-url>
cd crm-login-automation
```

### 2. Chạy toàn bộ test

```bash
mvn clean test
```

### 3. Chạy 1 test case cụ thể

```bash
mvn test -Dtest=LoginTest#testLoginSuccess
```

### 4. Xem Allure Report (sau khi chạy test)

```bash
mvn allure:serve
```

Lệnh này sẽ tự mở trình duyệt hiển thị report. Nhấn `Ctrl+C` trong terminal để tắt.

---

## 📁 Cấu trúc project

```text
crm-login-automation/
├── pom.xml                                  ← Cấu hình Maven, dependencies
├── README.md                                ← File bạn đang đọc
├── .gitignore                               ← File ignore
│
└── src/test/
    ├── java/
    │   ├── base/
    │   │   ├── BasePage.java                ← Common functions cho mọi Page Object
    │   │   ├── BaseTest.java                ← Móc TestNG lifecycle với Listener
    │   │   └── DriverFactory.java           ← Multi-browser parallel support (Thread-Safe)
    │   ├── pages/
    │   │   └── LoginPage.java               ← Page Object trang Login (extends BasePage)
    │   ├── tests/
    │   │   └── LoginTest.java               ← Test cases chạy bằng DDT (extends BaseTest)
    │   └── utils/
    │       ├── ConfigManager.java           ← Đọc properties
    │       ├── DataHelper.java              ← Đọc test data từ JSON nested
    │       ├── LogHelper.java               ← Ghi log quá trình test
    │       ├── ScreenRecorderHelper.java    ← Screenshot + quay video thread-safe
    │       ├── TestListener.java            ← Lắng nghe sự kiện test để log/chụp ảnh
    │       ├── RetryListener.java           ← Cài đặt tự động retry
    │       └── RetryAnalyzer.java           ← Cơ chế retry test khi flaky
    │
    └── resources/
        ├── config.properties                ← Cấu hình browser, timeout, mode
        ├── testdata.json                    ← Dữ liệu test cấu trúc JSON nested
        ├── testng.xml                       ← Cấu hình TestNG suite
        └── allure.properties                ← Cấu hình Allure Report
```

---

## 📖 Giải thích Kiến trúc cốt lõi

### 1. Quản lý WebDriver Thread-Safe (`DriverFactory.java`)
Khác với các framework cơ bản, dự án này không lưu WebDriver ở class level. Toàn bộ `driver` và `wait` được cấp phát thông qua `ThreadLocal`. Bạn hoàn toàn có thể chạy test **song song (parallel)** trên nhiều luồng mà trình duyệt không bao giờ bị dẫm đạp lên nhau.

### 2. Thiết lập Cấu hình (`config.properties`)
Mọi thay đổi liên quan đến môi trường đều thực hiện tại đây mà **không cần sửa code Java**:
```properties
browser=chrome         # Tùy chọn: chrome, firefox, edge
headless=false         # Tùy chọn: true (chạy ngầm), false (hiện UI)
implicit.wait=10       # Tính bằng giây
explicit.wait=15
screenshot.on.fail=true
video.on.fail=true
```

### 3. Data-Driven Testing (`DataHelper` & `@DataProvider`)
Dữ liệu test được lưu gọn gàng dưới dạng JSON lồng nhau trong `testdata.json` và gọi thông qua `DataHelper.get("login.validEmail")`.
Đặc biệt, 6 bài test lỗi (invalid credentials) được gom chung thành 1 bài test sử dụng `@DataProvider`, giúp code siêu ngắn gọn.

### 4. Tách biệt báo cáo với `TestListener`
`BaseTest` chỉ làm đúng 1 việc là cấp/thu hồi WebDriver. Việc chụp ảnh màn hình và quay video khi một test thất bại được class `TestListener` tự động lắng nghe và âm thầm thực thi. Không còn code rác trong test lifecycle.

### 5. Chống Flaky Test (`RetryAnalyzer`)
Thi thoảng server lag khiến 1 test fail oan uổng. Hệ thống tự động áp dụng `RetryAnalyzer` qua `RetryListener` trong `testng.xml`, cho phép test tự động chạy lại 1 lần trước khi báo cáo kết quả Failed thực sự.

### 6. BasePage & Page Object Model
- `BasePage.java`: Chứa 30+ hàm thao tác DOM đã được bọc lại bằng Explicit Wait cẩn thận (e.g. `waitForPageLoad`, `setText`, `click`).
- `LoginPage.java`: Chứa Locator và Business logic. Tuân thủ **Encapsulation** tuyệt đối, test class chỉ được phép gọi `loginPage.open()` để tự trang đó biết cách điều hướng.

---

## 🧪 Danh sách Test Cases

| # | Tên test | Loại | Mức độ |
|---|----------|------|--------|
| TC01 | Đăng nhập thành công | ✅ Positive | Blocker |
| TC02 | Title Dashboard sau login | ✅ Positive | Normal |
| TC03-TC08 | Đăng nhập thất bại (DDT với DataProvider) | ❌ Negative | Critical / Normal |
| TC09 | Password ẩn ký tự | 🎨 UI | Minor |
| TC10 | Nút Login hiển thị | 🎨 UI | Minor |
| TC11 | Title trang Login | 🎨 UI | Minor |
| TC12 | SQL Injection | 🔒 Security | Blocker |
| TC13 | XSS Attack | 🔒 Security | Blocker |

*(Chú ý: Mặc dù code có vẻ ít hơn nhờ gộp các bài test thất bại, nhưng Allure report vẫn đếm đủ số lượng bài test chạy trên từng mảng dữ liệu).*

---

## 📊 Allure Report

Sau khi chạy `mvn clean test`, kết quả lưu tại `target/allure-results/`.

```bash
# Mở report trên trình duyệt
mvn allure:serve
```

**Report bao gồm:**
- **Timeline** — Thời gian chạy từng test
- **Retries** — Các test bị fail nhưng pass sau khi tự động chạy lại
- **Screenshots** — Ảnh chụp tự động khi test fail
- **Videos** — Video quay toàn bộ quá trình test fail (tự xóa nếu test pass)

---

## ❓ FAQ

### Q: Muốn chạy test song song (Parallel) để tăng tốc?
Dự án đã được thiết kế 100% **Thread-Safe**. Mở file `src/test/resources/testng.xml` và xem thẻ `<suite>`:
```xml
<suite name="CRM Login Test Suite" verbose="1" parallel="methods" thread-count="3">
```
Cấu hình này sẽ mở cùng lúc 3 trình duyệt để chạy các bài test. Rất tuyệt vời để tăng tốc độ CI/CD!

### Q: Muốn chạy trên Firefox thay vì Chrome?
Rất đơn giản, hãy mở `src/test/resources/config.properties` và sửa lại:
```properties
browser=firefox
```

### Q: Muốn chạy headless (không hiện trình duyệt) trên CI/CD?
Mở `src/test/resources/config.properties` và sửa lại:
```properties
headless=true
```

### Q: Video recording bị lỗi trên Linux/CI server không có GUI?
Monte Screen Recorder cần môi trường có desktop (GUI). Nếu bạn chạy trên Server Linux không có giao diện, hãy mở `config.properties` và tắt nó đi:
```properties
video.on.fail=false
```

### Q: Screenshot và video lưu ở đâu?

| Loại | Đường dẫn | Allure |
|------|-----------|--------|
| Screenshot | `target/screenshots/` | ✅ Tự động attach |
| Video | `target/videos/` | ✅ Tự động attach |

---

## 📦 Tech Stack

| Thư viện | Phiên bản | Vai trò |
|----------|-----------|---------|
| Java | 17 | Ngôn ngữ lập trình |
| Maven | 3.8+ | Build tool |
| Selenium | 4.27.0 | Điều khiển trình duyệt (Selenium Manager built-in) |
| TestNG | 7.10.2 | Test framework |
| Allure | 2.29.0 | Test report cao cấp |
| Gson | 2.11.0 | Đọc file JSON |
| Monte Screen Recorder | 0.7.7.0 | Quay video màn hình test |
