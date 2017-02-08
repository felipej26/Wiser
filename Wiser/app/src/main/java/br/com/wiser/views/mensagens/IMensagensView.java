package br.com.wiser.views.mensagens;

import java.util.LinkedList;

import br.com.wiser.models.mensagens.Mensagem;
import br.com.wiser.dialogs.DialogSugestoes;

/**
 * Created by Jefferson on 24/01/2017.
 */
public interface IMensagensView extends DialogSugestoes.CallbackSugestao {
    void onInitView();
    void onLoadListaMensagens(LinkedList<Mensagem> listaMensagens);
    void onSetTitleActionBar(String titulo);
    void onSetPositionRecyclerView(int posicao);
    void onSetTextLblContLetras(String contLetras);
    void onSetVisibilityBtnSugestoes(int visibility);
    int onGetQntMensagens();
    void onClearCampos();
}
