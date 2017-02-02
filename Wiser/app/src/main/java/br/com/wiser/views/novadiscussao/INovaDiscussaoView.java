package br.com.wiser.views.novadiscussao;

import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 25/01/2017.
 */
public interface INovaDiscussaoView extends IView {
    void onInitView();
    void onSetTextLblContTitulo(String texto);
    void onSetTextLblContDescricao(String texto);
}
