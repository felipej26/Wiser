package br.com.wiser.features.assunto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Jefferson on 30/10/2016.
 */
public class Assunto {

    @SerializedName("titulos")
    private String titulo[];

    @SerializedName("subcategorias")
    private HashSet<String> categorias = new HashSet<>();

    @SerializedName("itens")
    private ArrayList<String> itens = new ArrayList<>();

    public String getTitulo() {
        return titulo[0];
    }

    public void setTitulo(String titulo) {
        this.titulo[0] = titulo;
    }

    public HashSet<String> getCategorias() {
        return categorias;
    }

    public ArrayList<String> getItens() {
        return itens;
    }
}
