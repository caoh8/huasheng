package com.ime.music;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ime.music.util.DeviceInfo;
import com.tendcloud.tenddata.TCAgent;

public class FeedbackActivity extends SlideBackActivity {

    private TextView mBtnFbBack;
    private WebView mWeb;
    private Drawable back;
    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(FeedbackActivity.this,"反馈");
    }

    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(FeedbackActivity.this,"反馈");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除顶部标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor("#F9F9F9"));
        }
        setContentView(R.layout.activity_feedback);
        mBtnFbBack = findViewById(R.id.btn_fb_back);
        Drawable jiantou_zuo = ContextCompat.getDrawable(FeedbackActivity.this, R.drawable.jiantou_zuo_std);
        jiantou_zuo.setBounds(0,0,50,50);
        mBtnFbBack.setCompoundDrawables(jiantou_zuo,null,null,null);
        mBtnFbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        back = getResources().getDrawable(R.drawable.jiantou);
//        back.setBounds(0,0,60,60);
//        mBtnFbBack.setCompoundDrawables(back,null,null,null);

        mWeb = findViewById(R.id.wv_feedback);
        openCamera();
        initWeb();

    }

    private void initWeb() {
        String path = "https://support.qq.com/product/83053";	//访问网址
        WebSettings settings = mWeb.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setBlockNetworkLoads(false);
        settings.setSupportMultipleWindows(false);
        settings.setBlockNetworkImage(false);         // 这个要加上

        mWeb.loadUrl(path);

/** app准备好这些参数 如不需要传空字符串即可 */
        String clientInfo = DeviceInfo.getDeviceModel();
//        String imei = DeviceInfo.getIMEI(getApplicationContext());

        /* 准备post参数 */
        String postData = "clientInfo=" + clientInfo;
        mWeb.postUrl(path, postData.getBytes());

        mWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });


        mWeb.setWebChromeClient(new WebChromeClient() {

            //扩展浏览器上传文件
            //3.0++版本
//            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
//                openFileChooserImpl(uploadMsg);
//            }
//
//            //3.0--版本
//            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
//                openFileChooserImpl(uploadMsg);
//            }
//
//            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
//                openFileChooserImpl(uploadMsg);
//            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                onenFileChooseImpleForAndroid(filePathCallback);
                return true;
            }
        });
    }

    private void openCamera() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(FeedbackActivity.this, Manifest.permission.CAMERA)) {
            //提示用户开启权限，
            String[] perms = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(FeedbackActivity.this, perms, 111);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 111:
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    Log.e("fuxx", "打开权限");
                } else {
                    Toast.makeText(this, "请手动到设置中打开应用相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public ValueCallback<Uri> mUploadMessage;

//    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
//        mUploadMessage = uploadMsg;
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("image/*");
//        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
//    }

    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    private void onenFileChooseImpleForAndroid(ValueCallback<Uri[]> filePathCallback) {
        mUploadMessageForAndroid5 = filePathCallback;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //点击返回按钮的时候判断有没有上一页
        if (mWeb.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
            mWeb.goBack(); // goBack()表示返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}


