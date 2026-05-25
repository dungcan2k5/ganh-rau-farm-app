# Tài liệu Xác thực & Tương tác Dữ liệu (Retrofit Edition)

Tài liệu này tập trung vào chi tiết kỹ thuật của hệ thống Xác thực (Auth) và truy vấn dữ liệu thông qua Supabase REST API.

## 1. Cấu hình Retrofit
Hệ thống sử dụng một Client tập trung tại `RetrofitClient.java` để quản lý các kết nối:
- **Base URL**: Lấy từ `BuildConfig.SUPABASE_URL`.
- **Interceptors**: 
    - Luôn thêm header `apikey`.
    - Tự động thêm header `Authorization: Bearer [token]` nếu người dùng đã đăng nhập.

## 2. Quy trình Xác thực (Google Login)

1.  **Google Sign-In**: Sử dụng `GoogleSignInClient` để người dùng chọn tài khoản và lấy `idToken`.
2.  **Token Exchange**:
    - **API**: `POST auth/v1/token?grant_type=id_token`
    - **Request Model**: `AuthRequest` (chứa `provider="google"` và `id_token`).
    - **Response Model**: `AuthResponse` (chứa `access_token` và `UserInfo`).
3.  **Lưu phiên**: `access_token` và `user_id` được lưu vào `SharedPreferences` để sử dụng cho các lần sau.

## 3. Truy vấn Dữ liệu (PostgREST)

Việc lấy dữ liệu từ Database được thực hiện qua các interface Retrofit:

- **Endpoint**: `GET rest/v1/users`
- **Tham số**:
    - `id=eq.[user_id]`: Lọc theo ID người dùng.
    - `select=*`: Lấy tất cả các cột.
- **Model**: `UserProfile.java` (Gson POJO).

## 4. Cấu hình Môi trường (Secrets)

Dữ liệu bí mật được lưu trong `local.properties` và truy cập qua `BuildConfig`:
- `SUPABASE_URL`: Địa chỉ API của dự án.
- `SUPABASE_ANON_KEY`: Mã API công khai.
- `GOOGLE_WEB_CLIENT_ID`: ID dùng cho Google Sign-In.

## 5. Danh sách Model (POJOs)
- `AuthRequest`: Body cho yêu cầu đăng nhập.
- `AuthResponse`: Chứa token và thông tin user cơ bản từ Auth service.
- `UserProfile`: Ánh xạ dữ liệu từ bảng `users` trong Database.
