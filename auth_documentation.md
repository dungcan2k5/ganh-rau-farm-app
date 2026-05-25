# Tài liệu Kiến trúc Hệ thống GanhRauFarm (Pure Java & OkHttp3)

Tài liệu này mô tả chi tiết cách xây dựng lại hệ thống xác thực và truy vấn dữ liệu sử dụng hoàn toàn ngôn ngữ Java và thư viện OkHttp3, thay thế cho Supabase Kotlin SDK để đảm bảo tính ổn định và dễ bảo trì.

## 1. Luồng hoạt động chính (Application Flow)

Chương trình hoạt động theo mô hình Router-Activity:

1.  **Khởi động (`MainActivity`)**: 
    - Đóng vai trò là bộ điều hướng (Router).
    - Kiểm tra `access_token` trong `SharedPreferences`.
    - Nếu có token -> Chuyển đến `HomeActivity`.
    - Nếu không có -> Chuyển đến `LoginActivity`.
2.  **Đăng nhập (`LoginActivity`)**:
    - Người dùng nhấn nút "Đăng nhập Google".
    - Hệ thống gọi `signOut()` của Google Client để ép buộc hiển thị hộp thoại chọn tài khoản (nếu máy có nhiều account).
    - Sau khi chọn account, lấy được `id_token`.
    - Gửi `id_token` lên Supabase Auth API qua OkHttp3 để đổi lấy `access_token` và `user_id`.
    - Lưu thông tin vào `SharedPreferences`.
3.  **Trang chủ (`HomeActivity`)**:
    - Lấy `access_token` và `user_id` từ bộ nhớ.
    - Gửi request GET đến Supabase PostgREST API để lấy thông tin chi tiết người dùng từ bảng `users`.
    - Hiển thị dữ liệu lên giao diện.
    - Cung cấp chức năng "Đăng xuất" (Xóa `SharedPreferences` và quay về Login).

---

## 2. Chi tiết kỹ thuật các màn hình

### A. LoginActivity (Xác thực)
- **Công cụ**: `GoogleSignInClient`, `OkHttpClient`.
- **Bước 1 (Lấy ID Token)**:
    ```java
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(SupabaseConfig.GOOGLE_WEB_CLIENT_ID)
            .build();
    ```
- **Bước 2 (Đổi token với Supabase)**:
    - **Endpoint**: `POST https://[PROJECT_ID].supabase.co/auth/v1/token?grant_type=id_token`
    - **Body**: `{"provider": "google", "id_token": "..."}`
    - **Headers**: `apikey: [ANON_KEY]`

### B. HomeActivity (Dữ liệu)
- **Công cụ**: `OkHttpClient`, `org.json`.
- **Truy vấn thông tin người dùng**:
    - **Endpoint**: `GET https://[PROJECT_ID].supabase.co/rest/v1/users?select=*&id=eq.[USER_ID]`
    - **Headers**: 
        - `apikey: [ANON_KEY]`
        - `Authorization: Bearer [ACCESS_TOKEN]`
- **Xử lý giao diện**: Sử dụng `runOnUiThread` để cập nhật TextView từ callback của OkHttp.

---

## 3. Quản lý Phiên đăng nhập (Session Management)

Thông tin được lưu trữ trong `SharedPreferences` có tên `ganh_rau_prefs`:
- `access_token`: Dùng để xác thực các request API.
- `user_id`: Dùng để xác định người dùng hiện tại trong database.

---

## 4. Cấu hình Môi trường (local.properties)

Các tham số quan trọng được quản lý qua file `local.properties` tại thư mục gốc của project (file này không được commit lên Git):
- `SUPABASE_URL`: Link project Supabase.
- `SUPABASE_ANON_KEY`: Mã API công khai.
- `GOOGLE_WEB_CLIENT_ID`: ID Client từ Google Cloud Console.

Gradle sẽ tự động đọc các giá trị này và tạo ra các hằng số trong `BuildConfig` để code Java có thể sử dụng.

## 5. Danh sách thư viện (Dependencies)

```gradle
dependencies {
    // Networking
    implementation libs.okhttp
    
    // Google Auth
    implementation libs.play.services.auth
    
    // UI & Core
    implementation libs.appcompat
    implementation libs.material
    implementation libs.constraintlayout
}
```
