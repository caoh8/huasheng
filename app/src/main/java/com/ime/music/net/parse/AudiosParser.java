package com.ime.music.net.parse;

import com.ime.music.CLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AudiosParser implements Parser {

    @Override
    public synchronized boolean Parse(String response, ArrayList<Map<String, Object>> result) {
        try{
            CLog.d(response);
            JSONObject jsonObject = new JSONObject(response);
            String errors = jsonObject.getString("errors");
            if (errors.equals("null")) {
                JSONObject joData = jsonObject.getJSONObject("data");
                if (joData.getInt("total") == 0) return true;
                JSONArray jsonArray = joData.getJSONArray("info");

                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject jo=jsonArray.getJSONObject(i);
                    String audioName=jo.getString("audioname");
                    String fileHash = jo.getString("hash");
                    String id = jo.getString("id");
                    String source = jo.getString("source");
                    Map<String, Object> map = new HashMap<>();
                    map.put("AudioName", audioName);
                    map.put("FileHash", fileHash);
                    map.put("ID", id);
                    map.put("Source", source);
                    int duration = jo.getInt("duration");
                    map.put("Duration", duration);
                    result.add(map);
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}

