package br.com.wiser.features.discussao;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

import br.com.wiser.features.usuario.Usuario;

/**
 * Created by Jefferson on 17/05/2016.
 */
public class Discussao implements Serializable {

    private long id;
    private Usuario usuario;
    private String titulo;
    private String descricao;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
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