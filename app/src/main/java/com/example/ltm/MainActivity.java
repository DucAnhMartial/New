package com.example.ltm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {
    Button btn2,btn_quaylai;
    EditText edt1_ten,edt2_sdt,edt3_cccd;
    Connection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btn2 = findViewById(R.id.btn2);
        btn_quaylai = findViewById(R.id.btnQuayLai);
        edt1_ten = findViewById(R.id.edt1);
        edt2_sdt = findViewById(R.id.edt2);
        edt3_cccd = findViewById(R.id.edt3);
        btn_quaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ten = edt1_ten.getText().toString();
                String sdt = edt2_sdt.getText().toString();
                String cccd = edt3_cccd.getText().toString();

                if(edt1_ten.getText().toString().isEmpty() || edt2_sdt.getText().toString().isEmpty() || edt3_cccd.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Hãy nhập dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(sdt.length() != 10){
                    Toast.makeText(MainActivity.this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(cccd.length() != 12){
                    Toast.makeText(MainActivity.this, "CCCD không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!isInformationValid(cccd, sdt)){
                    Toast.makeText(MainActivity.this, "Thông tin CCCD hoặc Số điện thoại không chính xác", Toast.LENGTH_SHORT).show();
                    return;
                }

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.activity_reset_pass_word, null);
                Button btn_ok = dialogView.findViewById(R.id.btn_ok);
                Button btn_huy = dialogView.findViewById(R.id.btn_huy);
                EditText edt_new_pass = dialogView.findViewById(R.id.edt_new_pass);
                EditText edt_new_pass2 = dialogView.findViewById(R.id.edt_new_pass2);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                btn_huy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String pass1 = edt_new_pass.getText().toString();
                        String pass2 = edt_new_pass2.getText().toString();
                        if(edt_new_pass.getText().toString().isEmpty() || edt_new_pass2.getText().toString().isEmpty()){
                            Toast.makeText(MainActivity.this, "Hãy nhập dữ liệu", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if ( !pass1.equals(pass2)){
                            Toast.makeText(MainActivity.this, "Mật khẩu không khớp, hãy nhập lại", Toast.LENGTH_SHORT).show();
                            edt_new_pass.setText(" ");
                            edt_new_pass2.setText(" ");
                        }
                        else if (!isValidPassword(edt_new_pass.getText().toString()) || edt_new_pass.getText().toString().length()<8 || !isValidPassword(edt_new_pass2.getText().toString()) || edt_new_pass2.getText().toString().length()<8){
                            Toast.makeText(MainActivity.this, "Mật khẩu phải lớn hơn 8 ký tự và chứa chữ cái, số, ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            // Cập nhật mật khẩu mới vào cơ sở dữ liệu
                            updatePassword(cccd, pass1);
                            Toast.makeText(MainActivity.this, "Mật khẩu được đổi thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }

        private boolean isInformationValid(String cccd, String sdt) {
        try {
            DbConnection db = new DbConnection();
            connection = db.conClass();
            if (connection != null) {
                String query = "SELECT * FROM KHACH_HANG WHERE CCCD = '" + cccd + "' AND Sdt = '" + sdt + "'";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                return resultSet.next(); // Trả về true nếu có kết quả từ truy vấn, ngược lại trả về false
            }
        } catch (Exception e) {
            Log.e("Lỗi truy vấn trong MainActivity", e.getMessage());
        }
        return false;
    }
    private void updatePassword(String cccd, String newPassword) {
        try {
            DbConnection db = new DbConnection();
            connection = db.conClass();
            if (connection != null) {
                String query = "UPDATE KHACH_HANG SET MatKhau = '" + newPassword + "' WHERE CCCD = '" + cccd + "'";
                Statement statement = connection.createStatement();
                statement.executeUpdate(query);
            }
        } catch (Exception e) {
            Log.e("Lỗi cập nhật mật khẩu", e.getMessage());
        }
    }

    private boolean isValidPassword(String password) {
        boolean hasDigit = password.matches(".*\\d.*"); //Kiểm tra chuỗi có chứa số hay không
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        return hasDigit && hasSpecialChar && hasLowercase ;
    }
}