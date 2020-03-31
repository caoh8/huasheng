package com.ime.music.net.parse;

import android.util.Log;

import com.ime.music.CLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HotAudioParser implements Parser {

    @Override
    public boolean Parse(String response, ArrayList<Map<String, Object>> result) {
        try {
            CLog.d(response);
            JSONObject jsonObject = new JSONObject(response);
            int status = jsonObject.getInt("status");

            if (200 == status) {
                JSONObject jData = jsonObject.getJSONObject("data");
                JSONArray joData = jData.getJSONArray("info");
                for (int i = 0; i < joData.length(); ++i) {
                    JSONObject jo = joData.getJSONObject(i);
                    String audioName = jo.getString("audioname");
                    String fileHash = jo.getString("hash");
                    String source = jo.getString("source");
                    String id = jo.getString("id");
                    Map<String, Object> map = new HashMap<>();
                    map.put("AudioName", audioName);
                    map.put("FileHash", fileHash);
                    map.put("ID", id);
                    map.put("Source", source);
                    int duration = jo.getInt("duration");
                    map.put("Duration", duration);
                    result.add(map);
                }
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
}