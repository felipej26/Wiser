package br.com.wiser.views.procurardiscussao;

import java.util.LinkedList;

import br.com.wiser.models.forum.Discussao;
import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 25/01/2017.
 */
public interface IProcurarDiscussaoActivity extends IView {
    void onInitView();
    void onLoadListaDiscussoes(LinkedList<Discussao> listaDiscussoes);
    void onSetVisibilityProgressBar(int visibility);
    void onSetTextLblResultados(String texto);
    void onSetTextLblContResultados(String texto);

    void onSetVisibilityLblResultados(int visibility);
    void onSetVisibilityLblContResultados(int visibility);
}
