package br.com.wiser.features.assunto;

import java.io.Serializable;

/**
 * Created by Jefferson on 02/11/2016.
 */
public class Pagina implements Serializable {

    private String nome;
    private String categoria;
    private boolean isVerificada;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isVerificada() {
        return isVerificada;
    }

    public void setVerificada(boolean verificada) {
        isVerificada = verificada;
    }
}
