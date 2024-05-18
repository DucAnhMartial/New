package com.example.ltm;

import com.example.ltm.CheckConnectInternet;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Login extends AppCompatActivity {
Button btnLogin_login,btnThoat_login,btnSignUp_login;
EditText edtTenDN_login,edtMatKhau_login;
TextView tv1;
Connection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnLogin_login = findViewById(R.id.btnLogin_login);
        btnThoat_login = findViewById(R.id.btnThoat_login);
        btnSignUp_login = findViewById(R.id.btnSignUp_login);
        edtTenDN_login = findViewById(R.id.edtTenDN_login);
        edtMatKhau_login = findViewById(R.id.edtMatKhau_Login);
        tv1 = findViewById(R.id.tv1);
        if (!CheckConnectInternet.checkInternetConnection(Login.this)) {
            Toast.makeText(Login.this, "Không có internet vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Login.this, "Đã có internet", Toast.LENGTH_SHORT).show();
            btnLogin_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tendn = edtTenDN_login.getText().toString();
                    String pass = edtMatKhau_login.getText().toString();
                    if (!CheckConnectInternet.checkInternetConnection(Login.this)) {
                        Toast.makeText(Login.this, "Không có internet vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                    } else {
                        if (tendn.isEmpty() || pass.isEmpty()) {
                            Toast.makeText(Login.this, "Vui lòng nhập đầy đủ thông tin người dùng", Toast.LENGTH_SHORT).show();
                        } else {
                            DbConnection db = new DbConnection();
                            connection = db.conClass();
                            if (connection != null) {
                                try {
                                    String sqlstatement = "SELECT TenDN, MatKhau FROM TAI_KHOAN WHERE TenDN = '" + tendn + "' AND MatKhau = '" + pass + "'";
                                    Statement smt = connection.createStatement();
                                    ResultSet resultSet = smt.executeQuery(sqlstatement);
                                    if (resultSet.next()) { // Kiểm tra xem ResultSet có hàng nào không
                                        Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(Login.this, AfterLogin.class);
                                        String username = resultSet.getString(1);
                                        i.putExtra("username", username);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(Login.this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Log.e("Có lỗi xảy ra, vui lòng thử lại sau", e.getMessage());
                                } finally {
                                    try {
                                        if (connection != null) connection.close();
                                    } catch (Exception e) {
                                        Log.e("Có lỗi khi đóng kết nối", e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                }
            });


            btnSignUp_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!CheckConnectInternet.checkInternetConnection(Login.this)) {
                        Toast.makeText(Login.this, "Không có internet vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i3 = new Intent(Login.this, sign_up.class);
                        startActivity(i3);
                    }
                }
            });

            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i2 = new Intent(Login.this, MainActivity.class);
                    startActivity(i2);
                }
            });
        }
        btnThoat_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}