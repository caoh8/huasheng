package com.ime.music.net.parse;

import java.util.ArrayList;
import java.util.Map;

public interface Parser {
    public boolean Parse(String response, ArrayList<Map<String, Object>> result);
}
