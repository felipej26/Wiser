package br.com.wiser.features.discussao;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.dialogs.DialogPerfilUsuario;
import br.com.wiser.dialogs.IDialog;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.utils.Utils;

/**
 * Created by Jefferson on 31/08/2017.
 */
public class DiscussaoPartial {

    public void onDiscussaoClicked(Context context, Discussao discussao) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Sistema.DISCUSSAO, discussao);

        Intent i = new Intent(context, DiscussaoActivity.class);
        i.putExtra(Sistema.DISCUSSAO, bundle);
        context.startActivity(i);
    }

    public void onPerfilClicked(Context context, Usuario usuario) {
        DialogPerfilUsuario perfil = new DialogPerfilUsuario();
        perfil.show(context, usuario);
    }

    public void onDesativarCliked(Context context, final Discussao discussao, final ICallback callback) {
        DialogConfirmar confirmar = new DialogConfirmar();

        confirmar.setYesClick(new IDialog() {
            @Override
            public void onClick() {
                DiscussaoPresenter discussaoPresenter = new DiscussaoPresenter(discussao);
                discussaoPresenter.desativarDiscussao(callback);
            }
        });

        if (discussao.isAtiva()) {
            confirmar.setMensagem(context.getString(R.string.confirmar_desativar_discussao));
        }
        else {
            confirmar.setMensagem(context.getString(R.string.confirmar_reativar_discussao));
        }

        confirmar.show(context);
    }

    public void onCompartilharClicked(View view) {
        Utils.compartilharComoImagem(view);
    }
}
