package br.com.wiser.views.forum;

import java.util.LinkedList;

import br.com.wiser.models.forum.Discussao;
import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 25/01/2017.
 */
public interface IForumView extends IView {
    void onInitView();
    void onLoadListaDiscussoes(LinkedList<Discussao> listaDiscussao);
    void onSetVisibilityProgressBar(int visibility);
}
