package br.com.wiser.business.chat.assunto;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Jefferson on 30/10/2016.
 */
public class Assunto {

    private String titulo;
    private String assunto;
    private HashSet<String> categorias;
    private ArrayList<String> itens;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public HashSet<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(HashSet<String> categorias) {
        this.categorias = categorias;
    }

    public ArrayList<String> getItens() {
        return itens;
    }

    public void setItens(ArrayList<String> itens) {
        this.itens = itens;
    }
}
