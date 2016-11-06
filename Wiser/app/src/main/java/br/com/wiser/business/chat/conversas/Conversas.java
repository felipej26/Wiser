package br.com.wiser.business.chat.conversas;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;

import br.com.wiser.business.app.usuario.Usuario;
import br.com.wiser.business.chat.mensagem.Mensagem;
import br.com.wiser.business.chat.paginas.Pagina;

/**
 * Created by Jefferson on 23/05/2016.
 */
public class Conversas implements Serializable {

    private long id = 0;
    private Usuario destinatario = new Usuario(0);
    private LinkedList<Mensagem> mensagens = new LinkedList<Mensagem>();

    private boolean carregouSugestoes;
    private HashSet<String> sugestoes;

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

    public boolean isCarregouSugestoes() {
        return carregouSugestoes;
    }

    public void setCarregouSugestoes(boolean carregouSugestoes) {
        this.carregouSugestoes = carregouSugestoes;
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
