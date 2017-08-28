package br.com.wiser.features.forum;

import java.util.LinkedList;

import br.com.wiser.features.discussao.Discussao;
import br.com.wiser.IView;

/**
 * Created by Jefferson on 25/01/2017.
 */
public interface IForumView extends IView {
    void onInitView();
    void onLoadListaDiscussoes(LinkedList<Discussao> listaDiscussao);
    void onSetVisibilityProgressBar(int visibility);
}
