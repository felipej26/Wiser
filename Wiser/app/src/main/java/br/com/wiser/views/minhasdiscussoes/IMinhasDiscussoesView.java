package br.com.wiser.views.minhasdiscussoes;

import java.util.LinkedList;

import br.com.wiser.models.forum.Discussao;
import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 25/01/2017.
 */
public interface IMinhasDiscussoesView extends IView {
    void onInitView();
    void onLoadListaDiscussoes(LinkedList<Discussao> listaDiscussoes);
    void onSetVisibilityProgressBar(int visibility);
}
