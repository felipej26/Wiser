package br.com.wiser.features.mensagem;

import java.util.List;

/**
 * Created by Jefferson on 12/03/2017.
 */

public interface IMensagemAdapter {
    int getItemCount();
    void addItem(Mensagem mensagem);
    void addAll(List<Mensagem> listaMensagens);
    void onSetSugestao(MensagemAdapter.Callback callback);
}
