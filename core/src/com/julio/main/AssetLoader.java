package com.julio.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class AssetLoader {
    static Preferences preferences = Gdx.app.getPreferences("game_preferences");
    public static void load() {
        if (!preferences.contains("minMins")) {preferences.putInteger("minMins", 10);}
        if (!preferences.contains("minTens")) {preferences.putInteger("minTens", 10);}
        if (!preferences.contains("minOnes")) {preferences.putInteger("minOnes", 10);}
        if (!preferences.contains("minTime")) {preferences.putInteger("minTime", 101);}
    }
    public static String getBestTime() {
        if (preferences.getInteger("minMins") == 10) {
            return "0:00:00";
        }
        return preferences.getInteger("minMins") + ":" + preferences.getInteger("minTens") + preferences.getInteger("minOnes") + ":" + preferences.getInteger("minTime");
    }
}
