package com.ime.music.net.parse;

import android.util.Log;

import com.ime.music.CLog;
import com.ime.music.info.AudioInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryParser implements Parser {

    @Override
    public boolean Parse(String response, ArrayList<Map<String, Object>> result) {
        try {
            CLog.d(response);
            JSONObject jsonObject = new JSONObject(response);
            int status = jsonObject.getInt("status");

            if (200 == status) {
                JSONObject joData = jsonObject.getJSONObject("data");
                result.add(getInfos("1", joData));
                result.add(getInfos("2", joData));
                result.add(getInfos("3", joData));
                result.add(getInfos("4", joData));
                result.add(getInfos("5", joData));
                result.add(getInfos("6", joData));
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("clog_d fail", ": Tip info failed");
            return false;
        }
    }

    private Map<String, Object> getInfos(String tag, JSONObject joData) {

        Map<String, Object> map = new HashMap<>();
        JSONArray joData2 = null;
        try {
            joData2 = joData.getJSONArray(tag);
            List<AudioInfo> infos = new ArrayList<>();
            for (int j = 0; j < joData2.length(); ++j) {
                JSONObject data = joData2.getJSONObject(j);
                String fileHash = data.getString("hash");
                String id = data.getString("id");
                String audioName = data.getString("audioname");
                String source = data.getString("source");
                int duration = data.getInt("duration");
                AudioInfo info = new AudioInfo();
                info.setAudioName(audioName);
                info.setDuration(duration);
                info.setFileHash(fileHash);
                info.setId(id);
                info.setSource(source);
                infos.add(info);
            }
            map.put(tag, infos);
            return map;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
