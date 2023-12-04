package com.notesapp.src.utils;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static Map<String, String> queryParser(String query) {
        if (query == null || !query.contains("="))
            return null;

        String[] pairs = query.split("&");
        Map<String, String> queryMap = new HashMap<>();

        for (String pair : pairs) {
            String[] subPairs = pair.split("=");
            queryMap.put(subPairs[0], subPairs[1]);
        }

        return queryMap;
    }


}
