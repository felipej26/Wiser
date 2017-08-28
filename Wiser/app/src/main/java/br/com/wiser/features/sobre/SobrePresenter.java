package br.com.wiser.features.sobre;

import br.com.wiser.R;
import br.com.wiser.Presenter;
import br.com.wiser.utils.Utils;

/**
 * Created by Jefferson on 22/01/2017.
 */
public class SobrePresenter extends Presenter<ISobreView> {

    public void onCreate() {
        String versao = "";

        view.onInitView();

        try {
            versao = getContext().getString(R.string.app_sobre_item_versao,
                    getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        view.onSetTextLblVersao(versao);
    }

    public void compartilharApp() {
        Utils.compartilharAppComoTexto(view.getContext());
    }
}
