package br.com.wiser.business.chat.assunto;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Jefferson on 30/10/2016.
 */
public class Assunto {

    private String titulo;
    private HashSet<String> categorias = new HashSet<>();
    private ArrayList<String> itens = new ArrayList<>();

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public HashSet<String> getCategorias() {
        return categorias;
    }

    public ArrayList<String> getItens() {
        return itens;
    }
}
