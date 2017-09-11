package br.com.wiser.features.mensagem;

import java.io.Serializable;
import java.util.Date;

import br.com.wiser.Sistema;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jefferson on 10/08/2016.
 */
public class Mensagem extends RealmObject implements Serializable {

    /*
    public enum Estado {
        ENVIADO (0),
        ENVIANDO (1),
        ERRO (2);

        private int estado;

        Estado(int estado) {
            this.estado = estado;
        }

        public int getEstado() {
            return estado;
        }
    }
    */

    @PrimaryKey
    private long id;
    private long conversa;
    private long usuario;
    //private Estado estado = Estado.ENVIADO;
    private Date data;
    private String mensagem;
    private boolean lida;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getConversa() {
        return conversa;
    }

    public void setConversa(long conversa) {
        this.conversa = conversa;
    }

    public long getUsuario() {
        return usuario;
    }

    public void setUsuario(long usuario) {
        this.usuario = usuario;
    }

    /*
    public Estado getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = Mensagem.Estado.values()[estado];
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    */

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
        return usuario != Sistema.getUsuario().getId();
    }
}
