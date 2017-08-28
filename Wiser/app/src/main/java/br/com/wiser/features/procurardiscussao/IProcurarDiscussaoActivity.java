package br.com.wiser.features.procurardiscussao;

import java.util.LinkedList;

import br.com.wiser.features.discussao.Discussao;
import br.com.wiser.IView;

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
