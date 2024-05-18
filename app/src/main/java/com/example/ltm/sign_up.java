package com.example.ltm;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.CollationElementIterator;
import java.util.jar.Attributes;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import android.os.AsyncTask;

public class sign_up extends AppCompatActivity {
    Button btn1, btn2;
    TextView edtname, edtcccd, edtsdt, edttendn, edtmatkhau;

    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btn1 = findViewById(R.id.btnSignUp_signUp);
        btn2 = findViewById(R.id.btnQuayLai_signup);
        edtname = findViewById(R.id.edtName_Signup);
        edtcccd = findViewById(R.id.edtCCCD_Signup);
        edtsdt = findViewById(R.id.edtSDT_Signup);
        edttendn = findViewById(R.id.edtTENDN_Signup);
        edtmatkhau = findViewById(R.id.edtMatKhau_Signup);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            Toast.makeText(sign_up.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xử lý logic khi có kết nối Internet
                String ten = edtname.getText().toString();
                String sdt = edtsdt.getText().toString();
                String cccd = edtcccd.getText().toString();
                String ten_login = edttendn.getText().toString();
                String pass = edtmatkhau.getText().toString();

                if (ten.isEmpty() || sdt.isEmpty() || cccd.isEmpty() || ten_login.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(sign_up.this, "Bạn hãy nhập dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                } else if (sdt.length() != 10) {
                    Toast.makeText(sign_up.this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                } else if (cccd.length() != 12) {
                    Toast.makeText(sign_up.this, "CCCD không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;

                } else if (!isValidPassword(pass)|| pass.length()<8){
                    Toast.makeText(sign_up.this, "Mật khẩu phải lớn hơn 8 ký tự và chứa chữ cái, số, ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    // Khởi tạo và thực thi AsyncTask để thêm dữ liệu vào cơ sở dữ liệu
                    new InsertDataAsyncTask().execute(ten, sdt, cccd, ten_login, pass);
                }
            }
        });


    }
    private boolean isValidPassword(String password) {
        boolean hasDigit = password.matches(".*\\d.*"); //Kiểm tra chuỗi có chứa số hay không
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        return hasDigit && hasSpecialChar && hasLowercase ;
    }
    private class InsertDataAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Connection connection = null;
            PreparedStatement preparedStatement1 = null;
            PreparedStatement preparedStatement2 = null;
            ResultSet resultSet1 = null;
            ResultSet resultSet2 = null;
            try {
                DbConnection db = new DbConnection();
                connection = db.conClass();
                if (connection != null) {
                    // Kiểm tra sự tồn tại của CCCD
                    String checkCCCDQuery = "SELECT CCCD FROM KHACH_HANG WHERE CCCD = ?";
                    preparedStatement1 = connection.prepareStatement(checkCCCDQuery);
                    preparedStatement1.setString(1, params[2]);
                    resultSet1 = preparedStatement1.executeQuery();
                    if (resultSet1.next()) {
                        // Nếu CCCD đã tồn tại, trả về false
                        return false;
                    }

                    // Kiểm tra sự tồn tại của Tên đăng nhập
                    String checkTenDNQuery = "SELECT TenDN FROM TAI_KHOAN WHERE TenDN = ?";
                    preparedStatement2 = connection.prepareStatement(checkTenDNQuery);
                    preparedStatement2.setString(1, params[3]);
                    resultSet2 = preparedStatement2.executeQuery();
                    if (resultSet2.next()) {
                        // Nếu Tên đăng nhập đã tồn tại, trả về false
                        return false;
                    }

                    // Thêm dữ liệu vào cơ sở dữ liệu nếu CCCD và Tên đăng nhập là duy nhất
                    String insertKHQuery = "INSERT INTO KHACH_HANG(CCCD, Ten, SDT, Email) VALUES (?, ?, ?, '')";
                    String insertTKQuery = "INSERT INTO TAI_KHOAN(TenDN, Matkhau, CCCD) VALUES (?, ?, ?)";
                    preparedStatement1 = connection.prepareStatement(insertKHQuery);
                    preparedStatement1.setString(1, params[2]);
                    preparedStatement1.setString(2, params[0]);
                    preparedStatement1.setString(3, params[1]);
                    preparedStatement1.executeUpdate();
                    preparedStatement2 = connection.prepareStatement(insertTKQuery);
                    preparedStatement2.setString(1, params[3]);
                    preparedStatement2.setString(2, params[4]);
                    preparedStatement2.setString(3, params[2]);
                    preparedStatement2.executeUpdate();

                    // Trả về true nếu thêm dữ liệu thành công
                    return true;
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
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(sign_up.this, "Tài khoản đã được tạo, Vui lòng quay lại đăng nhập", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(sign_up.this, "CCCD hoặc Tên đăng nhập đã được sử dụng", Toast.LENGTH_SHORT).show();
            }
        }
    }

}