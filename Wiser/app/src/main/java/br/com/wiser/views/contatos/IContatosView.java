package br.com.wiser.views.contatos;

import java.util.ArrayList;
import java.util.List;

import br.com.wiser.models.contatos.Contato;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 23/01/2017.
 */
public interface IContatosView extends IView {
    void onInitView();
    void onLoadListaContatos(ArrayList<Contato> listaContatos);
    void onNotifyDataSetChanged();
}
