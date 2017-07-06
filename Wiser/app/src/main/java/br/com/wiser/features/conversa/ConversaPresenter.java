package br.com.wiser.features.conversa;

import java.text.ParseException;
import java.util.List;

import br.com.wiser.Sistema;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class ConversaPresenter {

    private ConversaDAO conversaDAO;

    public ConversaPresenter(ConversaDAO conversaDAO) {
        this.conversaDAO = conversaDAO;
    }

    public List<Conversa> carregarConversas() throws ParseException {
        List<Conversa> listaConversa = conversaDAO.get();

        for (Conversa conversa : listaConversa) {
            Sistema.getListaUsuarios().get(conversa.getDestinatario());
        }

        return listaConversa;
    }
}
