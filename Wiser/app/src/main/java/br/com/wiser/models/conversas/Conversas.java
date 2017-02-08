package br.com.wiser.models.conversas;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.models.mensagens.Mensagem;

/**
 * Created by Jefferson on 23/05/2016.
 */
public class Conversas implements Serializable {

    @SerializedName("id")
    private long id = 0;

    @SerializedName("destinatario")
    private long destinatario;

    @SerializedName("mensagens")
    private LinkedList<Mensagem> mensagens = new LinkedList<>();

    private boolean carregouSugestoes;
    private HashSet<String> sugestoes = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(long destinatario) {
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
