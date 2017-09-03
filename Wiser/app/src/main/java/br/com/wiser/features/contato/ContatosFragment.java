package br.com.wiser.features.contato;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import br.com.wiser.AbstractFragment;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.features.conversa.ConversaPresenter;
import br.com.wiser.features.mensagem.MensagemActivity;
import br.com.wiser.features.procurarusuarios.ProcurarUsuariosActivity;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.interfaces.IClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jefferson on 22/09/2016.
 */
public class ContatosFragment extends AbstractFragment {

    private ContatosPresenter contatosPresenter;

    @BindView(R.id.btnEncontrarUsuarios) Button btnEncontrarUsuarios;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private ContatosAdapter adapter;

    public static ContatosFragment newInstance() {
        return new ContatosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contatos_principal, container, false);

        contatosPresenter = new ContatosPresenter();
        contatosPresenter.carregarContatos(new ICallback() {
            @Override
            public void onSuccess() {
                adapter.setItems(contatosPresenter.getContatos());
            }

            @Override
            public void onError(String mensagemErro) {
                showToast(mensagemErro);
            }
        });

        onLoad(view);

        return view;
    }

    public void onLoad(View view) {
        ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ContatosAdapter(getContext(), new ArrayList<Usuario>());
        adapter.setClickListener(new IClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                startChat(position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.btnEncontrarUsuarios)
    public void onEncontrarUsuariosClicked() {
        startActivity(new Intent(getContext(), ProcurarUsuariosActivity.class));
    }

    private void startChat(final int posicao) {
        ConversaPresenter conversaPresenter = new ConversaPresenter();
        conversaPresenter.getConversa(contatosPresenter.getContato(posicao), new ConversaPresenter.ICallbackConversa() {
            @Override
            public void onSuccess(Conversa conversa) {
                Intent i = new Intent(getContext(), MensagemActivity.class);
                i.putExtra(Sistema.CONVERSA, conversa);
                startActivity(i);
            }

            @Override
            public void onError() {
            }
        });
    }
}