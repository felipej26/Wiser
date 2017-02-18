package br.com.wiser.views.conversas;

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

import br.com.wiser.R;
import br.com.wiser.models.conversas.Conversas;
import br.com.wiser.interfaces.IClickListener;
import br.com.wiser.presenters.conversas.ConversasPresenter;
import br.com.wiser.views.AbstractFragment;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class ConversasFragment extends AbstractFragment implements IConversasView {

    private ConversasPresenter conversasPresenter;

    private View view;
    private RecyclerView recyclerView;
    private ConversasAdapter adapter;

    public static ConversasFragment newInstance() {
        return new ConversasFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chat_principal, container, false);

        conversasPresenter = new ConversasPresenter();
        conversasPresenter.onCreate(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        conversasPresenter.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(final LinkedList<Conversas> listaConversas) {
        conversasPresenter.onEvent(listaConversas);
    }

    @Override
    public void onInitView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new ConversasAdapter(getContext(), new LinkedList<Conversas>());
        adapter.setClickListener(new IClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                conversasPresenter.abrirChat(position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoadListaConversas(LinkedList<Conversas> listaConversas) {
        adapter.setItems(listaConversas);
    }

    @Override
    public void onNotifyDataSetChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
