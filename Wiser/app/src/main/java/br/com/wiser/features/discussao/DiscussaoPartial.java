package br.com.wiser.features.discussao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.dialogs.IDialog;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.utils.Utils;

/**
 * Created by Jefferson on 31/08/2017.
 */
public class DiscussaoPartial {

    private Activity activity;
    private DiscussaoPresenter discussaoPresenter;

    public DiscussaoPartial(Activity activity) {
        this.activity = activity;
        discussaoPresenter = new DiscussaoPresenter();
    }

    public void onDiscussaoClicked(Discussao discussao) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Sistema.DISCUSSAO, discussao);

        Intent i = new Intent(activity, DiscussaoActivity.class);
        i.putExtra(Sistema.DISCUSSAO, bundle);
        activity.startActivity(i);
    }

    public void onPerfilClicked(int posicao) {
        //discussaoPresenter.openPerfil(Sistema.getListaUsuarios().get(adapter.getItem(posicao).getUsuario()));
    }

    public void onDesativarCliked(final Discussao discussao, final ICallback callback) {
        DialogConfirmar confirmar = new DialogConfirmar(activity);

        confirmar.setYesClick(new IDialog() {
            @Override
            public void onClick() {
                discussaoPresenter.desativarDiscussao(discussao, callback);
            }
        });

        if (discussao.isAtiva()) {
            confirmar.setMensagem(activity.getString(R.string.confirmar_desativar_discussao));
        }
        else {
            confirmar.setMensagem(activity.getString(R.string.confirmar_reativar_discussao));
        }

        confirmar.show();
    }

    public void onCompartilharClicked(View view) {
        Utils.compartilharComoImagem(view);
    }
}
