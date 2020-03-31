package com.ime.music.net;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.ime.music.CLog;
import com.ime.music.R;
import com.ime.music.ShezhiActivity;
import com.ime.music.util.ConstantUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RenewSoft {
    public static final int INSTALL_PERMISS_CODE = 0101;
    private Activity activity;

    public RenewSoft(Activity activity) {
        this.activity = activity;
    }

    public void requestRenew() {
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

//                            mBtnAbout.post(new Runnable() {
//                                @Override
//                                public void run() {
                            showRenewDialog();
//                                }
//                            });
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

    private String url;
    private String saveDir;
    private Dialog dialogDowning;

    private void showRenewDialog() {
        AlertDialog.Builder renewDialog =
                new AlertDialog.Builder(activity);
        final View dialogView = LayoutInflater.from(activity)
                .inflate(R.layout.request_renew_ayout, null);
        renewDialog.setView(dialogView);
        renewDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CLog.i("开始下载更新");
                        CLog.e(ConstantUtil.softDownloadAddress);
                        url = ConstantUtil.softDownloadAddress;
                        saveDir = activity.getCacheDir().getPath();
                        CLog.e(saveDir);
//                        DownloadAPK.download(ConstantUtil.softDownloadAddress, saveDir);

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        View view = View.inflate(activity, R.layout.download_progress, null);
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
//                                mBtnAbout.post(new Runnable() {
//                                    @Override
//                                    public void run() {
                                setInstallPermission();
//                                    }
//                                });
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

    static public void installAPK(Activity activity, String url, String saveDir) {
        File file = new File(saveDir, url.substring(url.lastIndexOf("/") + 1));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(activity,
                    "com.ime.music.fileProvider", file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        activity.startActivity(intent);
    }

    public void setInstallPermission() {
        boolean haveInstallPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先判断是否有安装未知来源应用的权限
            haveInstallPermission = activity.getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {
                //弹框提示用户手动打开
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
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
                installAPK(activity, url, saveDir);
            }
        } else {
            installAPK(activity, url, saveDir);
        }
    }


    /**
     * 开启安装未知来源权限
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void toInstallPermissionSettingIntent() {
        Uri packageURI = Uri.parse("package:" + activity.getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        activity.startActivityForResult(intent, INSTALL_PERMISS_CODE);
    }

}
