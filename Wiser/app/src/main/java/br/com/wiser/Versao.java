package br.com.wiser;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jefferson on 07/09/2017.
 */

public class Versao {
    @SerializedName("min_versao")
    private String minVersao;

    public String getMinVersao() {
        return minVersao;
    }

    public void setMinVersao(String minVersao) {
        this.minVersao = minVersao;
    }
}
