package br.com.wiser.presenters.conversas;

import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import br.com.wiser.Sistema;
import br.com.wiser.models.conversas.Conversas;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.views.conversas.IConversasView;
import br.com.wiser.views.mensagens.MensagensActivity;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class ConversasPresenter extends Presenter<IConversasView> {

    private LinkedList<Conversas> listaConversas;

    @Override
    protected void onCreate() {
        super.onCreate();
        view.onInitView();
    }

    public void onStart() {
        EventBus.getDefault().register(view);
    }

    public void onStop() {
        EventBus.getDefault().unregister(view);
    }

    public void onEvent(LinkedList<Conversas> listaConversas) {
        if (!listaConversas.isEmpty()) {
            this.listaConversas = listaConversas;
            view.onLoadListaConversas(listaConversas);
        }
    }

    public void abrirChat(int posicao) {
        Intent i = new Intent(getContext(), MensagensActivity.class);
        i.putExtra(Sistema.CONVERSA, listaConversas.get(posicao));
        getContext().startActivity(i);
    }
}
