package br.com.wiser.views.configuracoes;

import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 22/01/2017.
 */
public interface IConfiguracoesView extends IView {
    void onInitView();

    void onSetSelectionCmbIdioma(int idioma);
    void onSetSelectionCmbFluencia(int fluencia);
    void onSetSelectionTxtStatus(int tamanho);
    void onSetTextTxtStatus(String status);
    void onSetTextLblContLetras(String contLetras);

    int onGetItemIdCmbIdioma();
    int onGetItemIdCmbFluencia();
    String onGetTextTxtStatus();
}
