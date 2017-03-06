package br.com.wiser.views.contatos;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import br.com.wiser.R;
import br.com.wiser.interfaces.IClickListener;
import br.com.wiser.models.contatos.Contato;
import br.com.wiser.presenters.contatos.ContatosPresenter;
import br.com.wiser.views.AbstractFragment;

/**
 * Created by Jefferson on 22/09/2016.
 */
public class ContatosFragment extends AbstractFragment implements IContatosView {

    private ContatosPresenter contatosPresenter;

    private View view;
    private Button btnEncontrarUsuarios;
    private RecyclerView recyclerView;
    private ContatosAdapter adapter;

    public static ContatosFragment newInstance() {
        return new ContatosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.contatos_principal, container, false);

        contatosPresenter = new ContatosPresenter();
        contatosPresenter.onCreate(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        contatosPresenter.onResume();
    }

    @Override
    public void onInitView() {
        btnEncontrarUsuarios = (Button) view.findViewById(R.id.btnEncontrarUsuarios);
        btnEncontrarUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contatosPresenter.startProcurarUsuarios();
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new ContatosAdapter(view.getContext(), new ArrayList<Contato>());
        adapter.setClickListener(new IClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                contatosPresenter.startChat(position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoadListaContatos(ArrayList<Contato> listaContatos) {
        adapter.setItems(listaContatos);
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