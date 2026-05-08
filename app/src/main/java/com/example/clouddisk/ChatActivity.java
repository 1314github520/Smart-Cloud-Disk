package com.example.clouddisk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<MessageItem> messageList = new ArrayList<>();
    private EditText etInput;
    private List<JSONObject> chatHistory = new ArrayList<>();
    private AppDatabase db;
    private String username;
    
    private String attachedFilePath = null;
    private String attachedFileName = null;
    private View layoutAttached;
    private TextView tvAttachedName;
    private List<FileIndex> cachedAllFiles = new ArrayList<>();
    private String currentPickerPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = AppDatabase.getInstance(this);
        SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        username = sp.getString("username", "admin");

        MaterialToolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvChat = findViewById(R.id.rv_chat);
        etInput = findViewById(R.id.et_msg);
        Button btnSend = findViewById(R.id.btn_send);
        ImageButton btnAttach = findViewById(R.id.btn_attach_file);

        layoutAttached = findViewById(R.id.layout_attached_file);
        tvAttachedName = findViewById(R.id.tv_attached_file_name);
        findViewById(R.id.btn_remove_attachment).setOnClickListener(v -> removeAttachment());

        adapter = new ChatAdapter(messageList);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendMessage());
        btnAttach.setOnClickListener(v -> selectFileToDiscuss());

        addMessage("AI", "你好！我是你的云盘助手。有什么我可以帮你的吗？");
    }

    private void removeAttachment() {
        attachedFilePath = null;
        attachedFileName = null;
        layoutAttached.setVisibility(View.GONE);
    }

    private void addMessage(String role, String text) {
        messageList.add(new MessageItem(role, text));
        adapter.notifyItemInserted(messageList.size() - 1);
        rvChat.scrollToPosition(messageList.size() - 1);
        
        try {
            JSONObject msg = new JSONObject();
            msg.put("role", role.equalsIgnoreCase("User") ? "user" : "assistant");
            msg.put("content", text);
            chatHistory.add(msg);
        } catch (Exception ignored) {}
    }

    private void sendMessage() {
        String text = etInput.getText().toString().trim();
        if (text.isEmpty()) return;

        addMessage("User", text);
        etInput.setText("");
        requestAiResponse();
    }

    private void selectFileToDiscuss() {
        new Thread(() -> {
            cachedAllFiles = db.userDao().getAllFilesForAi(username);
            currentPickerPath = username + "/";
            runOnUiThread(this::showHierarchicalPicker);
        }).start();
    }

    private void showHierarchicalPicker() {
        List<FileIndex> displayList = getFilesAt(currentPickerPath, cachedAllFiles);
        String[] names = new String[displayList.size() + (currentPickerPath.equals(username + "/") ? 0 : 1)];
        
        int offset = 0;
        if (!currentPickerPath.equals(username + "/")) {
            names[0] = ".. (返回上一级)";
            offset = 1;
        }
        
        for (int i = 0; i < displayList.size(); i++) {
            FileIndex f = displayList.get(i);
            names[i + offset] = (f.isDir ? "[文件夹] " : "[文件] ") + f.name;
        }

        String displayTitle = currentPickerPath.replace(username + "/", "");
        if (displayTitle.isEmpty()) displayTitle = "根目录";

        new AlertDialog.Builder(this)
                .setTitle("选择文件提问 - " + displayTitle)
                .setItems(names, (dialog, which) -> {
                    if (!currentPickerPath.equals(username + "/") && which == 0) {
                        String temp = currentPickerPath.substring(0, currentPickerPath.length() - 1);
                        currentPickerPath = temp.substring(0, temp.lastIndexOf("/") + 1);
                        showHierarchicalPicker();
                        return;
                    }
                    
                    int index = which - (currentPickerPath.equals(username + "/") ? 0 : 1);
                    FileIndex selected = displayList.get(index);
                    if (selected.isDir) {
                        currentPickerPath = selected.fullPath;
                        showHierarchicalPicker();
                    } else {
                        attachedFilePath = selected.fullPath;
                        attachedFileName = selected.name;
                        layoutAttached.setVisibility(View.VISIBLE);
                        tvAttachedName.setText("针对文件: " + attachedFileName);
                        Toast.makeText(this, "已关联文件，现在提问吧", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private List<FileIndex> getFilesAt(String parentPath, List<FileIndex> all) {
        List<FileIndex> result = new ArrayList<>();
        for (FileIndex f : all) {
            String path = f.fullPath;
            if (path.startsWith(parentPath) && !path.equals(parentPath)) {
                String relative = path.substring(parentPath.length());
                if (!relative.contains("/") || (relative.indexOf("/") == relative.length() - 1)) {
                    result.add(f);
                }
            }
        }
        java.util.Collections.sort(result, (f1, f2) -> {
            if (f1.isDir && !f2.isDir) return -1;
            if (!f1.isDir && f2.isDir) return 1;
            return f1.name.compareToIgnoreCase(f2.name);
        });
        return result;
    }

    private void requestAiResponse() {
        new Thread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("username", username);

                JSONArray historyArray = new JSONArray();
                for (JSONObject msg : chatHistory) {
                    historyArray.put(msg);
                }
                json.put("history", historyArray);

                if (attachedFilePath != null) {
                    json.put("attached_file_path", attachedFilePath);
                }

                List<FileIndex> allFiles = db.userDao().getAllFilesForAi(username);
                JSONArray fileArray = new JSONArray();
                for (FileIndex f : allFiles) {
                    JSONObject fObj = new JSONObject();
                    fObj.put("name", f.name);
                    fObj.put("fullPath", f.fullPath);
                    fObj.put("isDir", f.isDir);
                    fileArray.put(fObj);
                }
                json.put("files", fileArray);

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();

                RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url("http://47.97.50.135:8000/ai-assistant")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> Toast.makeText(ChatActivity.this, "请求失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String respStr = response.body().string();
                                JSONObject result = new JSONObject(respStr);
                                String answer = result.getString("answer");
                                int todayTotal = result.getInt("today_total");
                                runOnUiThread(() -> {
                                    addMessage("AI", answer);
                                    if (getSupportActionBar() != null) {
                                        getSupportActionBar().setSubtitle("今日消耗: " + todayTotal + "/100000");
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            runOnUiThread(() -> Toast.makeText(ChatActivity.this, "服务器错误: " + response.code(), Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}