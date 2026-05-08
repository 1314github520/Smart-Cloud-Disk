package com.example.clouddisk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 初始化系统启动页 API
        androidx.core.splashscreen.SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        
        // 开启全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                           WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_splash);

        // 延迟 1500 毫秒后执行跳转
        new Handler().postDelayed(() -> {
            SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
            if (sp.getBoolean("is_logged_in", false)) {
                startActivity(new Intent(SplashActivity.this, FileListActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            finish(); // 销毁启动页，防止返回键回来
        }, 1500);
    }
}