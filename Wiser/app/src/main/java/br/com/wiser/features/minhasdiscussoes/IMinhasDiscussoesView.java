package br.com.wiser.features.minhasdiscussoes;

import java.util.LinkedList;

import br.com.wiser.features.discussao.Discussao;
import br.com.wiser.IView;

/**
 * Created by Jefferson on 25/01/2017.
 */
public interface IMinhasDiscussoesView extends IView {
    void onInitView();
    void onLoadListaDiscussoes(LinkedList<Discussao> listaDiscussoes);
    void onSetVisibilityProgressBar(int visibility);
}
