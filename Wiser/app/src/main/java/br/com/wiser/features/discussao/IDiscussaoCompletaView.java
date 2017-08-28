package br.com.wiser.features.discussao;

import java.util.LinkedList;

/**
 * Created by Jefferson on 25/01/2017.
 */
public interface IDiscussaoCompletaView extends IDiscussaoView {
    void onInitView();
    void onLoadRespostas(LinkedList<Resposta> listaRespostas);
    void onLoadProfilePicture(String url);
    void onNotifyDataSetChanged();

    void onSetTextLblID(String texto);
    void onSetTextLblTitulo(String texto);
    void onSetTextLblDescricao(String texto);
    void onSetTextLblAutor(String texto);
    void onSetTextLblDataHora(String texto);
    void onSetTextLblQntRespostas(String texto);
    void onSetTextTxtResposta(String texto);
    void onSetVisibilityFrmResponder(int visibility);
    void onSetVisibilityProgressBar(int visibility);
}
