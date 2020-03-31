package com.ime.music.play;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.TextView;

import com.ime.music.CLog;
import com.ime.music.util.ConstantUtil;
import com.ime.music.util.Tools;
import com.ime.music.view.PlayItem;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayer {
    public void setHold(boolean hold) {
        if (status == STATUS.HOLD) return;
        isHold = hold;
    }


    private boolean isHold = false;

    public void setHoldShower(TextView holdShower) {
        if (status == STATUS.HOLD) return;
        this.holdShower = holdShower;
    }

    private TextView holdShower;

    private static class SingletonHolder {
        private static MusicPlayer instance = new MusicPlayer();
    }

    public static MusicPlayer getInstance() {
        return SingletonHolder.instance;
    }

    private MusicPlayer() {
    }

    private MediaPlayer player;
    private volatile STATUS status = STATUS.INIT;
    private String currentUrl = "";
    private PlayItem ui = null;
    private Changed changed;
    final PlayItem.OnSeekListener seekListener = new PlayItem.OnSeekListener() {
        @Override
        public void onMove() {
            pause();
        }

        @Override
        public void onSeek(int position) {
            seek(position);
        }
    };

    public interface Changed {
        void onStatusChanged(STATUS status);
    }

    public void setChanged(Changed changed) {
        this.changed = changed;
    }

    private void setStatus(STATUS status) {
        this.status = status;
        if (changed != null) changed.onStatusChanged(status);
    }

    public enum STATUS {
        HOLD,
        PLAYING,
        PAUSE,
        COMPLETION,
        PREPARING,
        PREPARED,
        INIT,
        NULL
    }

    private void initPlayer() {
        if (player != null) {
            player.reset();
            player.release();
            player = null;
        }
        setStatus(STATUS.INIT);
        player = new MediaPlayer();

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                CLog.d("player error: " + i1 + " " + i);
//                    stop();
                return true;
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                clearUI();
                setStatus(STATUS.COMPLETION);
            }
        });
    }

    private void delayShow() {
        if (holdShower == null) return;
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (status != STATUS.HOLD) {
                    timer.cancel();
                    holdShower.post(new Runnable() {
                        @Override
                        public void run() {
                            if (holdShower == null) return;
                            holdShower.setText("");
                            holdShower.setVisibility(View.GONE);
                            holdShower = null;
                        }
                    });
                    return;
                }
                holdShower.post(new Runnable() {
                    @Override
                    public void run() {
                        if (holdShower == null) return;
                        holdShower.setVisibility(View.VISIBLE);
                        holdShower.setText(String.valueOf(ConstantUtil.PlayDelayCount--));
                        if (ConstantUtil.PlayDelayCount < 0) {
                            timer.cancel();
                            holdShower.setText("");
                            holdShower.setVisibility(View.GONE);
                            holdShower = null;
                        }
                    }
                });
            }
        };
        ConstantUtil.PlayDelayCount = ConstantUtil.PlayDelay;
        timer.schedule(task, 0, 1000);
    }

    // 延时time后播放
    private void hold(int time) {
        if (Tools.isHeadSetON()) {
//            isHold = false;
            CLog.e("麻烦拔掉耳机");
            ConstantUtil.getFloatManagerMusic().showHeadSetAlert();
//            return;
        }

        if (Tools.isMusicVolumeZero()) {
            ConstantUtil.getFloatManagerMusic().showUpVolumeTip();
        }

        setStatus(STATUS.HOLD);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (status != STATUS.HOLD) return;
                startPlay();
                isHold = false;
            }
        }, time * 1000);

        delayShow();
    }

    private void playOrPause() {
        switch (status) {
            case PLAYING:
                pause();
                break;
            case PAUSE:
                play();
                break;
            case COMPLETION:
                if (isHold) {
                    hold(ConstantUtil.PlayDelay);
                    return;
                } else {
                    play();
                }
                break;
            case NULL:
//                play(music);
                break;
            case INIT:
                break;
//            case PREPARED:
//                hold(ConstantUtil.PlayDelay);
//                break;
            default:
                break;
        }
    }

    private void scrollSeekBar() {
        if (null != ui && !ui.isScrolling()) {
            uiStart(ui);
        }
    }

    private void play() {
        if (player == null) return;
        startPlay();
    }

    private void pause() {
        if (player == null || status == STATUS.PREPARING) return;
        player.pause();
        setStatus(STATUS.PAUSE);
    }

    private void seek(int position) {
        if (player == null || status != STATUS.PAUSE) return;
        CLog.d("seek: " + position);
        player.seekTo(position);
        player.start();
        setStatus(STATUS.PLAYING);

        scrollSeekBar();
    }

    private void newPlay(String musicUrl) {
        initPlayer();
        player.reset();
        CLog.d("player reset");
        try {
            CLog.d("player setDataSource: " + musicUrl);
            player.setDataSource(musicUrl);
        } catch (IOException e) {
            e.printStackTrace();
            CLog.d("音乐资源设置错误");
            return;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            CLog.d("音乐资源设置错误 1");
            return;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            CLog.d("音乐资源设置错误 2");
            return;
        }
        CLog.d("player prepareAsync");
//        status = STATUS.PREPARING;
        setStatus(STATUS.PREPARING);
        player.prepareAsync();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (status != STATUS.COMPLETION) {
                    setStatus(STATUS.PREPARED);
                    if (isHold) {
                        hold(ConstantUtil.PlayDelay);
                        return;
                    }
                    startPlay();
                }
            }
        });
    }

    private void startPlay() {
//        if (status != STATUS.PREPARED) return;
        player.start();
        setStatus(STATUS.PLAYING);

        if (ui != null) {
            ui.setPlayerLength(player.getDuration());
        }

        scrollSeekBar();
    }

    public STATUS getStatus() {
        return status;
    }

    public void play(String musicUrl) {
        if (status == STATUS.HOLD) return;
        CLog.d("Play: " + currentUrl);
        if (!musicUrl.equals(currentUrl)) {
            newPlay(musicUrl);

            currentUrl = musicUrl;
            return;
        }
        //与正在播放一致
        playOrPause();
    }

    private void uiStop(PlayItem ui) {
        final PlayItem tmp = ui;
        tmp.post(new Runnable() {
            @Override
            public void run() {
                tmp.stop();
                tmp.playInit();
            }
        });
    }

    private void uiStart(PlayItem ui) {
        final PlayItem tmp = ui;
        tmp.post(new Runnable() {
            @Override
            public void run() {
                tmp.start();
            }
        });
    }

    public void stop() {//释放播放器
        if (player != null) {
            if (ui != null) {
                uiStop(ui);
                ui = null;
            }

            player.release();
            player = null;
            currentUrl = "";
//            status = STATUS.NULL;

            setStatus(STATUS.NULL);
        }
    }

    //播放位置
    public int getPosition() {
        if (status == STATUS.HOLD) return 0;
        if (player != null && (STATUS.PLAYING == status || STATUS.PAUSE == status)) {
            try {
                return player.getCurrentPosition();
            } catch (Exception e) {
                CLog.e("获取进度条异常");
                e.printStackTrace();
            }
        }
        return 0;
    }

    public void clearUI() {
        registerUI(null);
    }

    public PlayItem getUi() {
        return ui;
    }

    public void registerUI(PlayItem ui) {
        if (null == ui && this.ui != null) {
            uiStop(this.ui);
            this.ui.setOnSeekListener(null);
            this.ui = null;
//            if (holdShower != null)
//            holdShower.setVisibility(View.GONE);
            return;
        }
        if (status == STATUS.HOLD && null != this.ui) {
//            if (holdShower != null)
//            holdShower.setVisibility(View.VISIBLE);
            return;
        }
        if (this.ui == ui) return;

        if (this.ui != null) {
            uiStop(this.ui);
            this.ui.setOnSeekListener(null);
            this.ui = null;
        }

        if (ui != null) {
            this.ui = ui;
            ui.setOnSeekListener(seekListener);
            if (STATUS.PLAYING == status || STATUS.PAUSE == status)
                ui.setPlayerLength(player.getDuration());
            uiStart(ui);
        }
    }
}
