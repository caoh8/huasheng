package com.ime.music.net.search;

import android.content.Context;

import com.ime.music.CLog;
import com.ime.music.net.parse.Parser;
import com.ime.music.net.parse.SongsParser;
import com.ime.music.net.shower.Shower;
import com.ime.music.net.shower.SongListShower;
import com.ime.music.util.ConstantUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Searcher {
    private Context context;
    private ParseResultHolder holder;
    static private Thread threadS = null;//这里会有BUG，由于确定什么时候被执行
    static private volatile boolean isDiscard = false;

    public enum STATUS {
        ERROR,
        INFO
    }

    public Searcher(Context context, ParseResultHolder holder) {
        this.context = context;
        this.holder = holder;
    }
    //碰到莫名其妙的问题，threadS没有
    public static void initSearch() {
        threadS = null;
        isDiscard = false;
    }

    synchronized static public void search_new(final String url, final Parser parser, final Shower shower) {
        search_new(url, parser, shower, 1);
    }

    //创建新的子线程进行搜索
    synchronized static public void search_new(final String url, final Parser parser, final Shower shower, int timeout) {
        if(null != shower) {
            shower.init();
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        final Call call = okHttpClient.newCall(request);
        //同步请求网络execute()
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CLog.d("search running");
                    Response response = call.execute();
                    if(response.isSuccessful()) {
                        CLog.d("isSuccessful");
                        String responseData = response.body().string();
                        CLog.d(responseData);
                        if (parser != null) {
                            ArrayList<Map<String, Object>> result = new ArrayList<>();
                            boolean isOk = parser.Parse(responseData, result);
                            if(!isOk) {
                                showTip(shower, "解析失败");
                            }
                            if(shower!=null) {
                                shower.Show(result, isOk);
                            }
                        }
                    } else {//歌曲查询失败
                        CLog.d("failed search song");
//                            showTip(shower, "查询失败");
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    CLog.d("execute error search song");
                    e.printStackTrace();
                    if(null != shower) {
                        shower.error("网络错误", Shower.Error.net);
                    }
                }
            }
        }).start();
    }

    /*  1-初始化shower
        2-请求
        3-结果解析
        4-结果显示
    */
    synchronized static public void search(final String url, final Parser parser, final Shower shower) {
        if(null != shower) {
            shower.init();
        }

        CLog.d("search url: " + url);
//        threadS = null;
        if (threadS != null) {
            if (threadS.isAlive()) {
                CLog.d("threadS isDiscard: " + isDiscard);
                isDiscard = true;
                return;
            }  else {
                isDiscard = false;
                threadS = null;
            }
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        final Call call = okHttpClient.newCall(request);
        //同步请求网络execute()
        if (null == threadS) {
            threadS = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        isDiscard = false;
                        CLog.d("search running");
                        Response response = call.execute();
                        if(response.isSuccessful()){
                            CLog.d("isSuccessful");
                            String responseData = response.body().string();
                            CLog.d(responseData);
                            if (parser != null) {
                                ArrayList<Map<String, Object>> result = new ArrayList<>();
                                boolean isOk = parser.Parse(responseData, result);
                                if(!isOk) {
                                    showTip(shower, "解析失败");
                                }
                                CLog.d("isDiscard: " + isDiscard);
                                if(shower!=null && !isDiscard) {
                                    shower.Show(result, isOk);
                                }
                            }
                        } else {//歌曲查询失败
                        CLog.d("failed search song");
//                            showTip(shower, "查询失败");
                            throw new IOException("Unexpected code " + response);
                        }
                    } catch (IOException e) {
                    CLog.d("execute error search song");
                        e.printStackTrace();
                        if(null != shower) {
                            shower.error("网络错误", Shower.Error.net);
                        }
                    }
                }
            });
        }
        threadS.start();
    }

    static private void showTip(Shower shower, String msg) {
        if(null != shower) {
            shower.info(msg);
        }
    }

    public void searchSongs(String keyword) {//文件名、Hash
        CLog.d("searchSongs");
        String url = ConstantUtil.http_search_song + "keyword=" + keyword;
        CLog.d(url);
        search_new(url, new SongsParser(), new SongListShower(holder.getSongListView(), true));
    }

    public void searchSongsWithPage(String keyword, int page) {
        CLog.d("page: " + page);
        String url = ConstantUtil.http_search_song + "keyword=" + keyword + "&page=" + page;
        CLog.d(url);
        search_new(url, new SongsParser(), new SongListShower(holder.getSongListView(), false));
    }

    public void searchSongSources(String fileHash) {
//        String url = mContext.getString(R.string.http_song_source) + "hash=" + fileHash;
//        search(url, new SongSourceParser());
    }

    public void searchTip(String keyword) {
//        if (keyword == mPreTip) return; else mPreTip = keyword;
        CLog.d("searchTip");
        String url = ConstantUtil.http_song_tip + "keyword=" + keyword;
        CLog.d(url);
//        search(url, new TipParser(mView));
    }
}