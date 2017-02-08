package br.com.wiser.views.principal;

import android.view.View;

import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 23/01/2017.
 */
public interface IPrincipalView extends IView {
    void onInitView();
    View onGetSnackBarView();
}
