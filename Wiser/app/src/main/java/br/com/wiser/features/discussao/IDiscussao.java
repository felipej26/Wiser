package br.com.wiser.features.discussao;

import android.view.View;

import br.com.wiser.IView;

/**
 * Created by Jefferson on 23/01/2017.
 */
public interface IDiscussao extends IView {
    void onDiscussaoClicked(int posicao);
    void onPerfilClicked(int posicao);
    void onDesativarCliked(int posicao);
    void onCompartilharClicked(View view);
}
