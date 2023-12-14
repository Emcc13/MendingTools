package com.github.Emcc13.MendingTools.Util;

import com.github.Emcc13.MendingToolsMain;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Stream;

public class UpdateChecker {
    public static String getLatestReleaseTitle() throws Exception {
        MendingToolsMain main = MendingToolsMain.getInstance();
        String dev = main.getDescription().getAuthors().get(0);
        String urlString = "https://api.github.com/repos/" + dev + "/MC-Plugin-Releases/releases";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output;
        StringBuilder response = new StringBuilder();

        while ((output = br.readLine()) != null) {
            response.append(output);
        }
        conn.disconnect();

        output = response.toString();
        output = output.substring(2, output.length() - 2);
        return Stream.of(output.split("},\\{"))
                .map(jsonObjectString -> JsonParser.parseString("{" + jsonObjectString + "}").getAsJsonObject())
                .filter(jsonObject -> jsonObject
                        .get("tag_name")
                        .getAsString()
                        .equals(main.getDescription().getName())
                )
                .findFirst()
                .orElse(new JsonObject())
                .get("name")
                .getAsString()
                ;
    }
}
