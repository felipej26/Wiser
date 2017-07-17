package br.com.wiser.features.conversa;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;

import br.com.wiser.features.mensagem.Mensagem;
import br.com.wiser.features.usuario.Usuario;

/**
 * Created by Jefferson on 23/05/2016.
 */
public class Conversa implements Serializable {

    private long id;
    @SerializedName("destinatario")
    private long idDestinatario;
    private Usuario usuario;
    private LinkedList<Mensagem> mensagens = new LinkedList<>();
    private HashSet<String> sugestoes = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(long idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LinkedList<Mensagem> getMensagens() {
        return mensagens;
    }

    public void setMensagens(LinkedList<Mensagem> mensagens) {
        this.mensagens = mensagens;
    }

    public HashSet<String> getSugestoes() {
        return sugestoes;
    }

    public void setSugestoes(HashSet<String> sugestoes) {
        this.sugestoes = sugestoes;
    }

    public int getContMsgNaoLidas() {
        int naoLidos = 0;

        if (mensagens != null) {
            for (Mensagem m : this.mensagens) {
                if (m.isDestinatario() && !m.isLida()) {
                    naoLidos++;
                }
            }
        }

        return naoLidos;
    }
}
