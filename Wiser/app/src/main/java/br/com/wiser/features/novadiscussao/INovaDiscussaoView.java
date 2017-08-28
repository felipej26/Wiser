package br.com.wiser.features.novadiscussao;

import br.com.wiser.IView;

/**
 * Created by Jefferson on 25/01/2017.
 */
public interface INovaDiscussaoView extends IView {
    void onInitView();
    void onSetTextLblContTitulo(String texto);
    void onSetTextLblContDescricao(String texto);
}
