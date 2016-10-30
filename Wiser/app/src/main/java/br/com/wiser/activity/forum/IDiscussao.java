package br.com.wiser.activity.forum;

import android.view.View;

/**
 * Created by Jefferson on 14/09/2016.
 */
public interface IDiscussao {
    void onClick(int posicao);
    void onClickPerfil(int posicao);
    void desativarDiscussao(int posicao);
    void compartilharDiscussao(View view);
}
