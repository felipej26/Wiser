package br.com.wiser.features.sobre;

import br.com.wiser.IView;

/**
 * Created by Jefferson on 22/01/2017.
 */
public interface ISobreView extends IView {
    void onInitView();
    void onSetTextLblVersao(String versao);
}
