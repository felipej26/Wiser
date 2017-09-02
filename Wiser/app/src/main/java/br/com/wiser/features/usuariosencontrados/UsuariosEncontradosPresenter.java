package br.com.wiser.features.usuariosencontrados;

import java.util.ArrayList;

import br.com.wiser.features.usuario.Usuario;

/**
 * Created by Jefferson on 24/01/2017.
 */
public class UsuariosEncontradosPresenter {

    private ArrayList<Usuario> listaUsuarios;

    public UsuariosEncontradosPresenter(ArrayList<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public ArrayList<Usuario> getUsuarios() {
        return listaUsuarios;
    }
}
