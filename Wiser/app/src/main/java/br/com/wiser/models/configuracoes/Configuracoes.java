package br.com.wiser.models.configuracoes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jefferson on 22/01/2017.
 */
public class Configuracoes {
    @SerializedName("id")
    private long id;

    @SerializedName("idioma")
    private int idioma;

    @SerializedName("fluencia")
    private String fluencia;

    @SerializedName("status")
    private String status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdioma() {
        return idioma;
    }

    public void setIdioma(int idioma) {
        this.idioma = idioma;
    }

    public String getFluencia() {
        return fluencia;
    }

    public void setFluencia(String fluencia) {
        this.fluencia = fluencia;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
