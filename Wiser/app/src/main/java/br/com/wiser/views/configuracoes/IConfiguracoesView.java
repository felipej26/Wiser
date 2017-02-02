package br.com.wiser.views.configuracoes;

import android.widget.Spinner;

import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 22/01/2017.
 */
public interface IConfiguracoesView extends IView {
    void onInitView();

    Spinner getCmbIdioma();
    Spinner getCmbFluencia();
    String getTextTxtStatus();

    void onSetSelectionCmbIdioma(int posicao);
    void onSetSelectionCmbFluencia(int posicao);
    void onSetSelectionTxtStatus(int tamanho);
    void onSetTextTxtStatus(String status);
    void onSetTextLblContLetras(String contLetras);

    int onGetItemIdCmbIdioma();
    int onGetItemIdCmbFluencia();
    String onGetTextTxtStatus();
}
