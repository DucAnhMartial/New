package com.example.ltm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Loading extends AppCompatActivity {

    private static final int DELAY_TIME = 3000; // Thời gian chờ 3 giây
    private static final int INTERVAL_CHECK_CONNECTION = 1000; // Thời gian kiểm tra kết nối Internet (5 giây)

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading);

        scheduleInternetConnectionCheck();
    }

    private void scheduleInternetConnectionCheck() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (CheckConnectInternet.checkInternetConnection(Loading.this)) {
                    // Nếu có kết nối Internet, chuyển sang màn hình đăng nhập
                    Intent intent = new Intent(Loading.this, Login.class);
                    startActivity(intent);
                    finish(); // Đóng Activity này để không trở lại được nữa
                } else {
                    // Nếu không có kết nối Internet, lập lịch kiểm tra lại sau một khoảng thời gian
                    scheduleInternetConnectionCheck();
                    Toast.makeText(Loading.this, "Không có Internet,vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }
        }, INTERVAL_CHECK_CONNECTION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy bỏ lập lịch khi Activity bị hủy
        handler.removeCallbacksAndMessages(null);
    }
}
