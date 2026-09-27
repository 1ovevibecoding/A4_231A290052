package vn.edu.vhu.ltdd.a4events;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // MSSV: 231A290052 - Họ tên: Huỳnh Anh Tuấn
    private static final String TAG = "A4_231A290052";
    private static final String KEY_HISTORY = "KEY_CALC_HISTORY";

    // NC4: Sử dụng TextInputLayout và TextInputEditText
    private TextInputLayout tilSoA, tilSoB, tilCanNang, tilChieuCao;
    private TextInputEditText edtSoA, edtSoB, edtCanNang, edtChieuCao;
    private TextView tvKetQua, tvBmi, tvPhanLoai, tvLichSu;

    // NC2: Danh sách lưu 5 phép tính gần nhất
    private ArrayList<String> lichSuPhepTinh = new ArrayList<>();

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

        // 1. Ánh xạ View (findViewById đặt sau setContentView)
        anhXaView();

        // 2. Thiết lập bộ lắng nghe sự kiện (Event Listeners)
        thietLapSuKien();

        // Tự động xóa lỗi khi người dùng gõ phím (Real-time error clearance)
        caiDatAutoClearError();

        // NC2: Khôi phục lịch sử phép tính nếu xoay màn hình
        if (savedInstanceState != null) {
            ArrayList<String> savedList = savedInstanceState.getStringArrayList(KEY_HISTORY);
            if (savedList != null) {
                lichSuPhepTinh = savedList;
                capNhatGiaoDienLichSu();
            }
        }
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
        tvLichSu = findViewById(R.id.tvLichSu);
    }

    /**
     * Bước 2: Gán bộ lắng nghe sự kiện click cho các nút
     */
    private void thietLapSuKien() {
        Button btnCong = findViewById(R.id.btnCong);
        Button btnTru = findViewById(R.id.btnTru);
        Button btnNhan = findViewById(R.id.btnNhan);
        Button btnChia = findViewById(R.id.btnChia);
        Button btnPhanTram = findViewById(R.id.btnPhanTram);
        Button btnDoiDau = findViewById(R.id.btnDoiDau);
        Button btnXoa = findViewById(R.id.btnXoa);
        Button btnTinhBmi = findViewById(R.id.btnTinhBmi);
        Button btnXoaLichSu = findViewById(R.id.btnXoaLichSu);

        // Cách 1: Lambda riêng biệt cho nút Cộng và Trừ
        btnCong.setOnClickListener(v -> tinhToan('+'));
        btnTru.setOnClickListener(v -> tinhToan('-'));

        // Cách 2: OnClickListener dùng chung cho Nhân, Chia và % (NC1)
        View.OnClickListener chungListener = v -> {
            int id = v.getId();
            if (id == R.id.btnNhan) {
                tinhToan('*');
            } else if (id == R.id.btnChia) {
                tinhToan('/');
            } else if (id == R.id.btnPhanTram) {
                tinhToan('%');
            }
        };
        btnNhan.setOnClickListener(chungListener);
        btnChia.setOnClickListener(chungListener);
        btnPhanTram.setOnClickListener(chungListener);

        // Nút nâng cao NC1: Đảo dấu (±)
        btnDoiDau.setOnClickListener(v -> doiDau());

        // Nút Xóa trắng
        btnXoa.setOnClickListener(v -> xoaTrang());

        // Nút Tính BMI
        btnTinhBmi.setOnClickListener(v -> tinhBmi());

        // Tự động tính BMI khi bấm nút Xong / Enter trên bàn phím
        edtChieuCao.setOnEditorActionListener((v, actionId, event) -> {
            tinhBmi();
            return false;
        });

        // Nút Xóa lịch sử (NC2)
        btnXoaLichSu.setOnClickListener(v -> xoaLichSu());
    }

    /**
     * Tự động xóa thông báo lỗi trên TextInputLayout khi người dùng nhập dữ liệu
     */
    private void caiDatAutoClearError() {
        edtSoA.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilSoA.setError(null);
            }
        });
        edtSoB.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilSoB.setError(null);
            }
        });
        edtCanNang.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilCanNang.setError(null);
            }
        });
        edtChieuCao.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilChieuCao.setError(null);
            }
        });
    }

    // ==========================================
    // MÁY TÍNH BỐN PHÉP TOÁN & NÂNG CAO (NC1, NC2, NC4)
    // ==========================================

    /**
     * Thực hiện phép tính (+, -, *, /, %) với quy trình kiểm tra dữ liệu 4 lớp
     * @param phepToan Ký tự phép toán (+, -, *, /, %)
     */
    private void tinhToan(char phepToan) {
        String chuoiA = edtSoA.getText() != null ? edtSoA.getText().toString().trim() : "";
        String chuoiB = edtSoB.getText() != null ? edtSoB.getText().toString().trim() : "";

        // LỚP 1: Kiểm tra rỗng và hiển thị lỗi trên TextInputLayout (NC4)
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

        // LỚP 2: Kiểm tra định dạng số với Double.parseDouble trong try-catch
        double a, b;
        try {
            a = Double.parseDouble(chuoiA);
            b = Double.parseDouble(chuoiB);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Dữ liệu nhập không phải số hợp lệ: chuoiA='" + chuoiA + "', chuoiB='" + chuoiB + "'", e);
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
            return;
        }

        // LỚP 3: Kiểm tra miền giá trị / trường hợp đặc biệt (chia hoặc mod cho 0)
        if ((phepToan == '/' || phepToan == '%') && b == 0) {
            tilSoB.setError(getString(R.string.err_divide_zero));
            Toast.makeText(this, R.string.err_divide_zero, Toast.LENGTH_SHORT).show();
            edtSoB.requestFocus();
            return;
        }
        tilSoB.setError(null);

        // LỚP 4: Xử lý nghiệp vụ tính toán
        double ketQua = 0;
        switch (phepToan) {
            case '+':
                ketQua = a + b;
                break;
            case '-':
                ketQua = a - b;
                break;
            case '*':
                ketQua = a * b;
                break;
            case '/':
                ketQua = a / b;
                break;
            case '%':
                ketQua = a % b;
                break;
            default:
                ketQua = 0;
                break;
        }

        // Định dạng chuỗi kết quả có truyền Locale chuẩn xác
        char kyHieu = phepToan;
        if (phepToan == '/') kyHieu = '÷';
        else if (phepToan == '*') kyHieu = '×';
        else if (phepToan == '-') kyHieu = '−';

        String ketQuaDinhDang = String.format(Locale.getDefault(), "%.2f %c %.2f = %.2f", a, kyHieu, b, ketQua);
        tvKetQua.setText(ketQuaDinhDang);
        Log.d(TAG, "Thực hiện phép tính: " + ketQuaDinhDang);

        // NC2: Thêm vào lịch sử tính toán 5 phép tính gần nhất
        themVaoLichSu(ketQuaDinhDang);
    }

    /**
     * Nâng cao NC1: Đảo dấu (±) cho ô nhập đang focus hoặc ô A/B
     */
    private void doiDau() {
        TextInputEditText target = edtSoB.hasFocus() ? edtSoB : edtSoA;
        TextInputLayout tilTarget = (target == edtSoB) ? tilSoB : tilSoA;

        String text = target.getText() != null ? target.getText().toString().trim() : "";
        if (text.isEmpty()) {
            target.setText("-");
            target.setSelection(target.getText().length());
            return;
        }

        if (text.equals("-")) {
            target.setText("");
            return;
        }

        try {
            double val = Double.parseDouble(text);
            val = val * -1;
            // Nếu là số nguyên thì hiển thị số nguyên cho đẹp
            if (val == (long) val) {
                target.setText(String.valueOf((long) val));
            } else {
                target.setText(String.valueOf(val));
            }
            target.setSelection(target.getText().length());
            tilTarget.setError(null);
            Log.d(TAG, "Đã đảo dấu giá trị: " + target.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Xóa trắng các trường nhập của máy tính và đặt lại kết quả
     */
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
    // NÂNG CAO NC2: LỊCH SỬ 5 PHÉP TÍNH GẦN NHẤT
    // ==========================================

    private void themVaoLichSu(String phepTinh) {
        // Chèn vào đầu danh sách (mới nhất hiển thị trước)
        lichSuPhepTinh.add(0, phepTinh);
        // Giữ tối đa 5 phần tử
        if (lichSuPhepTinh.size() > 5) {
            lichSuPhepTinh.remove(lichSuPhepTinh.size() - 1);
        }
        capNhatGiaoDienLichSu();
    }

    private void capNhatGiaoDienLichSu() {
        if (lichSuPhepTinh.isEmpty()) {
            tvLichSu.setText(R.string.history_empty);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lichSuPhepTinh.size(); i++) {
            sb.append(i + 1).append(". ").append(lichSuPhepTinh.get(i));
            if (i < lichSuPhepTinh.size() - 1) {
                sb.append("\n");
            }
        }
        tvLichSu.setText(sb.toString());
    }

    private void xoaLichSu() {
        lichSuPhepTinh.clear();
        capNhatGiaoDienLichSu();
        Toast.makeText(this, "Đã xóa lịch sử tính toán", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lưu lịch sử 5 phép tính qua vòng đời khi xoay màn hình (NC2)
        outState.putStringArrayList(KEY_HISTORY, lichSuPhepTinh);
        Log.d(TAG, "onSaveInstanceState: Đã lưu " + lichSuPhepTinh.size() + " phép tính vào bundle");
    }

    // ==========================================
    // TÍNH CHỈ SỐ BMI & NÂNG CAO NC3
    // ==========================================

    /**
     * Tính chỉ số BMI và phân loại theo khuyến nghị WHO dành cho khu vực Châu Á
     */
    private void tinhBmi() {
        String strCanNang = edtCanNang.getText() != null ? edtCanNang.getText().toString().trim() : "";
        String strChieuCao = edtChieuCao.getText() != null ? edtChieuCao.getText().toString().trim() : "";

        // Kiểm tra rỗng
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

            // Kiểm tra miền giá trị: cân nặng và chiều cao phải > 0
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

            // Quy đổi đơn vị: Cho phép nhập 1.70 (m) hoặc 170 (cm)
            if (chieuCao > 3) {
                chieuCao = chieuCao / 100.0;
            }

            // Tính toán BMI theo công thức
            double bmi = canNang / (chieuCao * chieuCao);

            tvBmi.setText(String.format(Locale.getDefault(), "BMI = %.1f", bmi));

            // Phân loại kết quả
            String phanLoaiText = phanLoai(bmi);
            tvPhanLoai.setText(String.format("Phân loại: %s", phanLoaiText));

            // Nâng cao NC3: Đổi màu dòng kết quả phân loại theo mức độ
            int colorRes = layMauPhanLoai(bmi);
            tvPhanLoai.setTextColor(ContextCompat.getColor(this, colorRes));

            // Tự động cuộn xuống dưới cùng để người dùng thấy ngay kết quả
            android.widget.ScrollView mainScrollView = findViewById(R.id.main);
            if (mainScrollView != null) {
                mainScrollView.post(() -> mainScrollView.fullScroll(View.FOCUS_DOWN));
            }

            Log.d(TAG, String.format(Locale.getDefault(), "BMI=%.2f, Phân loại: %s", bmi, phanLoaiText));

        } catch (NumberFormatException e) {
            Log.e(TAG, "Lỗi nhập liệu BMI không đúng định dạng số", e);
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Phân loại BMI theo khuyến nghị của WHO cho khu vực Châu Á
     * Tách riêng phương thức để dễ kiểm thử (Unit Testing sau này)
     */
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

    /**
     * Nâng cao NC3: Lấy mã màu tương ứng với phân loại BMI
     * - Thiếu cân: Xanh lam (bmi_under_color)
     * - Bình thường: Xanh lục (bmi_normal_color)
     * - Thừa cân: Vàng cam (bmi_over_color)
     * - Béo phì: Đỏ (bmi_obese_color)
     */
    private int layMauPhanLoai(double bmi) {
        if (bmi < 18.5) {
            return R.color.bmi_under_color;
        } else if (bmi < 23) {
            return R.color.bmi_normal_color;
        } else if (bmi < 25) {
            return R.color.bmi_over_color;
        } else {
            return R.color.bmi_obese_color;
        }
    }

    /**
     * Helper TextWatcher để code gọn hơn
     */
    private static abstract class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
