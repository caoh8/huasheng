package com.ime.music.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ime.music.CLog;
import com.ime.music.info.PlayInfo;
import com.ime.music.net.parse.Parser;
import com.ime.music.net.search.Searcher;
import com.ime.music.net.shower.Shower;
import com.ime.music.play.MusicPlayer;
import com.ime.music.play.PlayeItemHolder;
import com.ime.music.share.Share;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public abstract class PlayedAdapter extends BaseAdapter {
    protected int playingItem = -1;
    protected Parser sourceParser;
    protected Context context;

    abstract View getPlayedItem(PlayeItemHolder holder, int position);

    abstract void init();

    abstract void play(PlayInfo info);

    abstract void share(PlayInfo info, Share.ShareInfo sinfo);

    abstract PlayInfo getDataIndex(int i);

    abstract String getPlayURL(PlayInfo info);

    abstract String getShareURL(PlayInfo info);

    abstract void post2Service(int behavior, PlayInfo info);

    protected PlayeItemHolder getHolder() {
        return new PlayeItemHolder();
    }

    protected void display(PlayeItemHolder holder, PlayInfo info) {
    }

    protected void playItemClicked(PlayeItemHolder holder) {

    }

    protected void reused(PlayeItemHolder holder, int position) {

    }

    protected PlayedAdapter(Context context) {
        this.context = context;
        init();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PlayeItemHolder holder;
        if (null == convertView) {
            holder = getHolder();
            convertView = getPlayedItem(holder, position);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (PlayeItemHolder) convertView.getTag();//取出ViewHolder对象
            reused(holder, position);
            //复用Holder 播放进度条设置
            if (position != playingItem && holder.num == playingItem) {//播放VIEW要被复用
                //停止进度条
                MusicPlayer.getInstance().clearUI();
            }
            if (playingItem == position) {
                //恢复进度条
                MusicPlayer.getInstance().registerUI(holder.display_name);
            }
        }
        //显示设置
        PlayInfo info = getDataIndex(position);
        holder.display(info);
        holder.num = position;
        display(holder, info);

        //播放歌曲
        holder.display_name.setPlayClickListener(() -> {
            playItemClicked(holder);
            String url = info.getPlayUrl();
            if (null == url || url.equals("")) {
                Searcher.search_new(getPlayURL(info),
                        sourceParser, new Shower() {
                            @Override
                            public void init() {

                            }

                            @Override
                            public void info(String msg) {
//                                    toastShort(mContext, msg);
                            }

                            @Override
                            public void error(String msg, Error e) {

                            }

                            @Override
                            public void Show(ArrayList<Map<String, Object>> result, boolean isOk) {
                                if (isOk) {
//                                        toastShort(mContext, "准备播放");
                                    String musicUrl = (String) result.get(0).get("PlayUrl");
                                    MusicPlayer.getInstance().play(musicUrl);
                                    MusicPlayer.getInstance().registerUI(holder.display_name);
                                    info.setPlayUrl(musicUrl);
                                    playingItem = position;
                                    info.setPlayed(true);
                                    play(info);
                                } else {
                                    CLog.e("播放失败");
//                                        toastShort(mContext, "没有资源");
                                }
                            }
                        });
            } else {
                MusicPlayer.getInstance().play(url);
                info.setPlayed(true);
                MusicPlayer.getInstance().registerUI(holder.display_name);
                playingItem = position;
                play(info);
                post2Service(1, info);
            }
        });

        //分享歌曲
        if (null != holder.share_btn)
            holder.share_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                share(info);
                    String url = info.getPlayUrl();
                    if (null == url || url.equals("")) {
                        Searcher.search_new(getShareURL(info),
                                sourceParser, new Shower() {
                                    @Override
                                    public void init() {

                                    }

                                    @Override
                                    public void info(String msg) {

                                    }

                                    @Override
                                    public void error(String msg, Error e) {

                                    }

                                    @Override
                                    public void Show(ArrayList<Map<String, Object>> result, boolean isOk) {
                                        if (isOk) {
                                            String musicUrl = (String) result.get(0).get("PlayUrl");
                                            Share.ShareInfo sinfo = new Share.ShareInfo();
                                            sinfo.setMusic_url(musicUrl);
                                            share(info, sinfo);
                                            sinfo.setTitle_url(musicUrl);
                                            Share.showShare(context, null, sinfo);
//                            Share.showShare(mContext, QQ.NAME, info);
                                        } else {
//                            toastShort(mContext,"没有资源");
                                            CLog.e("分享失败");
                                        }
                                    }
                                });
                    } else {
                        Share.ShareInfo sinfo = new Share.ShareInfo();
                        sinfo.setMusic_url(url);
                        share(info, sinfo);
                        sinfo.setTitle_url(url);
                        Share.showShare(context, null, sinfo);
                        post2Service(2, info);
                    }
                }
            });
        return convertView;
    }

}
