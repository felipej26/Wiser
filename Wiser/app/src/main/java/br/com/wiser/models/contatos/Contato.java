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
    private long usuario;

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
}
