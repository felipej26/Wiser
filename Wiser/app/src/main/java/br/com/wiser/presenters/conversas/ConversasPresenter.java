package br.com.wiser.presenters.conversas;

import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import br.com.wiser.Sistema;
import br.com.wiser.models.conversas.Conversas;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.views.conversas.IConversasView;
import br.com.wiser.views.mensagens.MensagensActivity;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class ConversasPresenter extends Presenter<IConversasView> implements Observer {

    private LinkedList<Conversas> listaConversas;

    @Override
    protected void onCreate() {
        super.onCreate();
        view.onInitView();
    }

    public void onResume() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                view.onNotifyDataSetChanged();
            }
        }, 1500);
    }

    public void onEvent(LinkedList<Conversas> listaConversas) {
        if (!listaConversas.isEmpty()) {
            this.listaConversas = listaConversas;
            view.onLoadListaConversas(listaConversas);

            for (Conversas conversa : listaConversas) {
                try {
                    Sistema.getListaUsuarios().get(conversa.getDestinatario()).addObserver(this);
                }
                catch (Exception e) {
                    Log.e("AAAA", "Deveria não cair aqui!" + e.getMessage());
                }
            }

            view.onNotifyDataSetChanged();
        }
    }

    public void abrirChat(int posicao) {
        Intent i = new Intent(getContext(), MensagensActivity.class);
        i.putExtra(Sistema.CONVERSA, listaConversas.get(posicao));
        getContext().startActivity(i);
    }

    @Override
    public void update(Observable observable, Object data) {
        view.onNotifyDataSetChanged();
    }
}
