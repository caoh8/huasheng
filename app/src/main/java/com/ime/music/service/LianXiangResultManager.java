package com.ime.music.service;

import com.ime.music.CLog;
import com.ime.music.R;
import com.ime.music.info.AudioInfo;
import com.ime.music.net.shower.Shower;
import com.ime.music.util.ConstantUtil;
import com.ime.music.view.FloatWindowView;

import java.util.ArrayList;
import java.util.Map;

public class LianXiangResultManager implements Shower {
    static private LianXiangResultManager self;

    public boolean isValid() {
        return isValid;
    }

    public void failed() {
        isValid = false;

        ConstantUtil.getFloatManagerMusic().changBall(ConstantUtil.getAppContext().getDrawable(R.drawable.qiu_std));
    }

    private boolean isValid = false;

    public ArrayList<AudioInfo> getData() {
        return data;
    }

    ArrayList<AudioInfo> data = new ArrayList<>();

    private LianXiangResultManager() {}

    static public LianXiangResultManager get() {
        if (self == null) {
            self = new LianXiangResultManager();
        }
        return self;
    }

    @Override
    public void init() {
        isValid = false;
    }

    @Override
    public void info(String msg) {

    }

    @Override
    public void error(String msg, Error e) {
        failed();
    }

    @Override
    public void Show(ArrayList<Map<String, Object>> result, boolean isOk) {
        isValid = isOk;
        data.clear();
        if (!result.isEmpty()) {
            ConstantUtil.getFloatManagerMusic().changBall(ConstantUtil.getAppContext().getDrawable(R.drawable.qiu_lianxiang));
            for (int i = 0; i < result.size(); ++i) {
                AudioInfo info = new AudioInfo();
                info.setDuration((int) result.get(i).get("Duration"));
                info.setAudioName((String) result.get(i).get("AudioName"));
                info.setFileHash((String) result.get(i).get("FileHash"));
                info.setId((String) result.get(i).get("ID"));
                info.setSource((String) result.get(i).get("Source"));
                data.add(info);
            }
//            adapter.setData(dataHot);
            ((FloatWindowView)ConstantUtil.getFloatManagerMusic().getWindowView()).setDataAndFlush(data);
        } else {
            CLog.d("no audios list");
            failed();
        }
    }
}
