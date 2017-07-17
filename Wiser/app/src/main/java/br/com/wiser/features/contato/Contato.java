package br.com.wiser.features.contato;

import com.google.gson.annotations.SerializedName;

import br.com.wiser.features.usuario.Usuario;

/**
 * Created by Jefferson on 27/01/2017.
 */
public class Contato {
    private long id;
    @SerializedName("contato")
    private long idContato;
    private Usuario destinatario;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdContato() {
        return idContato;
    }

    public void setIdContato(long idContato) {
        this.idContato = idContato;
    }

    public Usuario getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Usuario destinatario) {
        this.destinatario = destinatario;
    }
}
