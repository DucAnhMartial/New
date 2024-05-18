package com.example.ltm;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Personal extends AppCompatActivity {
    TextView tvName_Personal, tvCCCD_Personal, tvSDT_Personal, tvSoCho_Personal, tvslot_ticket, tvBienso_ticket, tvgiovao_ticket, tvgiora_ticket;
    ImageView img1;
    Connection connection;
    LinearLayout linearLayoutTickets;
    Button btn_ticket_personal_qr;
    int socho = 0;
    String qrcode1 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo linearLayoutTickets sau khi setContentView
        linearLayoutTickets = findViewById(R.id.linear_layout_tickets);

        tvName_Personal = findViewById(R.id.tvName_Personal);
        tvCCCD_Personal = findViewById(R.id.tvCCCD_Personal);
        tvSDT_Personal = findViewById(R.id.tvSDT_Personal);
        tvSoCho_Personal = findViewById(R.id.tvSoCho_Personal);
        btn_ticket_personal_qr = linearLayoutTickets.findViewById(R.id.btn_ticket_personal_qr);
        img1 = findViewById(R.id.img1);
        ham();


        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void ham() {
        connectionSQL();
        int count1 = 0;
        if (connection != null) {
            try {
                Intent intent = getIntent();
                if (intent != null) {
                    String username = intent.getStringExtra("username");

                    if (username != null) {
                        String NameOfCustomer = "select * from KHACH_HANG inner Join TAI_KHOAN on KHACH_HANG.CCCD = TAI_KHOAN.CCCD where TenDN = '" + username + "'";
                        String count = "SELECT COUNT(*) AS socho FROM VE WHERE TenDN = '" + username + "'";
                        String Thongtinve = "SELECT * FROM VE WHERE TenDN = '" + username + "'";
                        Statement smt = connection.createStatement();
                        Statement smt2 = connection.createStatement();
                        Statement smt3 = connection.createStatement();
                        ResultSet ketqua = smt.executeQuery(NameOfCustomer);
                        ResultSet ketqua2 = smt2.executeQuery(count);
                        ResultSet ketqua3 = smt3.executeQuery(Thongtinve);
                        linearLayoutTickets = findViewById(R.id.linear_layout_tickets);
                        while (ketqua.next() && ketqua2.next()) {
                            tvName_Personal.setText(ketqua.getString(2)); // Hiển thị tên khách hàng lên TextView
                            tvCCCD_Personal.setText(ketqua.getString(1));
                            tvSDT_Personal.setText(ketqua.getString(3));
                            socho = Integer.parseInt(ketqua2.getString(1));
                            tvSoCho_Personal.setText(String.valueOf(socho));
                            count1 = Integer.parseInt(ketqua2.getString(1));

                            int i = 0;
                            while (ketqua3.next() && i < count1) {
                                // Lấy giá trị của cột "MaCho" cho hàng hiện tại
                                String maCho = ketqua3.getString("MaCho");

                                // Tạo một View mới từ layout XML của ticket
                                View ticketView = getLayoutInflater().inflate(R.layout.ticket_personal, null);

                                // Tùy chỉnh thông tin vé trên View này
                                TextView vitri = ticketView.findViewById(R.id.edt_ticket_personal_slot);
                                TextView bienso = ticketView.findViewById(R.id.edt_ticket_personal_bienso);
                                TextView giovao = ticketView.findViewById(R.id.edt_ticket_personal_gio_vao);
                                TextView giora = ticketView.findViewById(R.id.edt_ticket_personal_gio_ra);
                                Button btnHuy = ticketView.findViewById(R.id.btn_ticket_personal_huy);
                                TextView ve = ticketView.findViewById(R.id.Ve);

                                String mave = ketqua3.getString(1);
                                // Gán giá trị tương ứng từ cột "MaCho" cho TextView của từng vé
                                vitri.setText(maCho);

                                ve.setText(mave);
                                bienso.setText(ketqua3.getString("Bienso"));
                                giovao.setText(ketqua3.getString("GioVao").substring(11, 16));
                                giora.setText(ketqua3.getString("GioRa").substring(11, 16));

                                // Thêm View này vào LinearLayout
                                linearLayoutTickets.addView(ticketView);

                                // Gọi phương thức setupDeleteButton để thiết lập sự kiện xóa vé cho nút xóa hiện tại
                                setupDeleteButton(btnHuy, mave);
                                // Tạo mã QR từ mã vé
                                Button btnQrCode = ticketView.findViewById(R.id.btn_ticket_personal_qr);
                                final Bitmap qrCodeBitmap = generateQRCode(mave, 500, 500);
                                btnQrCode.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showQRCodeDialog(qrCodeBitmap);
                                    }
                                });
                                i++;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }


        }
    }

    // Phương thức xử lý sự kiện khi người dùng ấn nút xóa
    private void onDeleteButtonClick(Button btnHuy, String mave) {
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Personal.this);
                builder.setMessage("Bạn có chắc chắn muốn xóa không?")
                        .setCancelable(false)
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    String updateChoDoQuery = "UPDATE Cho_Do SET TrangThai = 'Empty' WHERE MaCho IN (SELECT MaCho FROM VE WHERE Mave = '" + mave + "')";
                                    Statement smtUpdateChoDo = connection.createStatement();
                                    smtUpdateChoDo.executeUpdate(updateChoDoQuery);
                                    Statement smtDelete = connection.createStatement();
                                    String deleteQuery = "DELETE FROM VE WHERE Mave = '" + mave + "'";
                                    smtDelete.executeUpdate(deleteQuery);
                                    try {
                                        Statement smtDeleteXe = connection.createStatement();
                                        String deleteQueryXE = "DELETE FROM XE WHERE Bienso = (SELECT Bienso FROM VE WHERE Mave = '" + mave + "')";
                                        smtDeleteXe.executeUpdate(deleteQueryXE);
                                    } catch (Exception e) {
                                        Log.e("Lỗi xóa xe", e.getMessage());
                                    }

                                    // Sau khi xóa, giảm số chỗ và cập nhật giao diện
                                    socho--;
                                    tvSoCho_Personal.setText(String.valueOf(socho));

                                    // Loại bỏ vé khỏi giao diện sau khi xóa thành công
                                    linearLayoutTickets.removeView((View) btnHuy.getParent());
                                    Toast.makeText(Personal.this, "Xóa vé thành công", Toast.LENGTH_SHORT).show();
                                    // Cập nhật trạng thái của chỗ đỗ thành "empty"

                                } catch (Exception e) {
                                    Log.e("DeleteError", "Lỗi khi xóa vé: " + e.getMessage());
                                    Toast.makeText(Personal.this, "Có lỗi trong quá trình xóa vé", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void setupDeleteButton(Button btnHuy, String mave) {
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteButtonClick(btnHuy, mave);
            }
        });
    }

    private void connectionSQL() {
        DbConnection db = new DbConnection();
        connection = db.conClass();
    }

    private Bitmap generateQRCode(String data, int width, int height) throws WriterException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        return bitmap;
    }

    // Method to show QR code in a dialog
    private void showQRCodeDialog(Bitmap qrCodeBitmap) {
        // Create dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.qrcode);


        ImageView imageViewQRCode = dialog.findViewById(R.id.imageView_qr_code);


        imageViewQRCode.setImageBitmap(qrCodeBitmap);


        dialog.show();
    }
}
