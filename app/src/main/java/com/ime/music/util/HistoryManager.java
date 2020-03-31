package com.ime.music.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ime.music.R;
import com.ime.music.info.AudioHistoryInfo;
import com.ime.music.info.AudioInfo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class HistoryManager {
    private Context context;
    final private static int AUDIO_HISTORY_MAX_COUNT = 30;

    public HistoryManager(Context context) {
        this.context = context;
    }

    static public void addAudioHistory(AudioInfo info, Context context) {
        AudioHistoryInfo historyInfo = new AudioHistoryInfo();
        historyInfo.setAudioName(info.getAudioName());
        historyInfo.setId(info.getId());
        historyInfo.setFileHash(info.getFileHash());
        historyInfo.setUrl(info.getUrl());
        historyInfo.setSource(info.getSource());
        HistoryManager.addAudioHistory(historyInfo, context);
    }

    static private void addAudioHistory(AudioHistoryInfo inof, Context context) {
        ArrayList<AudioHistoryInfo> audioHistory = getAudioHistoryFromFile(
                context.getString(R.string.audio_history_file), context);
        if (audioHistory == null) audioHistory = new ArrayList<>();
        //找到一致的id就移除
        for (int i=0; i<audioHistory.size(); ++i) {
            String id = audioHistory.get(i).getId();
            if(id.equals(inof.getId())) {
                audioHistory.remove(i);
                break;
            }
        }
        //将新的加入
        audioHistory.add(0, inof);

        //移除超高上线的对象
        if (audioHistory.size() > AUDIO_HISTORY_MAX_COUNT) {
            int count = audioHistory.size() - AUDIO_HISTORY_MAX_COUNT;
            for (int i=0; i < count; ++i) {
                audioHistory.remove(AUDIO_HISTORY_MAX_COUNT);
            }
        }

        //将变化结果写入文件
        try {
            FileOutputStream fos = context.openFileOutput(
                    context.getString(R.string.audio_history_file), MODE_PRIVATE);//获得FileOutputStream
            //将要写入的字符串转换为byte数组
            Gson gson = new Gson();
            String historyJson = gson.toJson(audioHistory);
            byte[]  bytes = historyJson.getBytes();
            fos.write(bytes);//将byte数组写入文件
            fos.close();//关闭文件输出流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public ArrayList<AudioHistoryInfo> getAudioHistoryFromFile(String fileName, Context context) {
        ArrayList<AudioHistoryInfo> ret = null;
        try {
            FileInputStream inStream = context.openFileInput(fileName);
            byte[] buffer = new byte[1024];
            int hasRead = 0;
            StringBuilder sb = new StringBuilder();
            while ((hasRead = inStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, hasRead));
            }

            inStream.close();
            String jsonStr = sb.toString();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<AudioHistoryInfo>>(){}.getType();
            ret = gson.fromJson(jsonStr, type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
