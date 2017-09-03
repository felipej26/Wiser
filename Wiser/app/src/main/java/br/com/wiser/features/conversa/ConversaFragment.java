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

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.AbstractFragment;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.mensagem.MensagemActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class ConversaFragment extends AbstractFragment {

    private List<Conversa> listaConversas = new LinkedList<>();
    private ConversaAdapter adapter;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    public static ConversaFragment newInstance() {
        return new ConversaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_principal, container, false);

        onLoad(view);

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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(LinkedList<Conversa> listaConversas) {
        boolean encontrada;

        List<Conversa> novasConversas = new LinkedList<>();

        for (Conversa conversa : listaConversas) {
            encontrada = false;
            for (Conversa c : this.listaConversas) {
                if (c.getId() == conversa.getId()) {
                    c.getMensagens().addAll(conversa.getMensagens());
                    adapter.addMensagens(c, conversa.getMensagens());
                    encontrada = true;
                }
            }

            if (!encontrada) {
                novasConversas.add(conversa);
            }
        }

        this.listaConversas.addAll(novasConversas);
        adapter.addConversas(novasConversas);
    }

    private void onLoad(View view) {
        ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new ConversaAdapter(getContext(), new ConversaAdapter.ICallback() {
            @Override
            public void onClick(int posicao) {
                Intent i = new Intent(getContext(), MensagemActivity.class);
                i.putExtra(Sistema.CONVERSA, listaConversas.get(posicao));
                startActivity(i);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
