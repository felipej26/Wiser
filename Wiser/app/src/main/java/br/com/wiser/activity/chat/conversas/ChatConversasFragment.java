package br.com.wiser.activity.chat.conversas;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import br.com.wiser.Sistema;
import br.com.wiser.business.chat.conversas.ConversasDAO;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class ChatConversasFragment extends Fragment {

    private ProgressBar pgbLoading;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private LinkedList<ConversasDAO> conversas;

    public static ChatConversasFragment newInstance() {
        return new ChatConversasFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_mensagens, container, false);

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
        if (conversas != null) {
            if (!conversas.isEmpty()) {
                ((ChatConversasAdapter) adapter).setItems(conversas);
            }
        }
    }

    private void carregarComponentes(View view) {

        pgbLoading = (ProgressBar) view.findViewById(R.id.pgbLoadingMsg);
        pgbLoading.setVisibility(View.VISIBLE);
        pgbLoading.bringToFront();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        conversas = new LinkedList<>();
        adapter = new ChatConversasAdapter(ChatConversasFragment.this.getContext(), conversas);
        recyclerView.setAdapter(adapter);

        pgbLoading.setVisibility(View.INVISIBLE);
    }
}
