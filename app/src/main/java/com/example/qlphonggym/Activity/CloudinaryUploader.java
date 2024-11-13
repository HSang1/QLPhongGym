package com.example.qlphonggym.Activity;

import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CloudinaryUploader {

    private static final String CLOUD_NAME = "dp8bipggt";  // Thay thế bằng Cloud Name của bạn
    private static final String API_KEY = "283881843642369";  // Thay thế bằng API Key của bạn từ Cloudinary
    private static final String API_SECRET = "SgGX_Ne1v1SSOTk_aNy0K3O4tos";  // Thay thế bằng API Secret của bạn từ Cloudinary
    private static final String UPLOAD_PRESET = "qlphonggym";  // Upload Preset mà bạn đã tạo

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(String errorMessage);
    }

    // Phương thức upload hình ảnh
    public void uploadImage(File file, UploadCallback callback) {
        // Nếu xảy ra lỗi từ Cloudinary API, log chi tiết lỗi trả về từ API
        try {
            // Cấu hình Cloudinary với Cloud Name, API Key, API Secret và Upload Preset
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", CLOUD_NAME,
                    "api_key", API_KEY,
                    "api_secret", API_SECRET,
                    "upload_preset", UPLOAD_PRESET
            ));

            Log.d("CloudinaryUploader", "Uploading file: " + file.getAbsolutePath());
            if (!file.exists()) {
                callback.onFailure("Tệp ảnh không tồn tại.");
                return;
            }

            // Đảm bảo rằng file là ảnh có định dạng hợp lệ
            String fileExtension = getFileExtension(file);
            Log.d("CloudinaryUploader", "File extension: " + fileExtension);
            if (!fileExtension.equalsIgnoreCase("jpg") && !fileExtension.equalsIgnoreCase("png")) {
                callback.onFailure("Tệp không hợp lệ. Vui lòng tải lên ảnh với định dạng .jpg hoặc .png.");
                return;
            }

            // Upload ảnh lên Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());

            if (uploadResult != null) {
                // Lấy URL của ảnh đã upload
                String imageUrl = (String) uploadResult.get("url");
                Log.d("CloudinaryUploader", "Image uploaded successfully. URL: " + imageUrl);
                callback.onSuccess(imageUrl);
            } else {
                callback.onFailure("Lỗi khi tải ảnh lên Cloudinary: Không có kết quả trả về");
            }
        } catch (IOException e) {
            // Xử lý các lỗi không xác định
            Log.e("CloudinaryUploader", "Upload failed", e);
            callback.onFailure("Lỗi khi tải ảnh lên Cloudinary: " + e.getMessage());
        }
    }

    // Phương thức lấy phần mở rộng của tệp
    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
    }
}
