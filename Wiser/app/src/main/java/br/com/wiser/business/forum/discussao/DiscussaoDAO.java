package br.com.wiser.business.forum.discussao;

import android.content.Context;

import java.util.LinkedList;

import br.com.wiser.business.app.servidor.Servidor;
import br.com.wiser.business.forum.resposta.Resposta;

public class DiscussaoDAO extends Discussao {

    public LinkedList<DiscussaoDAO> carregarDiscussoes(Context context) {
        return new Servidor().new Forum(context).carregarDiscussoes(false);
    }

    public boolean salvarDiscussao(Context context) {
        return new Servidor().new Forum(context).salvarDiscussao(this);
    }

    public LinkedList<DiscussaoDAO> carregarMinhasDiscussoes(Context context) {
        return new Servidor().new Forum(context).carregarDiscussoes(true);
    }

    public LinkedList<DiscussaoDAO> procurarDiscussoes(Context context, String chave) {
        return new Servidor().new Forum(context).procurarDiscussoes(chave);
    }

    public boolean desativarDiscussao(Context context) {
        return new Servidor().new Forum(context).desativarDiscussao(this);
    }

    public void enviarResposta(Context context, Resposta resposta) {
        if (new Servidor().new Forum(context).responderDiscussao(this, resposta)) {
            this.getListaRespostas().add(resposta);
        }
    }
}
