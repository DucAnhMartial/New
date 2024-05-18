package com.example.ltm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Delayed;

import kotlinx.coroutines.Delay;

public class Booking extends AppCompatActivity {

    ArrayList<Button> buttonArrayList = new ArrayList<>();
    ImageButton imageButtonExit;
    ArrayList<TextView> textViewArrayList = new ArrayList<>();

    ArrayList<String> maChoList = new ArrayList<>();
    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String macho = "";
        ConnectSQL();
        if (connection != null) {
            try {
                String cho = "select MaCho from Cho_Do";
                Statement smt = connection.createStatement();
                ResultSet ketqua = smt.executeQuery(cho);
                while (ketqua.next()) {
                    macho = ketqua.getString(1);
                    maChoList.add(macho);
                }
            } catch (Exception e) {
                Log.e("Lỗi truy van MaCho", e.getMessage());
            }

        }
        // Khởi tạo các button và textView
        initButtonsAndTextViews();
        imageButtonExit = findViewById(R.id.imageButton1);
        imageButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Booking.this, AfterLogin.class);
                startActivity(i);
                finish();
            }
        });

        // Lấy dữ liệu từ Intent
        Intent i = getIntent();
        String username = i.getStringExtra("username");

        // Hiển thị Dialog View khi nhấn nút
        showBookingDialog(buttonArrayList, username, maChoList);

        // Cập nhật trạng thái của các button và textView
        switchStatuesButton(buttonArrayList, textViewArrayList);
    }

    // Phương thức để khởi tạo các button và textView
    private void initButtonsAndTextViews() {
        for (int i = 0; i < 10; i++) {
            int textViewId = getResources().getIdentifier("tvA" + (i + 1) + "_Booking", "id", getPackageName());
            int buttonId = getResources().getIdentifier("btnBook" + (i + 1), "id", getPackageName());

            TextView textView = findViewById(textViewId);
            Button button = findViewById(buttonId);

            buttonArrayList.add(button);
            textViewArrayList.add(textView);
        }
    }

    // Xử lý khi nhấn nút thoát

    // Kết nối cơ sở dữ liệu
    private void ConnectSQL() {
        DbConnection db = new DbConnection();
        connection = db.conClass();
    }


    // Hiển thị Dialog View khi nhấn nút
    private void showBookingDialog(ArrayList<Button> buttonArrayList, String username, ArrayList<String> maChoList) {
        for (int i = 0; i < buttonArrayList.size(); i++) {
            final int position = i; // Biến tạm để lưu vị trí của button
            Button button = buttonArrayList.get(i);
            String maCho1 = maChoList.get(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Sử dụng biến position thay vì i trong đoạn code bên trong
                    final String tempMaCho = maCho1; // Biến tạm để lưu giá trị maCho1
                    AlertDialog.Builder builder = new AlertDialog.Builder(Booking.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.model, null);
                    builder.setView(dialogView);
                    builder.setTitle("BOOKING"); // Tiêu đề của dialog

                    Spinner spinnerHourIn = dialogView.findViewById(R.id.spinnerHourIn);
                    Spinner spinnerMinuteIn = dialogView.findViewById(R.id.spinnerMinuteIn);
                    Spinner spinnerHourOut = dialogView.findViewById(R.id.spinnerHourOut);
                    Spinner spinnerMinuteOut = dialogView.findViewById(R.id.spinnerMinuteOut);
                    Button btnBookNow_model = dialogView.findViewById(R.id.btnBookNow_model);
                    TextView textViewMaCho = dialogView.findViewById(R.id.tvSlot_BookNow);
                    EditText editBienSo = dialogView.findViewById(R.id.editBienSoXe);

                    // Khởi tạo Spinner cho giờ và phút
                    initSpinnerHours(spinnerHourIn);
                    initSpinnerMintues(spinnerMinuteIn);
                    initSpinnerHours(spinnerHourOut);
                    initSpinnerMintues(spinnerMinuteOut);

                    //Xét chỗ
                    textViewMaCho.setText(tempMaCho);

                    // Lắng nghe sự kiện khi nhấn nút BOOK NOW
                    btnBookNow_model.setOnClickListener(bookNowClickListener(tempMaCho, username, editBienSo, spinnerHourIn, spinnerMinuteIn, spinnerHourOut, spinnerMinuteOut));
                    // Thiết lập nút "Đóng" cho dialog
                    builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); // Đóng dialog khi nhấn nút "Đóng"
                        }
                    });

                    // Khởi tạo và hiển thị AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
    }


    private View.OnClickListener bookNowClickListener(String maCho, String username, EditText editBienSoXe, Spinner spinnerHourIn, Spinner spinnerMinuteIn, Spinner spinnerHourOut, Spinner spinnerMinuteOut) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bienSoXe = editBienSoXe.getText().toString(); // Lấy giá trị từ EditText

                String gioVao = spinnerHourIn.getSelectedItem().toString() + ":" + spinnerMinuteIn.getSelectedItem().toString();
                String gioRa = spinnerHourOut.getSelectedItem().toString() + ":" + spinnerMinuteOut.getSelectedItem().toString();

                // Tách chuỗi giờ và phút
                String[] partsGioVao = gioVao.split(":");
                String[] partsGioRa = gioRa.split(":");

                // Chuyển đổi giờ và phút thành phút
                int phutGioVao = Integer.parseInt(partsGioVao[0]) * 60 + Integer.parseInt(partsGioVao[1]);
                int phutGioRa = Integer.parseInt(partsGioRa[0]) * 60 + Integer.parseInt(partsGioRa[1]);

                if (bienSoXe.isEmpty() || gioVao.equals("0:0") || gioRa.equals("0:0")) {
                    Toast.makeText(Booking.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    String MaVe = maCho + "/" + String.valueOf(Math.abs(phutGioRa - phutGioVao));
                    AddNewCar(bienSoXe);
                    new InsertDataAsyncTask(Booking.this).execute(MaVe, gioVao, gioRa, bienSoXe, username, maCho);
                    Intent intent = new Intent();
                    finish();
                    startActivity(intent);
                }
            }
        };
    }
    // Method để khởi tạo Spinner
    private void initSpinnerMintues(Spinner spinner) {
        String[] items = new String[60];
        for (int i = 0; i < 60; i++) {
            items[i] = String.valueOf(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Booking.this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void initSpinnerHours(Spinner spinner) {
        String[] items = new String[24];
        for (int i = 0; i < 24; i++) {
            items[i] = String.valueOf(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Booking.this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // Cập nhật trạng thái của các button và textView
    protected void switchStatuesButton(ArrayList<Button> buttonArrayList, ArrayList<TextView> textViewArrayList) {
        ConnectSQL();
        if (connection != null) {
            try {
                String TenCho = "SELECT * FROM Cho_Do";
                Statement smt = connection.createStatement();
                ResultSet ketqua = smt.executeQuery(TenCho);
                int i = 0;
                while (ketqua.next() && i < 10) {
                    String maCho = ketqua.getString(1);
                    String trangThai = ketqua.getString(2);

                    textViewArrayList.get(i).setText(maCho);
                    updateButtonState(buttonArrayList.get(i), trangThai);

                    i++;
                }
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
        }
    }

    // Cập nhật trạng thái của button
    protected void updateButtonState(Button button, String trangThai) {
        if (trangThai.equals("Full")) {
            button.setText("Full");
            button.setEnabled(false);
        }
    }

    // Thêm mới xe vào cơ sở dữ liệu
    private void AddNewCar(String bienSoXe) {
        ConnectSQL();
        try {
            if (connection != null) {
                String insertXe = "INSERT INTO XE (Bienso, Loaixe) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertXe);
                preparedStatement.setString(1, bienSoXe);
                preparedStatement.setString(2, "");
                preparedStatement.executeUpdate();
//                Toast.makeText(this, "Thêm xe thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Không thể kết nối đến cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
//            Toast.makeText(this, "Xe đã tồn tại", Toast.LENGTH_SHORT).show();
            Log.e("Lỗi AddNewCar", e.getMessage());
        }
    }

    private void updateSlotStatus(String maCho) {
        Connection connection = null;
        try {
            DbConnection db = new DbConnection();
            connection = db.conClass();
            if (connection != null) {
                // Cập nhật trạng thái của chỗ thành "Full" trong cơ sở dữ liệu
                String updateQuery = "UPDATE Cho_Do SET TrangThai = 'Full' WHERE MaCho = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setString(1, maCho);
                preparedStatement.executeUpdate();
                // Hiển thị thông báo hoặc cập nhật giao diện nếu cần
            } else {
                // Xử lý khi không thể kết nối đến cơ sở dữ liệu
            }
        } catch (Exception e) {
            // Xử lý ngoại lệ
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                // Xử lý ngoại lệ khi đóng kết nối
            }
        }
    }

    // Lớp AsyncTask để thực hiện việc chèn dữ liệu vào cơ sở dữ liệu
    protected class InsertDataAsyncTask extends AsyncTask<String, Void, Boolean> {
        private Context mContext;
        private String mMaVe;
        private String mGioVao;
        private String mGioRa;
        private String mBienSoXe;
        private String mUsername;
        private String mMaCho;

        public InsertDataAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // Lưu các giá trị từ params vào các biến thành viên
            mMaVe = params[0];
            mGioVao = params[1];
            mGioRa = params[2];
            mBienSoXe = params[3];
            mUsername = params[4];
            mMaCho = params[5];

            Connection connection = null;
            PreparedStatement preparedStatement1 = null;
            PreparedStatement preparedStatement2 = null;
            ResultSet resultSet1 = null;
            ResultSet resultSet2 = null;

            try {
                DbConnection db = new DbConnection();
                connection = db.conClass();

                if (connection != null) {
                    // Kiểm tra sự tồn tại của mã vé
                    String checkMaVeQuery = "SELECT Mave FROM VE WHERE Mave = ?";
                    preparedStatement1 = connection.prepareStatement(checkMaVeQuery);
                    preparedStatement1.setString(1, mMaVe);
                    resultSet1 = preparedStatement1.executeQuery();
                    if (resultSet1.next()) {
                        // Nếu mã vé đã tồn tại, trả về false
                        return false;
                    }

                    // Thêm dữ liệu vào cơ sở dữ liệu nếu mã vé là duy nhất
                    String insertVEQuery = "INSERT INTO VE(Mave, GioRa, GioVao, Bienso, TenDN, MaCho) VALUES (?, ?, ?, ?, ?, ?)";

                    preparedStatement2 = connection.prepareStatement(insertVEQuery);
                    preparedStatement2.setString(1, mMaVe); // Mave
                    preparedStatement2.setString(2, mGioVao); // GioVao
                    preparedStatement2.setString(3, mGioRa); // GioRa
                    preparedStatement2.setString(4, mBienSoXe); // Bienso
                    preparedStatement2.setString(5, mUsername); // TenDN
                    preparedStatement2.setString(6, mMaCho); // MaCho

                    // Thực thi câu lệnh SQL và kiểm tra xem có bao nhiêu hàng đã được thêm
                    int rowsInserted = preparedStatement2.executeUpdate();
                    // Trả về true nếu có ít nhất một hàng đã được thêm thành công
                    return rowsInserted > 0;
                } else {
                    return false;
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                return false;
            } finally {
                try {
                    if (resultSet1 != null) resultSet1.close();
                    if (resultSet2 != null) resultSet2.close();
                    if (preparedStatement1 != null) preparedStatement1.close();
                    if (preparedStatement2 != null) preparedStatement2.close();
                    if (connection != null) connection.close();
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(mContext, "Thêm dữ liệu thành công", Toast.LENGTH_SHORT).show();
                // Gọi phương thức cập nhật tình trạng chỗ với mMaCho ở đây
                updateSlotStatus(mMaCho);
            } else {
                Toast.makeText(mContext, "Mã vé đã tồn tại hoặc thêm dữ liệu không thành công", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
