package br.com.wiser.utils;

import com.google.gson.annotations.SerializedName;

public class ComboBoxItem {

    private int id;
    private String descricao;

    public ComboBoxItem() {

    }

    public ComboBoxItem(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

