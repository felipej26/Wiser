package br.com.wiser.features.conversa;

import java.text.ParseException;
import java.util.List;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class ConversaPresenter {

    private ConversaDAO conversaDAO = new ConversaDAO();

    public List<Conversa> carregarConversas() throws ParseException {
        List<Conversa> listaConversa = conversaDAO.get();

        return listaConversa;
    }
}
