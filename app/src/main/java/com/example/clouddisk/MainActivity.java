package com.example.clouddisk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 激活开屏页
        androidx.core.splashscreen.SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // 检查登录状态
        SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        if (sp.getBoolean("is_logged_in", false)) {
            startActivity(new Intent(this, FileListActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            performLogin(user, pass, null);
        });
        // 在 MainActivity 的 onCreate 里面添加：
        TextView tvRegister = findViewById(R.id.tv_register_link);
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 检查更新
        checkUpdate();
    }

    private void performLogin(String user, String pass, String code) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("username", user);
                json.put("password", pass);
                if (code != null) json.put("code", code);

                RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
                Request request = new Request.Builder()
                        .url(Config.getBackendUrl() + "/login")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String respStr = response.body().string();
                    if (response.isSuccessful()) {
                        if (respStr.equals("NEED_2FA")) {
                            runOnUiThread(() -> show2FADialog(user, pass));
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
                                getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
                                        .putBoolean("is_logged_in", true)
                                        .putString("username", user)
                                        .putString("password", pass)
                                        .apply();
                                startActivity(new Intent(this, FileListActivity.class));
                                finish();
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            String error = respStr.equals("FAIL_2FA") ? "验证码错误" : "账号或密码错误";
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "连不上服务器", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void show2FADialog(String user, String pass) {
        EditText etCode = new EditText(this);
        etCode.setHint("6 位数字验证码");
        etCode.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etCode.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(6)});

        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        container.setPadding(padding, padding / 2, padding, 0);
        container.addView(etCode);

        new MaterialAlertDialogBuilder(this)
                .setTitle("安全验证")
                .setMessage("请输入安全盾上的 6 位验证码")
                .setView(container)
                .setPositiveButton("验证", (dialog, which) -> {
                    String code = etCode.getText().toString();
                    if (code.length() == 6) {
                        performLogin(user, pass, code);
                    } else {
                        Toast.makeText(this, "请输入6位验证码", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("返回", null)
                .show();
    }

    private void checkUpdate() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Config.UPDATE_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 检查更新失败，通常不打扰用户
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonStr = response.body().string();
                        JSONObject json = new JSONObject(jsonStr);
                        int remoteVersionCode = json.getInt("versionCode");
                        String versionName = json.getString("versionName");
                        String updateMessage = json.getString("updateMessage");
                        String downloadUrl = json.getString("downloadUrl");

                        if (remoteVersionCode > com.example.clouddisk.BuildConfig.VERSION_CODE) {
                            runOnUiThread(() -> showUpdateDialog(versionName, updateMessage, downloadUrl));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showUpdateDialog(String versionName, String message, String url) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("发现新版本: " + versionName)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("立即下载", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                })
                .setNegativeButton("稍后再说", null)
                .show();
    }
}
