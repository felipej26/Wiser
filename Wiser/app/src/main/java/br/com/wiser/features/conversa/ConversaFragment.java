package br.com.wiser.features.conversa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.wiser.AbstractFragment;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.mensagem.MensagemActivity;
import br.com.wiser.features.usuario.Usuario;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class ConversaFragment extends AbstractFragment {

    private ConversaPresenter conversaPresenter;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    public static ConversaFragment newInstance() {
        return new ConversaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_principal, container, false);

        conversaPresenter = new ConversaPresenter();

        onLoad(view);
        return view;
    }

    private void onLoad(View view) {
        final ConversaAdapter adapter;
        ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new ConversaAdapter(getContext(), conversaPresenter.getConversas(), true, new ConversaAdapter.ICallback() {
            @Override
            public void onClick(long idConversa, Usuario usuario) {
                Intent i = new Intent(getContext(), MensagemActivity.class);
                i.putExtra(Sistema.CONVERSA, idConversa);
                i.putExtra(Sistema.CONTATO, usuario);
                startActivity(i);
            }
        });

        conversaPresenter.addUsuariosListener(new ConversaPresenter.ICallbackUsuarios() {
            @Override
            public void onSuccess(List<Usuario> listaUsuarios) {
                adapter.updateUsuarios(listaUsuarios);
            }
        });

        recyclerView.setAdapter(adapter);
    }
}
