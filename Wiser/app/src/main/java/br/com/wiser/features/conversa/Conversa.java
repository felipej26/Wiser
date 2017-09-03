package br.com.wiser.features.conversa;

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
    private Usuario destinatario;
    private LinkedList<Mensagem> mensagens = new LinkedList<>();
    private HashSet<String> sugestoes = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Usuario getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Usuario destinatario) {
        this.destinatario = destinatario;
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
