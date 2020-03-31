package com.ime.music.net.shower;

import com.ime.music.CLog;

import java.util.ArrayList;
import java.util.Map;

public class SongSourceShower implements Shower {
    public enum TYPE {
        SHARE,
        PLAY,
    };

    private TYPE type = TYPE.SHARE;
    private String musicUrl;

    public SongSourceShower(TYPE type) {
        this.type = type;
    }

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
        if(result.size() != 0) {
            musicUrl = (String) result.get(0).get("MusicUrl");
        } else {
            return;
        }
        switch (type) {
            case PLAY:
                CLog.d("PLAY MUISC: " + musicUrl);
                break;
            case SHARE:
                CLog.d("SHARE MUISC: " + musicUrl);
                break;
        }
    }
}
