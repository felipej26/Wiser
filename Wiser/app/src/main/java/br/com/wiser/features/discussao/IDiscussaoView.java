package br.com.wiser.features.discussao;

import android.view.View;

import br.com.wiser.IView;

/**
 * Created by Jefferson on 23/01/2017.
 */
public interface IDiscussaoView extends IView {
    void onClick(int posicao);
    void onClickPerfil(int posicao);
    void desativarDiscussao(int posicao);
    void compartilharDiscussao(View view);
}
