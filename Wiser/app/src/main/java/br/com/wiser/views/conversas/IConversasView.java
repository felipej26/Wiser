package br.com.wiser.views.conversas;

import java.util.LinkedList;

import br.com.wiser.models.conversas.Conversas;
import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 24/01/2017.
 */
public interface IConversasView extends IView {
    void onInitView();
    void onLoadListaConversas(LinkedList<Conversas> listaConversas);
}
