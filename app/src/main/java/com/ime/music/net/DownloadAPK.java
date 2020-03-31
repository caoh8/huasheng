package com.ime.music.net;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.ProgressBar;

import com.ime.music.CLog;

import java.io.File;

public class DownloadAPK {
//    private ProgressBar progressBar;
    public static void download(final String url, final String saveDir) {
        Download.get().download(url, saveDir, new Download.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                CLog.e("下载完成");
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.fromFile(new File(saveDir, url.substring(url.lastIndexOf("/") + 1))),
//                        "application/vnd.android.package-archive");
//                startActivity(intent);

            }
            @Override
            public void onDownloading(int progress) {
//                progressBar.setProgress(progress);
                CLog.e("下载进度： " + progress);
            }
            @Override
            public void onDownloadFailed() {
                CLog.e("下载失败");
            }
        });
    }
}
