package br.com.wiser;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jefferson on 07/09/2017.
 */

public class Versao {
    @SerializedName("min_versao")
    private String minVersaoApp;

    @SerializedName("min_versao_cache")
    private int minVersaoCache;

    public String getMinVersaoApp() {
        return minVersaoApp;
    }

    public void setMinVersaoApp(String minVersaoApp) {
        this.minVersaoApp = minVersaoApp;
    }

    public int getMinVersaoCache() {
        return minVersaoCache;
    }

    public void setMinVersaoCache(int minVersaoCache) {
        this.minVersaoCache = minVersaoCache;
    }
}
