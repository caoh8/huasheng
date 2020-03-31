package com.ime.music.util;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ime.music.R;

import com.ime.music.info.AudioInfo;
import com.ime.music.info.MineAudioInfo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MineManager {
//    private Context context;
    final private static int AUDIO_MINE_MAX_COUNT = 100;

//    public MineManager(Context context) {
//        this.context = context;
//    }

    static public void addMineAudio(AudioInfo info) {
//        AudioInfo mineAudioInfo = new AudioInfo();
//        mineAudioInfo.setAudioName(info.getAudioName());
//        mineAudioInfo.setId(info.getId());
//        mineAudioInfo.setFileHash(info.getFileHash());
//        mineAudioInfo.setUrl(info.getUrl());
//        mineAudioInfo.setSource(info.getSource());
//        mineAudioInfo.setMine(true);
        info.setMine(true);
        MineManager.addAudio(info);
    }

    static public void removeMineAudio(AudioInfo info) {
        info.setMine(false);
        MineManager.removeAudio(info);
    }

    // 判断收藏中是否有对应音频
    static public boolean isMine(AudioInfo info) {
        ArrayList<AudioInfo> audios = getMineAudioFromFile();

        if (audios == null) return false;

        for (AudioInfo mineAudioInfo : audios) {
            if (mineAudioInfo.getId().equals(info.getId())) {
                return true;
            }
        }
        return false;
    }

    static private void removeAudio(AudioInfo info) {
        ArrayList<AudioInfo> audios = getMineAudioFromFile();

        if (audios == null) return;

        for (AudioInfo mineAudioInfo : audios) {
            if (mineAudioInfo.getId().equals(info.getId())) {
                audios.remove(mineAudioInfo);
                mineAudioInfo.setMine(false);
                break;
            }
        }

        writeToFile(audios);
    }

    static private void addAudio(AudioInfo info) {
        ArrayList<AudioInfo> audios = getMineAudioFromFile();
        if (audios == null) {
            audios = new ArrayList<>();
            Toast.makeText(ConstantUtil.getAppContext(), "可在悬浮窗-收藏中使用", Toast.LENGTH_SHORT).show();
        }
        //找到一致的id就移除
        for (int i=0; i<audios.size(); ++i) {
            String id = audios.get(i).getId();
            if(id.equals(info.getId())) {
                audios.remove(i);
                break;
            }
        }
        //将新的加入
        audios.add(0, info);

        //移除超高上线的对象
        if (audios.size() > AUDIO_MINE_MAX_COUNT) {
            int count = audios.size() - AUDIO_MINE_MAX_COUNT;
            for (int i=0; i < count; ++i) {
                audios.remove(AUDIO_MINE_MAX_COUNT);
            }
        }

        //将变化结果写入文件
        writeToFile(audios);
    }

    static private void writeToFile(ArrayList<AudioInfo> audios) {
        try {
            FileOutputStream fos = ConstantUtil.getAppContext().openFileOutput(
                    ConstantUtil.getAppContext().getString(R.string.audio_mine_file), MODE_PRIVATE);//获得FileOutputStream
            //将要写入的字符串转换为byte数组
            Gson gson = new Gson();
            String mineJson = gson.toJson(audios);
            byte[]  bytes = mineJson.getBytes();
            fos.write(bytes);//将byte数组写入文件
            fos.close();//关闭文件输出流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public ArrayList<AudioInfo> getMineAudioFromFile() {
        ArrayList<AudioInfo> ret = null;
        try {
            FileInputStream inStream = ConstantUtil.getAppContext().openFileInput(
                    ConstantUtil.getAppContext().getString(R.string.audio_mine_file));
            byte[] buffer = new byte[1024];
            int hasRead = 0;
            StringBuilder sb = new StringBuilder();
            while ((hasRead = inStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, hasRead));
            }

            inStream.close();
            String jsonStr = sb.toString();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<AudioInfo>>(){}.getType();
            ret = gson.fromJson(jsonStr, type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
