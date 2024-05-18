//package com.example.ltm;
//
//import android.content.Context;
//import android.content.Intent;
//
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//
//public class test1 extends AppCompatActivity {
//    ArrayList<Button> buttons = new ArrayList<>();
//    ArrayList<TextView> textViews = new ArrayList<>();
//    Button btnBookNow_model;
//
//    Connection connection;
//
//    ImageButton imaB;
//
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_booking);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        String username = null; // Khởi tạo biến username
//
//        // Kiểm tra xem intent có null hay không trước khi lấy giá trị username
//        Intent intent = getIntent();
//        if (intent != null) {
//            username = intent.getStringExtra("username");
//        }
//
//        initButtonsAndTextViews(username);
//
//        imaB = findViewById(R.id.imageButton1);
//
//        DbConnection db = new DbConnection();
//        connection = db.conClass();
//
//        if (connection != null) {
//            try {
//                String TenCho = "SELECT * FROM Cho_Do";
//                Statement smt = connection.createStatement();
//                ResultSet ketqua = smt.executeQuery(TenCho);
//                int i = 0;
//                while (ketqua.next() && i < 10) {
//                    String maCho = ketqua.getString("MaCho");
//                    String trangThai = ketqua.getString("TrangThai");
//
//                    textViews.get(i).setText(maCho);
//
//                    updateButtonState(buttons.get(i), trangThai);
//                    if (trangThai.equals("Full")) {
//                        updateSlotStatus(maCho);
//                    }
//                    i++;
//                }
//            } catch (Exception e) {
//                Log.e("error", e.getMessage());
//            }
//        }
//
//
//        setButtonListeners(username);
//
//        imaB.setOnClickListener(v -> {
//            Intent i = new Intent(Booking.this, AfterLogin.class);
//            startActivity(i);
//        });
//    }
//    private void initButtonsAndTextViews(String username) {
//        for (int i = 0; i < 10; i++) {
//            int textViewId = getResources().getIdentifier("tvA" + (i + 1) + "_Booking", "id", getPackageName());
//            int buttonId = getResources().getIdentifier("btnBook" + (i + 1), "id", getPackageName());
//
//            TextView textView = findViewById(textViewId);
//            Button button = findViewById(buttonId);
//
//            textViews.add(textView);
//            buttons.add(button);
//
//            final int finalI = i;
//            button.setOnClickListener(v -> showDialog(textViews.get(finalI).getText().toString(), username));
//        }
//    }
//    private void setButtonListeners(String username) {
//        for (int i = 0; i < buttons.size(); i++) {
//            final int index = i;
//            buttons.get(i).setOnClickListener(v -> showDialog(textViews.get(index).getText().toString(), username));
//        }
//    }
//    private void updateButtonState(Button button, String trangThai) {
//        if (trangThai.equals("Full")) {
//            button.setText("Full");
//            button.setEnabled(false);
//        }
//    }
//
//    private void showDialog(String maCho, String username) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(Booking.this);
//        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
//        View dialogView = inflater.inflate(R.layout.model, null);
//
//        Spinner spinnerHourIn = dialogView.findViewById(R.id.spinnerHourIn);
//        Spinner spinnerMinuteIn = dialogView.findViewById(R.id.spinnerMinuteIn);
//        Spinner spinnerHourOut = dialogView.findViewById(R.id.spinnerHourOut);
//        Spinner spinnerMinuteOut = dialogView.findViewById(R.id.spinnerMinuteOut);
//
//        EditText editBienSoXe = dialogView.findViewById(R.id.editBienSoXe);
//        Button btnBookNow_model = dialogView.findViewById(R.id.btnBookNow_model);
//        TextView textViewMaCho = dialogView.findViewById(R.id.tvSlot_BookNow);
//
//        // Khởi tạo Spinner cho giờ và phút
//        initSpinner(spinnerHourIn);
//        initSpinner(spinnerMinuteIn);
//        initSpinner(spinnerHourOut);
//        initSpinner(spinnerMinuteOut);
//
//        // Hiển thị mã chỗ trống
//        textViewMaCho.setText(maCho);
//
//        // Thiết lập OnClickListener cho button Đặt vé
//        btnBookNow_model.setOnClickListener(bookNowClickListener(maCho, username, editBienSoXe, spinnerHourIn, spinnerMinuteIn, spinnerHourOut, spinnerMinuteOut));
//
//        builder.setView(dialogView);
//        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.cancel());
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//
//    // Method để khởi tạo Spinner
//    private void initSpinner(Spinner spinner) {
//        String[] items = new String[60];
//        for (int i = 0; i < 60; i++) {
//            items[i] = String.valueOf(i);
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(Booking.this, android.R.layout.simple_spinner_item, items);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//    }
//
//    // Method để tạo OnClickListener cho button Đặt vé
//    private View.OnClickListener bookNowClickListener(String maCho, String username, EditText editBienSoXe, Spinner spinnerHourIn, Spinner spinnerMinuteIn, Spinner spinnerHourOut, Spinner spinnerMinuteOut) {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String bienSoXe = editBienSoXe.getText().toString(); // Lấy giá trị từ EditText
//
//                String gioVao = spinnerHourIn.getSelectedItem().toString() + ":" + spinnerMinuteIn.getSelectedItem().toString();
//                String gioRa = spinnerHourOut.getSelectedItem().toString() + ":" + spinnerMinuteOut.getSelectedItem().toString();
//
//                // Tách chuỗi giờ và phút
//                String[] partsGioVao = gioVao.split(":");
//                String[] partsGioRa = gioRa.split(":");
//
//                // Chuyển đổi giờ và phút thành phút
//                int phutGioVao = Integer.parseInt(partsGioVao[0]) * 60 + Integer.parseInt(partsGioVao[1]);
//                int phutGioRa = Integer.parseInt(partsGioRa[0]) * 60 + Integer.parseInt(partsGioRa[1]);
//
//                if (bienSoXe.isEmpty() || gioVao.equals("0:0") || gioRa.equals("0:0")) {
//                    Toast.makeText(Booking.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
//                } else {
//                    String MaVe = maCho + "/" + String.valueOf(Math.abs(phutGioRa - phutGioVao));
//                    addNewCarToDatabase(bienSoXe);
//                    new InsertDataAsyncTask(Booking.this).execute(MaVe, gioVao, gioRa, bienSoXe, username, maCho);
//                }
//            }
//        };
//    }
//
//
//    private void updateSlotStatus(String maCho) {
//        Connection connection = null;
//        try {
//            DbConnection db = new DbConnection();
//            connection = db.conClass();
//            if (connection != null) {
//                // Cập nhật trạng thái của chỗ thành "Full" trong cơ sở dữ liệu
//                String updateQuery = "UPDATE Cho_Do SET TrangThai = 'Full' WHERE MaCho = ?";
//                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
//                preparedStatement.setString(1, maCho);
//                preparedStatement.executeUpdate();
//                // Hiển thị thông báo hoặc cập nhật giao diện nếu cần
//            } else {
//                // Xử lý khi không thể kết nối đến cơ sở dữ liệu
//            }
//        } catch (Exception e) {
//            // Xử lý ngoại lệ
//        } finally {
//            try {
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (Exception e) {
//                // Xử lý ngoại lệ khi đóng kết nối
//            }
//        }
//    }
//
//    private void addNewCarToDatabase(String bienSoXe) {
//        try {
//            if (connection != null) {
//                String insertXe = "INSERT INTO XE (Bienso, Loaixe) VALUES (?, ?)";
//                PreparedStatement preparedStatement = connection.prepareStatement(insertXe);
//                preparedStatement.setString(1, bienSoXe);
//                preparedStatement.setString(2, "Loaixe"); // Thay "Loaixe" bằng giá trị thích hợp từ giao diện hoặc dữ liệu khác
//                preparedStatement.executeUpdate();
//                Toast.makeText(this, "Thêm xe thành công", Toast.LENGTH_SHORT).show();
//
//            } else {
//                Toast.makeText(this, "Không thể kết nối đến cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "Xe đã tồn tại", Toast.LENGTH_SHORT).show();
//            Log.e("Lỗi thêm xe", e.getMessage());
//        } finally {
//            try {
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//            }
//        }
//    }
//
//    protected static class InsertDataAsyncTask extends AsyncTask<String, Void, Boolean> {
//        private Context mContext;
//        private String mMaVe;
//        private String mGioVao;
//        private String mGioRa;
//        private String mBienSoXe;
//        private String mUsername;
//        private String mMaCho;
//
//        public InsertDataAsyncTask(Context context) {
//            mContext = context;
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            // Lưu các giá trị từ params vào các biến thành viên
//            mMaVe = params[0];
//            mGioVao = params[1];
//            mGioRa = params[2];
//            mBienSoXe = params[3];
//            mUsername = params[4];
//            mMaCho = params[5];
//
//            Connection connection = null;
//            PreparedStatement preparedStatement1 = null;
//            PreparedStatement preparedStatement2 = null;
//            ResultSet resultSet1 = null;
//            ResultSet resultSet2 = null;
//
//            try {
//
//                if (connection != null) {
//                    // Kiểm tra sự tồn tại của mã vé
//                    String checkMaVeQuery = "SELECT Mave FROM VE WHERE Mave = ?";
//                    preparedStatement1 = connection.prepareStatement(checkMaVeQuery);
//                    preparedStatement1.setString(1, mMaVe);
//                    resultSet1 = preparedStatement1.executeQuery();
//                    if (resultSet1.next()) {
//                        // Nếu mã vé đã tồn tại, trả về false
//                        return false;
//                    }
//
//                    // Thêm dữ liệu vào cơ sở dữ liệu nếu mã vé là duy nhất
//                    String insertVEQuery = "INSERT INTO VE(Mave, GioVao, GioRa, Bienso, TenDN, MaCho) VALUES (?, ?, ?, ?, ?, ?)";
//
//                    preparedStatement2 = connection.prepareStatement(insertVEQuery);
//                    preparedStatement2.setString(1, mMaVe); // Mave
//                    preparedStatement2.setString(2, mGioVao); // GioVao
//                    preparedStatement2.setString(3, mGioRa); // GioRa
//                    preparedStatement2.setString(4, mBienSoXe); // Bienso
//                    preparedStatement2.setString(5, mUsername); // TenDN
//                    preparedStatement2.setString(6, mMaCho); // MaCho
//
//                    // Thực thi câu lệnh SQL và kiểm tra xem có bao nhiêu hàng đã được thêm
//                    int rowsInserted = preparedStatement2.executeUpdate();
//                    // Trả về true nếu có ít nhất một hàng đã được thêm thành công
//                    return rowsInserted > 0;
//                } else {
//                    return false;
//                }
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                return false;
//            } finally {
//                try {
//                    if (resultSet1 != null) resultSet1.close();
//                    if (resultSet2 != null) resultSet2.close();
//                    if (preparedStatement1 != null) preparedStatement1.close();
//                    if (preparedStatement2 != null) preparedStatement2.close();
//                    if (connection != null) connection.close();
//                } catch (Exception e) {
//                    Log.e("Error", e.getMessage());
//                }
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            super.onPostExecute(result);
//            if (result) {
//                Toast.makeText(mContext, "Thêm dữ liệu thành công", Toast.LENGTH_SHORT).show();
//                // Gọi phương thức cập nhật tình trạng chỗ với mMaCho ở đây
//                updateSlotStatus(mMaCho);
//            } else {
//                Toast.makeText(mContext, "Mã vé đã tồn tại hoặc thêm dữ liệu không thành công", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}
