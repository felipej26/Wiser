package br.com.wiser.views.usuariosencontrados;

import java.util.ArrayList;
import java.util.LinkedList;

import br.com.wiser.models.contatos.Contato;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 24/01/2017.
 */
public interface IUsuariosEncontradosView extends IView {
    void onInitView();
    void onLoad(ArrayList<Usuario> listaUsuarios);
    void onNotifyDataSetChanged();
}
