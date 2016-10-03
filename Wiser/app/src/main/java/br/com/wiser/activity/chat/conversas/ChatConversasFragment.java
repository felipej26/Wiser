package br.com.wiser.activity.chat.conversas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.activity.chat.mensagens.ChatMensagensActivity;
import br.com.wiser.business.chat.conversas.ConversasDAO;
import br.com.wiser.utils.IClickListener;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class ChatConversasFragment extends Fragment implements IClickListener {

    private RecyclerView recyclerView;
    private ChatConversasAdapter adapter;

    private LinkedList<ConversasDAO> conversas;

    public static ChatConversasFragment newInstance() {
        return new ChatConversasFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_principal, container, false);

        carregarComponentes(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final LinkedList<ConversasDAO> conversas) {
        if (!conversas.isEmpty()) {
            this.conversas = conversas;
            adapter.setItems(conversas);
        }
    }

    private void carregarComponentes(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        conversas = new LinkedList<>();
        adapter = new ChatConversasAdapter(this.getContext(), conversas);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void itemClicked(View view, int position) {
        Intent i = new Intent(view.getContext(), ChatMensagensActivity.class);
        i.putExtra("conversa", conversas.get(position));
        view.getContext().startActivity(i);
    }
}
