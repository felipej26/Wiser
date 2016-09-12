package br.com.wiser.business.chat.conversas;

import java.util.LinkedList;

import br.com.wiser.business.app.servidor.Servidor;
import br.com.wiser.business.app.usuario.Usuario;

public class ConversasDAO extends Conversas {

    public LinkedList<ConversasDAO> carregarGeral(Usuario usuario) {
        return new Servidor().new Chat(usuario).carregarGeral();
    }

    public void enviarMensagem(String texto) {
    }
}