package br.com.wiser.features.contato;

import java.util.ArrayList;

import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 23/01/2017.
 */
public interface IContatosView extends IView {
    void onInitView();
    void onLoadListaContatos(ArrayList<Contato> listaContatos);
    void onNotifyDataSetChanged();
}
