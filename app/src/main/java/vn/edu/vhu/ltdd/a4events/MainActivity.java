package vn.edu.vhu.ltdd.a4events;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // MSSV: 231A290052 - Họ tên: Huỳnh Anh Tuấn
    private static final String TAG = "A4_231A290052";

    private TextInputLayout tilSoA, tilSoB, tilCanNang, tilChieuCao;
    private TextInputEditText edtSoA, edtSoB, edtCanNang, edtChieuCao;
    private TextView tvKetQua, tvBmi, tvPhanLoai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Xử lý Window Insets cho Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // 1. Ánh xạ View
        anhXaView();

        // 2. Thiết lập bộ lắng nghe sự kiện
        thietLapSuKien();
    }

    /**
     * Bước 1: Ánh xạ tất cả các View từ layout XML
     */
    private void anhXaView() {
        tilSoA = findViewById(R.id.tilSoA);
        tilSoB = findViewById(R.id.tilSoB);
        edtSoA = findViewById(R.id.edtSoA);
        edtSoB = findViewById(R.id.edtSoB);

        tilCanNang = findViewById(R.id.tilCanNang);
        tilChieuCao = findViewById(R.id.tilChieuCao);
        edtCanNang = findViewById(R.id.edtCanNang);
        edtChieuCao = findViewById(R.id.edtChieuCao);

        tvKetQua = findViewById(R.id.tvKetQua);
        tvBmi = findViewById(R.id.tvBmi);
        tvPhanLoai = findViewById(R.id.tvPhanLoai);
    }

    /**
     * Bước 2: Gán bộ lắng nghe sự kiện click cho các nút
     */
    private void thietLapSuKien() {
        Button btnCong = findViewById(R.id.btnCong);
        Button btnTru = findViewById(R.id.btnTru);
        Button btnNhan = findViewById(R.id.btnNhan);
        Button btnChia = findViewById(R.id.btnChia);
        Button btnXoa = findViewById(R.id.btnXoa);
        Button btnTinhBmi = findViewById(R.id.btnTinhBmi);

        // Nút Cộng và Trừ
        btnCong.setOnClickListener(v -> tinhToan('+'));
        btnTru.setOnClickListener(v -> tinhToan('-'));

        // Nút Nhân và Chia
        btnNhan.setOnClickListener(v -> tinhToan('*'));
        btnChia.setOnClickListener(v -> tinhToan('/'));

        // Nút Xóa trắng
        btnXoa.setOnClickListener(v -> xoaTrang());

        // Nút Tính BMI
        btnTinhBmi.setOnClickListener(v -> tinhBmi());
    }

    // ==========================================
    // MÁY TÍNH BỐN PHÉP TOÁN CƠ BẢN
    // ==========================================

    private void tinhToan(char phepToan) {
        String chuoiA = edtSoA.getText() != null ? edtSoA.getText().toString().trim() : "";
        String chuoiB = edtSoB.getText() != null ? edtSoB.getText().toString().trim() : "";

        // Lớp 1: Kiểm tra rỗng
        if (chuoiA.isEmpty()) {
            tilSoA.setError(getString(R.string.err_empty));
            edtSoA.requestFocus();
            return;
        }
        tilSoA.setError(null);

        if (chuoiB.isEmpty()) {
            tilSoB.setError(getString(R.string.err_empty));
            edtSoB.requestFocus();
            return;
        }
        tilSoB.setError(null);

        // Lớp 2: Kiểm tra định dạng số
        double a, b;
        try {
            a = Double.parseDouble(chuoiA);
            b = Double.parseDouble(chuoiB);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Dữ liệu nhập không phải số hợp lệ", e);
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
            return;
        }

        // Lớp 3: Kiểm tra chia cho 0
        if (phepToan == '/' && b == 0) {
            tilSoB.setError(getString(R.string.err_divide_zero));
            Toast.makeText(this, R.string.err_divide_zero, Toast.LENGTH_SHORT).show();
            edtSoB.requestFocus();
            return;
        }
        tilSoB.setError(null);

        // Lớp 4: Xử lý tính toán
        double ketQua = 0;
        switch (phepToan) {
            case '+': ketQua = a + b; break;
            case '-': ketQua = a - b; break;
            case '*': ketQua = a * b; break;
            case '/': ketQua = a / b; break;
            default: ketQua = 0; break;
        }

        char kyHieu = phepToan;
        if (phepToan == '/') kyHieu = '÷';
        else if (phepToan == '*') kyHieu = '×';
        else if (phepToan == '-') kyHieu = '−';

        String ketQuaDinhDang = String.format(Locale.getDefault(), "%.2f %c %.2f = %.2f", a, kyHieu, b, ketQua);
        tvKetQua.setText(ketQuaDinhDang);
        Log.d(TAG, "Thực hiện phép tính: " + ketQuaDinhDang);
    }

    private void xoaTrang() {
        edtSoA.setText("");
        edtSoB.setText("");
        tilSoA.setError(null);
        tilSoB.setError(null);
        tvKetQua.setText(R.string.result_placeholder);
        edtSoA.requestFocus();
        Log.d(TAG, "Đã thực hiện Xóa trắng");
    }

    // ==========================================
    // TÍNH CHỈ SỐ BMI THEO CHUẨN WHO CHÂU Á
    // ==========================================

    private void tinhBmi() {
        String strCanNang = edtCanNang.getText() != null ? edtCanNang.getText().toString().trim() : "";
        String strChieuCao = edtChieuCao.getText() != null ? edtChieuCao.getText().toString().trim() : "";

        if (strCanNang.isEmpty()) {
            tilCanNang.setError(getString(R.string.err_empty));
            edtCanNang.requestFocus();
            return;
        }
        tilCanNang.setError(null);

        if (strChieuCao.isEmpty()) {
            tilChieuCao.setError(getString(R.string.err_empty));
            edtChieuCao.requestFocus();
            return;
        }
        tilChieuCao.setError(null);

        try {
            double canNang = Double.parseDouble(strCanNang);
            double chieuCao = Double.parseDouble(strChieuCao);

            if (canNang <= 0) {
                tilCanNang.setError(getString(R.string.err_weight_positive));
                Toast.makeText(this, R.string.err_positive, Toast.LENGTH_SHORT).show();
                edtCanNang.requestFocus();
                return;
            }
            if (chieuCao <= 0) {
                tilChieuCao.setError(getString(R.string.err_height_positive));
                Toast.makeText(this, R.string.err_positive, Toast.LENGTH_SHORT).show();
                edtChieuCao.requestFocus();
                return;
            }

            if (chieuCao > 3) {
                chieuCao = chieuCao / 100.0;
            }

            double bmi = canNang / (chieuCao * chieuCao);
            tvBmi.setText(String.format(Locale.getDefault(), "BMI = %.1f", bmi));

            String phanLoaiText = phanLoai(bmi);
            tvPhanLoai.setText(String.format("Phân loại: %s", phanLoaiText));

            // Tự động cuộn xuống kết quả BMI
            android.widget.ScrollView mainScrollView = findViewById(R.id.main);
            if (mainScrollView != null) {
                mainScrollView.post(() -> mainScrollView.fullScroll(View.FOCUS_DOWN));
            }

            Log.d(TAG, String.format(Locale.getDefault(), "BMI=%.2f, Phân loại: %s", bmi, phanLoaiText));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Lỗi định dạng số BMI", e);
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
        }
    }

    public String phanLoai(double bmi) {
        if (bmi < 18.5) {
            return getString(R.string.bmi_under);
        } else if (bmi < 23) {
            return getString(R.string.bmi_normal);
        } else if (bmi < 25) {
            return getString(R.string.bmi_over);
        } else {
            return getString(R.string.bmi_obese);
        }
    }
}
