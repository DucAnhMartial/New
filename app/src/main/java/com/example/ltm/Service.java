package com.example.ltm;

import android.app.DownloadManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Service extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText edt_sent;
    ImageView send,img_exit;
    List<MessModel> list;
    MessAdapter adapter;
    private OkHttpClient client = new OkHttpClient();
    private Handler handler = new Handler(Looper.getMainLooper());

    public static final MediaType JSON = MediaType.get("application/json");

//    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        recyclerView = findViewById(R.id.recyclerView);
        edt_sent = findViewById(R.id.edt_sent);
        send = findViewById(R.id.img_sent);
        list = new ArrayList<>();
        img_exit = findViewById(R.id.img_exit);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MessAdapter(list);
        recyclerView.setAdapter(adapter);

        img_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = edt_sent.getText().toString();
                if(question.isEmpty()){
                    Toast.makeText(Service.this, "Hãy nhập câu hỏi của bạn!", Toast.LENGTH_SHORT).show();

                }
                else{

                        addToChat(question, MessModel.SENT_BY_ME);
                        edt_sent.setText("");
                        callAPIWithRetry(question, 3);


                }
            }
        });

    }
    private void callAPIWithRetry(String question, int retryCount) {
        if (retryCount > 5) { // Giới hạn số lần thử lại
            addResponse("Failed to load after retries.");
            return;
        }

        list.add(new MessModel("TYPING.......", MessModel.SENT_BY_BOT));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo-instruct");
            jsonObject.put("prompt", question);
            jsonObject.put("max_tokens", 50);
            jsonObject.put("temperature", 0.5);
        } catch (JSONException e) {
            addResponse("Error creating JSON: " + e.getMessage());
            return; // Dừng xử lý nếu JSON không hợp lệ
        }
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer sk-proj-ErRQ7slHsv4MPiDcNhK2T3BlbkFJzC4AmhKOGmBqVJ4heAbX")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    addResponse("Failed to load: HTTP " + response.code());
                    return;
                }
                String responseData = response.body().string();
                try {
                    JSONObject jsonObject1 = new JSONObject(responseData);
                    JSONArray jsonArray = jsonObject1.getJSONArray("choices");
                    String result = jsonArray.getJSONObject(0).getString("text");
                    addResponse(result.trim());
                } catch (JSONException e) {
                    addResponse("Error parsing the response: " + e.getMessage());
                } finally {
                    response.body().close(); // Đảm bảo đóng body của response
                }
            }
        });
    }



//    private void callAPI(String question) {
//        list.add(new MessModel("TYPING.......", MessModel.SENT_BY_BOT));
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("model", "gpt-3.5-turbo-instruct");
//            jsonObject.put("prompt", question);
//            jsonObject.put("max_tokens", 5000);
//            jsonObject.put("temperature", 0.5);
//
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
//        Request request = new Request.Builder()
//                .url("https://api.openai.com/v1/completions")
//                .header("Authorization", "Bearer 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")
//                .post(body)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                addResponse("Failed to load: " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (response.isSuccessful() && response.body() != null) {
//                    String responseData = response.body().string();
//                    try {
//                        JSONObject jsonObject1 = new JSONObject(responseData);
//                        JSONArray jsonArray = jsonObject1.getJSONArray("choices");
//                        String result = jsonArray.getJSONObject(0).getString("text");
//                        addResponse(result.trim());
//                    } catch (JSONException e) {
//                        addResponse("Error parsing the response: " + e.getMessage());
//                    }
//                } else {
//                    addResponse("Failed to load: HTTP Code " + response.code());
//                }
//            }
//        });
//    }

    private void addResponse(String response) {
        runOnUiThread(() -> {
            list.remove(list.size() - 1);
            addToChat(response, MessModel.SENT_BY_BOT);
        });
    }

    private void addToChat(String text, String sentByMe) {
        runOnUiThread(() -> {
            list.add(new MessModel(text, sentByMe));
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        });
    }

}