package br.com.wiser.features.discussao;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by Jefferson on 17/05/2016.
 */
public class Discussao implements Serializable {

    @SerializedName("id")
    private long id;

    @SerializedName("usuario")
    private long usuario;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("descricao")
    private String descricao;

    @SerializedName("data")
    private Date data;

    @SerializedName("discussao_ativa")
    private boolean isAtiva;

    @SerializedName("respostas")
    private LinkedList<Resposta> listaRespostas = new LinkedList<Resposta>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUsuario() {
        return usuario;
    }

    public void setUsuario(long usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public boolean isAtiva() {
        return isAtiva;
    }

    public void setAtiva(boolean ativa) {
        this.isAtiva = ativa;
    }

    public LinkedList<Resposta> getListaRespostas() {
        return listaRespostas;
    }

    public void setListaRespostas(LinkedList<Resposta> listaRespostas) {
        this.listaRespostas = listaRespostas;
    }
}