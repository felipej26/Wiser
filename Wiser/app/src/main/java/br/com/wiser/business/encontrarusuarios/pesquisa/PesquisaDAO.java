package br.com.wiser.business.encontrarusuarios.pesquisa;

import android.content.Context;

import br.com.wiser.business.app.servidor.Servidor;

public class PesquisaDAO extends Pesquisa {

    public boolean procurarUsuarios(Context context) {
        this.setListaResultados(new Servidor().new Contatos(context).encontrarUsuarios(this));
        return (this.getListaResultados().size() > 0);
    }
}