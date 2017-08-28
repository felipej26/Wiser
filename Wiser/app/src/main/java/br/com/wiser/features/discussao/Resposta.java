package br.com.wiser.features.discussao;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Jefferson on 20/05/2016.
 */
public class Resposta implements Serializable {

    private long id;
    private long usuario;
    private Date data;
    private String resposta;

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

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }
}
