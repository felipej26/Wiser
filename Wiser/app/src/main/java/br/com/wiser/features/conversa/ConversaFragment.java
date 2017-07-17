package br.com.wiser.features.conversa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.util.List;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.mensagem.Mensagem;
import br.com.wiser.features.mensagem.MensagemActivity;
import br.com.wiser.views.AbstractFragment;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class ConversaFragment extends AbstractFragment {

    private List<Conversa> listaConversas;

    private ConversaPresenter conversasPresenter;

    private View view;
    private RecyclerView recyclerView;
    private ConversaAdapter adapter;

    public static ConversaFragment newInstance() {
        return new ConversaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chat_principal, container, false);

        conversasPresenter = new ConversaPresenter();
        onLoad();
        onLoadChat();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Mensagem mensagem) {
        for (Conversa conversa : listaConversas) {
            if (conversa.getId() == mensagem.getConversa()) {
                conversa.getMensagens().add(mensagem);
                adapter.updateItem(conversa);
            }
        }
    }

    private void onLoad() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new ConversaAdapter(getContext(), new ConversaAdapter.ICallback() {
            @Override
            public void onClick(int posicao) {
                Intent i = new Intent(getContext(), MensagemActivity.class);
                i.putExtra(Sistema.CONVERSA, listaConversas.get(posicao));
                getContext().startActivity(i);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void onLoadChat() {
        try {
            listaConversas = conversasPresenter.carregarConversas();
            adapter.addAll(listaConversas);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
