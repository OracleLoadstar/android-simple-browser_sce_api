package com.example.simplebrowser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private EditText urlEdit;
    private Button shortcutBtn;
    private RadioGroup browserModeGroup;
    private ExecutorService executorService;
    private Handler mainHandler;
    private Bitmap userIconBitmap;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        
        // 简单线性布局
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        
        // 标题
        TextView titleView = new TextView(this);
        titleView.setText("SimpleBrowser - 网页快捷方式生成器");
        titleView.setTextSize(18);
        titleView.setPadding(0, 0, 0, 16);
        layout.addView(titleView);
        
        // URL输入框
        urlEdit = new EditText(this);
        urlEdit.setHint("输入网址");
        layout.addView(urlEdit, new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
        
        // 浏览器模式选择
        TextView modeLabel = new TextView(this);
        modeLabel.setText("选择浏览器模式:");
        modeLabel.setPadding(0, 24, 0, 8);
        layout.addView(modeLabel);
        
        browserModeGroup = new RadioGroup(this);
        browserModeGroup.setOrientation(RadioGroup.VERTICAL);
        
        // WebView选项
        RadioButton webViewOption = new RadioButton(this);
        webViewOption.setText("内置浏览器 (WebView)");
        webViewOption.setId(2);
        webViewOption.setChecked(true);  // 设为默认选项
        browserModeGroup.addView(webViewOption);
        
        // WebView说明
        TextView webViewDesc = new TextView(this);
        webViewDesc.setText("✓ 完全全屏\n✓ Chromium内核\n✓ 桌面模式支持");
        webViewDesc.setTextSize(12);
        webViewDesc.setPadding(32, 4, 0, 8);
        webViewDesc.setTextColor(0xFF4CAF50);
        browserModeGroup.addView(webViewDesc);
        
        layout.addView(browserModeGroup);

        // 选择自定义图标（可选）
        Button pickIconBtn = new Button(this);
        pickIconBtn.setText("选择图标（可选）");
        android.widget.LinearLayout.LayoutParams pickBtnParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        pickBtnParams.topMargin = 16;
        layout.addView(pickIconBtn, pickBtnParams);

        // 注册图片选择器
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                try (InputStream in = getContentResolver().openInputStream(uri)) {
                    Bitmap bmp = BitmapFactory.decodeStream(in);
                    userIconBitmap = bmp;
                    Toast.makeText(this, "已选择自定义图标", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "图标加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        pickIconBtn.setOnClickListener(v2 -> pickImageLauncher.launch("image/*"));
        
        // 生成快捷方式按钮
        shortcutBtn = new Button(this);
        shortcutBtn.setText("生成快捷方式");
        android.widget.LinearLayout.LayoutParams btnParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.topMargin = 24;
        layout.addView(shortcutBtn, btnParams);

        shortcutBtn.setOnClickListener(v -> {
            String url = urlEdit.getText().toString().trim();
            if (!url.isEmpty()) {
                // 确保URL有协议前缀
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url;
                }
                
                // 获取选择的浏览器模式
                int selectedId = browserModeGroup.getCheckedRadioButtonId();
                String finalUrl = url;
                
                if (userIconBitmap != null) {
                    // 使用自定义图标，跳过图标抓取与兜底
                    createShortcut(finalUrl, selectedId, userIconBitmap);
                } else {
                    // 显示进度提示并抓取网站图标
                    Toast.makeText(this, "正在获取网站图标...", Toast.LENGTH_SHORT).show();
                    fetchFaviconAndCreateShortcut(finalUrl, selectedId);
                }
            } else {
                Toast.makeText(this, "请输入网址", Toast.LENGTH_SHORT).show();
            }
        });
        
        setContentView(layout);
    }

    private void fetchFaviconAndCreateShortcut(String url, int browserMode) {
        executorService.execute(() -> {
            Bitmap favicon = null;
            
            try {
                // 尝试多个常见的favicon位置
                String[] faviconUrls = {
                    getFaviconUrl(url, "/favicon.ico"),
                    getFaviconUrl(url, "/apple-touch-icon.png"),
                    getFaviconUrl(url, "/apple-touch-icon-precomposed.png")
                };
                
                for (String faviconUrl : faviconUrls) {
                    favicon = downloadFavicon(faviconUrl);
                    if (favicon != null) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            Bitmap finalFavicon = favicon;
            mainHandler.post(() -> createShortcut(url, browserMode, finalFavicon));
        });
    }
    
    private String getFaviconUrl(String siteUrl, String faviconPath) {
        try {
            URL url = new URL(siteUrl);
            return url.getProtocol() + "://" + url.getHost() + faviconPath;
        } catch (Exception e) {
            return null;
        }
    }
    
    private Bitmap downloadFavicon(String faviconUrl) {
        if (faviconUrl == null) return null;
        
        try {
            URL url = new URL(faviconUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private void createShortcut(String url, int browserMode, Bitmap favicon) {
        android.content.pm.ShortcutManager shortcutManager =
                (android.content.pm.ShortcutManager) getSystemService(Context.SHORTCUT_SERVICE);
        
        // 创建WebView Intent
        String uriString = url + "#desktopMode=true";
        Intent intent = new Intent(this, WebViewActivity.class)
                .setAction(Intent.ACTION_VIEW)
                .setData(android.net.Uri.parse(uriString))
                .putExtra("url", url)
                .putExtra("desktopMode", true);
        
        android.content.pm.ShortcutInfo.Builder builder = 
                new android.content.pm.ShortcutInfo.Builder(this, url + "_" + System.currentTimeMillis())
                .setShortLabel("网页快捷方式")
                .setLongLabel(url + " (WebView)")
                .setIntent(intent);
        
        // 如果成功获取了favicon，使用它作为图标
        if (favicon != null) {
            builder.setIcon(android.graphics.drawable.Icon.createWithBitmap(favicon));
            Toast.makeText(this, "已获取网站图标", Toast.LENGTH_SHORT).show();
        } else {
            builder.setIcon(android.graphics.drawable.Icon.createWithResource(this, android.R.drawable.ic_menu_view));
            Toast.makeText(this, "使用默认图标", Toast.LENGTH_SHORT).show();
        }
        
        android.content.pm.ShortcutInfo shortcut = builder.build();
        shortcutManager.requestPinShortcut(shortcut, null);
        
        Toast.makeText(this, "快捷方式已创建 (WebView)", Toast.LENGTH_LONG).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}