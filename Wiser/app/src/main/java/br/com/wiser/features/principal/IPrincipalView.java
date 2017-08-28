package br.com.wiser.features.principal;

import android.view.View;

import br.com.wiser.IView;

/**
 * Created by Jefferson on 23/01/2017.
 */
public interface IPrincipalView extends IView {
    void onInitView();
    View onGetSnackBarView();
}
