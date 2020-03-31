package com.ime.music.share;

import android.content.Context;

import com.ime.music.CLog;
import com.ime.music.util.ConstantUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Share {

    interface ShareSoft {
        void parse(String result);
        void share();
    }

    static private void SoftUrl(ShareSoft s) {
        String url = ConstantUtil.shareSoftUrl;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                CLog.d("onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                CLog.d("onResponse: " + result);
                s.parse(result);
                s.share();
            }
        });
    }

    private static PlatformActionListener callback = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            // TODO 分享成功后的操作或者提示
            CLog.d("分享成功");
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            // TODO 失败，打印throwable为错误码
            CLog.d("分享失败" + throwable.toString());
        }

        @Override
        public void onCancel(Platform platform, int i) {
            // TODO 分享取消操作
            CLog.d("分享取消");
        }
    };

    static public void ShareSoft(Context context) {
        Share.ShareInfo shareInfo = new Share.ShareInfo();
        shareInfo.setAuthorName("聊天太闷？来嗑花生");
        showShare(context, null, shareInfo);
    }

    //platform自动识别应用
    static public void showShare(Context context, String platform, ShareInfo info) {

        SoftUrl(new ShareSoft() {
            private String softUrl;
            @Override
            public void parse(String result) {
                if (null == result) return;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");

                    if(200 == status) {
                        softUrl = jsonObject.getString("data");
                    } else {
                        softUrl = ConstantUtil.staticSoftUrl;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CLog.e("软件下载网页获取失败 使用默认地址");
                    softUrl = ConstantUtil.staticSoftUrl;
                }
            }

            @Override
            public void share() {

                info.setTitle_url(softUrl);
                info.setUrl(softUrl);
                showShare_oks(context, platform, info);
            }
        });
    }

    private static String getPlatform() {
        switch (ConstantUtil.currentApp) {
            case QQ:
                return QQ.NAME;
            case ORTHER:
                return null;
            case WEIXIN:
                return Wechat.NAME;
        }
        return null;
    }

    private static void showShare_oks(Context context, String platform, ShareInfo info) {
        final OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform == null) {
            platform = getPlatform();
        }
        if (platform != null) {
            oks.setPlatform(platform);
        }
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        if(null == info.fileName) info.fileName = "花生语音包";
        oks.setTitle(info.fileName);
        if (/*!Trime.isIsOpenedShare()&& */ 0 != info.duration) oks.setTitle("收到一条语音"); //0 != info.duration 表示是否为音频
        if (null == info.title_url) info.title_url = ConstantUtil.staticSoftUrl;
        oks.setTitleUrl(info.title_url);

        if (null != info.music_url)
            oks.setMusicUrl(info.music_url);
        if (0 != info.duration) oks.setText(String.valueOf(info.duration) + "″");
        else oks.setText(info.authorName);//微信分享需要
        //分享网络图片
        if (null == info.image_url) info.image_url = ConstantUtil.imageUrl;
        oks.setImageUrl(info.image_url);
//        oks.setSite(info.title_url);
        if (null == info.url) info.url = ConstantUtil.staticSoftUrl;
        oks.setUrl(info.url);
        /**
         * 下面的这些参数必须要写，某些不写会导致某些平台分享失败
         */
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(entity.title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(entity.shoreUrl);
        // text是分享文本，所有平台都需要这个字段
//        oks.setText(entity.intro);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数,
        // 使用 imagePath 必须保证SDcard下面存在此张图片
        //imagePath,imageUrl 必须保留一个，否则微信不能分享，或者分享过去的图片都是应用的 logo
//        String imagePath = ConstantUtil.sharedDataDirSimple + "/ic.png";
//        CLog.d("imag: " + info.image_url);
//        oks.setImagePath(imagePath);
        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(entity.shoreUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("测试Comment");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("花生语音包");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(info.url);

        oks.setCallback(callback);
        //启动分享
        oks.show(context);
    }

    public static class ShareInfo {
        private String fileName;
        private String authorName;
        private String music_url;
        private String image_url;
        private String title_url;
        private String url;
        private int duration;

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public void setMusic_url(String music_url) {
            this.music_url = music_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public void setTitle_url(String title_url) {
            this.title_url = title_url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }
    }

}
