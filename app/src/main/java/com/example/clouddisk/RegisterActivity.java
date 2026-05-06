package com.example.clouddisk;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    // 移除本地 db 引用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etUser = findViewById(R.id.re_username);
        EditText etPass = findViewById(R.id.re_password);
        EditText etConfirm = findViewById(R.id.re_confirm_password);
        EditText etInvite = findViewById(R.id.re_invite_code);
        android.widget.CheckBox cb2fa = findViewById(R.id.cb_use_2fa);
        Button btnReg = findViewById(R.id.btn_do_register);

        btnReg.setOnClickListener(v -> {
            String name = etUser.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();
            String invite = etInvite.getText().toString().trim();
            boolean use2FA = cb2fa.isChecked();

            if (name.isEmpty() || pass.isEmpty() || invite.isEmpty()) {
                Toast.makeText(this, "用户名、密码或邀请码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirm)) {
                Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    json.put("username", name);
                    json.put("password", pass);
                    json.put("inviteCode", invite);
                    json.put("use2FA", use2FA);

                    RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
                    Request request = new Request.Builder()
                            .url(Config.getBackendUrl() + "/register")
                            .post(body)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        String respStr = response.body().string();
                        if (response.isSuccessful()) {
                            if (use2FA) {
                                JSONObject respJson = new JSONObject(respStr);
                                org.json.JSONArray secret = respJson.getJSONArray("secret");
                                
                                JSONObject qrJson = new JSONObject();
                                qrJson.put("issuer", "CloudDisk");
                                qrJson.put("account", name);
                                qrJson.put("secret", secret);
                                
                                runOnUiThread(() -> show2FAQRCode(name, qrJson.toString()));
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "注册成功，云端已同步！", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                            }
                        } else {
                            runOnUiThread(() -> Toast.makeText(this, "注册失败: " + respStr, Toast.LENGTH_SHORT).show());
                        }
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "连不上服务器", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }

    private void show2FAQRCode(String username, String secretJson) {
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=400x400&data=" + android.net.Uri.encode(secretJson);

        android.widget.ImageView imageView = new android.widget.ImageView(this);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        imageView.setPadding(padding, padding, padding, padding);
        com.bumptech.glide.Glide.with(this).load(qrUrl).into(imageView);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("开启安全盾")
                .setMessage("账号: " + username + "\n请使用 CDAuthy 扫描二维码绑定。")
                .setView(imageView)
                .setCancelable(false)
                .setPositiveButton("我已完成扫码", (dialog, which) -> finish())
                .show();
    }

    private boolean createFolderForUser(String username) {
        try {
            OkHttpClient client = new OkHttpClient();
            // 直接在 WebDAV 根目录下建立同名文件夹
            String url = Config.getWebDavUrl() + username;
            Request request = new Request.Builder()
                    .url(url)
                    .method("MKCOL", null)
                    .header("Authorization", okhttp3.Credentials.basic(Config.WEBDAV_USER, Config.WEBDAV_PASS))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                // 201 创建成功，405 已存在（也视为成功）
                return response.isSuccessful() || response.code() == 405;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
