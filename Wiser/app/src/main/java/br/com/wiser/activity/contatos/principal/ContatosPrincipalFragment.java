package br.com.wiser.activity.contatos.principal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.activity.chat.mensagens.ChatMensagensActivity;
import br.com.wiser.business.app.usuario.Usuario;
import br.com.wiser.business.chat.conversas.ConversasDAO;
import br.com.wiser.enums.Activities;
import br.com.wiser.utils.IClickListener;
import br.com.wiser.utils.Utils;
import android.widget.Button;

import java.util.List;

/**
 * Created by Jefferson on 22/09/2016.
 */
public class ContatosPrincipalFragment extends Fragment implements IClickListener {

    private Button btnEncontrarUsuarios;

    private RecyclerView recyclerView;
    private ContatoAdapter adapter;

    private List<Usuario> listaContatos;

    public static ContatosPrincipalFragment newInstance() { return new ContatosPrincipalFragment(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contatos_principal, container, false);
        carregarComponentes(view);

        return view;
    }

    private void carregarComponentes(View view) {
        btnEncontrarUsuarios = (Button) view.findViewById(R.id.btnEncontrarUsuarios);
        btnEncontrarUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encontrarUsuarios(v);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        listaContatos = Sistema.getUsuario(view.getContext()).carregarContatos(view.getContext());
        adapter = new ContatoAdapter(view.getContext(), listaContatos);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    public void encontrarUsuarios(View view) {
        Utils.chamarActivity(getActivity(), Activities.CHAT_PESQUISA);
    }

    @Override
    public void itemClicked(View view, int position) {
        Intent i = new Intent(view.getContext(), ChatMensagensActivity.class);
        ConversasDAO conversa = new ConversasDAO();
        conversa.setDestinatario(listaContatos.get(position));
        i.putExtra("conversa", conversa);
        view.getContext().startActivity(i);
    }
}
