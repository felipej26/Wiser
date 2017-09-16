package br.com.wiser.features.conversa;

import java.io.Serializable;
import java.util.HashSet;

import br.com.wiser.Sistema;
import br.com.wiser.features.mensagem.Mensagem;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jefferson on 23/05/2016.
 */
public class Conversa extends RealmObject implements Serializable {

    @PrimaryKey
    private long id;
    private long destinatario;
    private RealmList<Mensagem> mensagens = new RealmList<>();

    @Ignore
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

    public RealmList<Mensagem> getMensagens() {
        return mensagens;
    }

    public void setMensagens(RealmList<Mensagem> mensagens) {
        this.mensagens = mensagens;
    }

    public HashSet<String> getSugestoes() {
        return sugestoes;
    }

    public void setSugestoes(HashSet<String> sugestoes) {
        this.sugestoes = sugestoes;
    }

    public int getContMsgNaoLidas() {
        return mensagens.where()
                .notEqualTo("usuario", Sistema.getUsuario().getId())
                .equalTo("lida", false)
                .findAll().size();
    }
}
