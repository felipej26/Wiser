package br.com.wiser.presenters.usuariosencontrados;

import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import br.com.wiser.facebook.Facebook;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.dialogs.DialogPerfilUsuario;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.views.usuariosencontrados.IUsuariosEncontradosView;

/**
 * Created by Jefferson on 24/01/2017.
 */
public class UsuariosEncontradosPresenter extends Presenter<IUsuariosEncontradosView> implements Observer {

    private ArrayList<Usuario> listaUsuarios;

    public void onCreate(IUsuariosEncontradosView view, final ArrayList<Usuario> listaUsuarios) {
        super.onCreate(view);
        view.onInitView();
        this.listaUsuarios = listaUsuarios;

        new Facebook(getContext()).carregarUsuarios(listaUsuarios, new ICallback() {
            @Override
            public void onSuccess() {
                UsuariosEncontradosPresenter.this.view.onLoad(listaUsuarios);

                for (Usuario usuario : listaUsuarios) {
                    usuario.addObserver(UsuariosEncontradosPresenter.this);
                }
            }

            @Override
            public void onError(String mensagemErro) {
                Log.e("Carregar Usuarios", mensagemErro);
            }
        });

    }

    public void abrirPerfil(int posicao) {
        DialogPerfilUsuario perfil = new DialogPerfilUsuario();
        perfil.show(view.getContext(), listaUsuarios.get(posicao));
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof Usuario) {
            view.onNotifyDataSetChanged();
        }
    }
}
