package com.ime.music;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.ime.music.net.Download;
import com.ime.music.share.Share;
import com.ime.music.util.ConstantUtil;
import com.ime.music.util.DeviceInfo;
import com.tendcloud.tenddata.TCAgent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShezhiActivity extends SlideBackActivity {

    private Button mBtnAbout;
    private Button mBtnFeedback;
    private Button mBtnShare;
//    private Button mBtnHelp;
    private TextView mBtnShezhi;
    private Drawable jianyou_you;
    private Drawable jiantou_zuo;
    private Button mBtnRenew;
    private int INSTALL_PERMISS_CODE = 111;

    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(ShezhiActivity.this,"设置页");
    }

    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(ShezhiActivity.this,"设置页");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//去除顶部标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor("#F9F9F9"));
        }
        setContentView(R.layout.activity_shezhi);

        mBtnShezhi = findViewById(R.id.btn_shezhi);
        jiantou_zuo = ContextCompat.getDrawable(getApplicationContext(), R.drawable.jiantou_zuo_std);
        jiantou_zuo.setBounds(0,0,50,50);
        mBtnShezhi.setCompoundDrawables(jiantou_zuo,null,null,null);
        mBtnShezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mBtnAbout = findViewById(R.id.btn_about);
        jianyou_you = ContextCompat.getDrawable(ShezhiActivity.this, R.drawable.jiantou);
        jianyou_you.setBounds(0,0,50,50);
        mBtnAbout.setCompoundDrawables(null,null,jianyou_you,null);
        mBtnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShezhiActivity.this,AboutActivity.class);
                startActivity(intent);
                Map kv = new HashMap();
                kv.put("点击", "设置页-关于");
                TCAgent.onEvent(ShezhiActivity.this, "设置页-关于点击", "", kv);
            }
        });

        mBtnFeedback = findViewById(R.id.btn_feedback);
        mBtnFeedback.setCompoundDrawables(null,null,jianyou_you,null);
        mBtnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShezhiActivity.this,FeedbackActivity.class);
                startActivity(intent);
                Map kv = new HashMap();
                kv.put("点击", "设置页-反馈");
                TCAgent.onEvent(ShezhiActivity.this, "设置页-反馈点击", "", kv);
            }
        });

        mBtnShare = findViewById(R.id.btn_share);
        mBtnShare.setCompoundDrawables(null,null,jianyou_you,null);

        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Share.ShareSoft(v.getContext());
                Map kv = new HashMap();
                kv.put("点击", "设置页-分享");
                TCAgent.onEvent(ShezhiActivity.this, "设置页-分享点击", "", kv);
            }
        });

//        mBtnHelp = findViewById(R.id.btn_help);
//        mBtnHelp.setCompoundDrawables(null,null,jianyou_you,null);

//        mBtnHelp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ShezhiActivity.this,HelpActivity.class);
//                startActivity(intent);
//                Map kv = new HashMap();
//                kv.put("点击", "设置页-帮助");
//                TCAgent.onEvent(ShezhiActivity.this, "设置页-帮助点击", "", kv);
//            }
//        });

        mBtnRenew = findViewById(R.id.btn_renew_soft);
        mBtnRenew.setCompoundDrawables(null,null,jianyou_you,null);

        findViewById(R.id.btn_renew_soft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRenew();
            }
        });

        TextView version = findViewById(R.id.shezhi_version);
        version.setText(String.format("V%s", DeviceInfo.getClientver()));
    }

    private void requestRenew() {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .build();

        String url = ConstantUtil.checkSoftUrl + "hash=" + ConstantUtil.softHash;
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                CLog.d("更新请求失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String ret = response.body().string();

                try {
                    JSONObject o = new JSONObject(ret);

                    int status = o.getInt("status");

                    if (200 == status) {
                        JSONObject jData = o.getJSONObject("data");
                        boolean update = jData.getBoolean("update");
                        if (update) {
                            String hash = jData.getString("hash");
                            String version = jData.getString("version");
                            String address = jData.getString("address");
                            ConstantUtil.softHash = hash;
                            ConstantUtil.softDownloadAddress = address;
                            ConstantUtil.hasNew = true;
                            ConstantUtil.version = version;

                            mBtnAbout.post(new Runnable() {
                                @Override
                                public void run() {
                                    showRenewDialog(version);
                                }
                            });
                        } else if (ConstantUtil.hasNew) {
                            mBtnAbout.post(new Runnable() {
                                @Override
                                public void run() {
                                    showRenewDialog(ConstantUtil.version);
                                }
                            });
                        } else {
                            mBtnAbout.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "已是最新版本", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showDownloadingDialog(Context context) {
        ;
    }

    String url;
    String saveDir;
    Dialog dialogDowning;
    private void showRenewDialog(String content) {
        AlertDialog.Builder renewDialog =
                new AlertDialog.Builder(ShezhiActivity.this);
        final View dialogView = LayoutInflater.from(ShezhiActivity.this)
                .inflate(R.layout.request_renew_ayout,null);
        TextView textView = dialogView.findViewById(R.id.request_renew_tv);
        if (null != content)
            textView.setText(content);
        renewDialog.setView(dialogView);
        renewDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CLog.i("开始下载更新");
                        CLog.e(ConstantUtil.softDownloadAddress);
                        url = ConstantUtil.softDownloadAddress;
                        saveDir = getCacheDir().getPath();
                        CLog.e(saveDir);
//                        DownloadAPK.download(ConstantUtil.softDownloadAddress, saveDir);

                        AlertDialog.Builder builder = new AlertDialog.Builder(ShezhiActivity.this);
                        View view = View.inflate(ShezhiActivity.this, R.layout.download_progress, null);
                        TextView textView = view.findViewById(R.id.download_tv);
                        builder.setView(view);
                        builder.setTitle("正在下载...");
                        builder.setCancelable(false);
                        dialogDowning = builder.create();
                        dialogDowning.show();
                        Download.get().download(url, saveDir, new Download.OnDownloadListener() {
                            @Override
                            public void onDownloadSuccess() {
                                dialogDowning.dismiss();
                                CLog.e("下载完成");
                                mBtnAbout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setInstallPermission();
                                    }
                                });
                            }

                            @Override
                            public void onDownloading(int progress) {
//                                CLog.e("下载进度： " + progress);
                                textView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText("下载进度: " + progress + "%");
                                    }
                                });

                            }

                            @Override
                            public void onDownloadFailed() {
                                dialogDowning.dismiss();
                                CLog.e("下载失败");
                            }
                        });
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CLog.i("取消更新");
            }
        });
        renewDialog.create().show();
    }

    private void installAPK(String url, String saveDir) {
        File file = new File(saveDir, url.substring(url.lastIndexOf("/") + 1));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
                    "com.ime.music.fileProvider", file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
        }else{
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    public void setInstallPermission(){
        boolean haveInstallPermission;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //先判断是否有安装未知来源应用的权限
            haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            if(!haveInstallPermission){
                //弹框提示用户手动打开
                AlertDialog.Builder dialog = new AlertDialog.Builder(ShezhiActivity.this);
                dialog.setTitle("安装权限");
                dialog.setMessage("需要打开允许来自此来源，请去设置中开启此权限");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toInstallPermissionSettingIntent();
                    }
                });
                dialog.create().show();
//                dialog.show();
            } else {
                installAPK(url, saveDir);
            }
        } else {
            installAPK(url, saveDir);
        }
    }


    /**
     * 开启安装未知来源权限
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void toInstallPermissionSettingIntent() {
        Uri packageURI = Uri.parse("package:"+getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
        startActivityForResult(intent, INSTALL_PERMISS_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == INSTALL_PERMISS_CODE) {
            Toast.makeText(this,"安装应用",Toast.LENGTH_SHORT).show();
            installAPK(url, saveDir);
        }
    }
}
