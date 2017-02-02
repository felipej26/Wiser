package br.com.wiser.models.mensagens;

import java.io.Serializable;
import java.util.Date;

import br.com.wiser.Sistema;

/**
 * Created by Jefferson on 10/08/2016.
 */
public class Mensagem implements Serializable {
    private long id;
    private long usuario;
    private Date data;
    private String mensagem;
    private boolean lida;

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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }

    public boolean isDestinatario() {
        return usuario != Sistema.getUsuario().getUserID();
    }
}
