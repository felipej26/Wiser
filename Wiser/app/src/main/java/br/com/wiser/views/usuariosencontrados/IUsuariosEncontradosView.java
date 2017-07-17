package br.com.wiser.views.usuariosencontrados;

import java.util.ArrayList;

import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 24/01/2017.
 */
public interface IUsuariosEncontradosView extends IView {
    void onInitView();
    void onLoad(ArrayList<Usuario> listaUsuarios);
    void onNotifyDataSetChanged();
    void onSetVisibilityProgressBar(int visibility);
}
