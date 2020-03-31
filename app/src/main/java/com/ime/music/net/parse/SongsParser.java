package com.ime.music.net.parse;

import android.util.Log;

import com.ime.music.CLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SongsParser implements Parser {

    @Override
    public synchronized boolean Parse(String response, ArrayList<Map<String, Object>> result) {
        try{
            CLog.d("Parse");
            CLog.d(response);
            JSONObject jsonObject = new JSONObject(response);
            String errors = jsonObject.getString("errors");
//            Log.d("clog_d", msg);
            if (errors.equals("null")) {
                JSONObject joData = jsonObject.getJSONObject("data");
                int total = joData.getInt("total");
                if (total == 0) return true;
                JSONArray jsonArray = joData.getJSONArray("info");
                Log.d("clog_d", "JSONArray");

                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject jo=jsonArray.getJSONObject(i);
                    String fileName=jo.getString("filename");
                    String fileHash = jo.getString("hash");
                    String songName=jo.getString("songname");
                    String singerName = jo.getString("singername");
                    String albumID=jo.getString("album_id");
                    String albumAudioID = jo.getString("album_audio_id");
                    String extName=jo.getString("extname");
                    String segment = "";
                    try {
                        segment = jo.getString("segment");
                    } catch (JSONException e) {
                        CLog.i("no segment");
                        segment = "歌词跑了";
                    }

//                    String feeType = jo.getString("feetype");
                    Map<String, Object> map = new HashMap<>();
                    map.put("FileName", fileName);
                    map.put("FileHash", fileHash);
                    map.put("SingerName", singerName);
                    map.put("AlbumID", albumID);
                    map.put("AlbumAudioID", albumAudioID);
                    map.put("SongName", songName);
                    map.put("ExtName", extName);
                    map.put("Segment", segment);
//                    map.put("FeeType", feeType);
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

