# Tài liệu Kiến trúc Tổng quan GanhRauFarm

Tài liệu này mô tả cấu trúc tổng quát, luồng hoạt động và các màn hình chính của ứng dụng GanhRauFarm.

## 1. Tổng quan Công nghệ
- **Ngôn ngữ**: 100% Java 11.
- **Networking**: Retrofit2 + OkHttp3.
- **JSON Parsing**: Gson.
- **UI**: XML Layouts + ViewBinding.

## 2. Luồng hoạt động chính (Application Flow)

Ứng dụng sử dụng mô hình điều hướng dựa trên trạng thái phiên đăng nhập (Session):

1.  **Màn hình Khởi động (`MainActivity`)**:
    - Là điểm vào của ứng dụng (Launcher Activity).
    - Kiểm tra `access_token` trong `SharedPreferences` (`ganh_rau_prefs`).
    - **Điều hướng**:
        - Nếu Token tồn tại: Mở `HomeActivity`.
        - Nếu không: Mở `LoginActivity`.
    - Sau khi điều hướng, `MainActivity` sẽ tự kết thúc (`finish()`).

2.  **Màn hình Đăng nhập (`LoginActivity`)**:
    - Cung cấp tính năng đăng nhập bằng Google.
    - Xử lý việc lấy ID Token từ Google và đổi lấy Access Token từ Supabase.
    - Sau khi đăng nhập thành công, lưu Token và chuyển sang `HomeActivity`.

3.  **Màn hình Trang chủ (`HomeActivity`)**:
    - Hiển thị thông tin hồ sơ người dùng (Họ tên, Email, Địa chỉ...).
    - Thực hiện truy vấn dữ liệu từ bảng `users` trên Supabase.
    - Cung cấp chức năng Đăng xuất (Logout).

## 3. Các màn hình chính (Screens)

| Màn hình | Activity Class | Chức năng chính |
| :--- | :--- | :--- |
| **Splash/Router** | `MainActivity` | Kiểm tra session, điều hướng người dùng. |
| **Login** | `LoginActivity` | Đăng nhập Google, xác thực với Supabase Auth. |
| **Home** | `HomeActivity` | Hiển thị thông tin cá nhân, quản lý tài khoản. |

---

## 4. Quản lý Trạng thái (State Management)
- **SharedPreferences**: Tên file là `"ganh_rau_prefs"`.
- **Dữ liệu lưu trữ**:
    - `access_token`: Token dùng cho các request API.
    - `user_id`: UUID của người dùng trong hệ thống Supabase.
