package br.com.wiser.views.sobre;

import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 22/01/2017.
 */
public interface ISobreView extends IView {
    void onInitView();
    void onSetTextLblVersao(String versao);
}
