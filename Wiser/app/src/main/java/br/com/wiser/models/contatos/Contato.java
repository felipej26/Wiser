package br.com.wiser.models.contatos;

import com.google.gson.annotations.SerializedName;

import br.com.wiser.models.usuario.Usuario;

/**
 * Created by Jefferson on 27/01/2017.
 */
public class Contato {
    @SerializedName("id")
    private long id;

    @SerializedName("contato")
    private Usuario usuario;

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
}
