package br.com.wiser.business.chat.conversas;

import android.content.Context;

import java.util.LinkedList;

import br.com.wiser.business.app.servidor.Servidor;
import br.com.wiser.business.app.usuario.Usuario;
import br.com.wiser.business.chat.mensagem.Mensagem;

public class ConversasDAO extends Conversas {

    public LinkedList<ConversasDAO> carregarGeral(Context context) {
        return new Servidor().new Chat(context).carregarGeral();
    }

    public boolean enviarMensagem(Context context, Mensagem mensagem) {
        return new Servidor().new Chat(context).enviarMensagem(this, getDestinatario().getUserID(), mensagem);
    }
}