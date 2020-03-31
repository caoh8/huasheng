package com.ime.music.net.parse;

import android.util.Log;

import com.ime.music.CLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TipParser4 implements Parser {

    @Override
    public boolean Parse(String response, ArrayList<Map<String, Object>> result) {
        try {
            CLog.d(response);
            JSONObject jsonObject = new JSONObject(response);
            int status = jsonObject.getInt("status");

            if(200 == status) {
                JSONArray joData = jsonObject.getJSONArray("data");
                for (int i=0; i<joData.length(); ++i) {
                    JSONObject data = joData.getJSONObject(i);
                    String fileHash = data.getString("hash");
                    String id = data.getString("id");
                    String audioName = data.getString("audioname");
                    String source = data.getString("source");
                    Map<String, Object> map = new HashMap<>();
                    map.put("FileHash", fileHash);
                    map.put("Source", source);
                    map.put("ID", id);
                    map.put("AudioName", audioName);
                    int duration = data.getInt("duration");
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
