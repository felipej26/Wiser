package br.com.wiser.business.encontrarusuarios.pesquisa;

import android.content.Context;

import br.com.wiser.business.app.servidor.Servidor;

public class PesquisaDAO extends Pesquisa {

    public void procurarUsuarios(Context context) {
        setListaResultados(new Servidor().new Contatos(context).encontrarUsuarios(this));
    }
}