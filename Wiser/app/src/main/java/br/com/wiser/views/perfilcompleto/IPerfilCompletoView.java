package br.com.wiser.views.perfilcompleto;

import java.util.LinkedList;

import br.com.wiser.models.forum.Discussao;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 23/01/2017.
 */
public interface IPerfilCompletoView extends IView {
    void onInitView();
    void onLoadAsUser();
    void onLoadAsFriend(Usuario usuario);
    void onLoadAsNotFriend(Usuario usuario);
    void onLoadListaDiscussoes(LinkedList<Discussao> listaDiscussoes);

    void onLoadProfilePicture(String urlProfilePicture);
    void onSetTextLblNome(String nome);
    void onSetTextLblIdade(String idade);
    void onSetTextLblIdiomaNivel(String idioma);
    void onSetTextLblStatus(String status);
    void onSetVisibilityProgressBar(int visibility);
}
