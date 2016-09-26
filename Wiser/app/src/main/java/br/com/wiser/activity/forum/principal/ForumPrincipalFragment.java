package br.com.wiser.activity.forum.principal;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Button;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.activity.forum.DiscussaoCardViewAdapter;
import br.com.wiser.activity.forum.IDiscussaoCardViewAdapterCallback;
import br.com.wiser.business.forum.discussao.Discussao;
import br.com.wiser.business.forum.discussao.DiscussaoDAO;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.dialogs.DialogInformar;
import br.com.wiser.enums.Activities;
import br.com.wiser.utils.Utils;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class ForumPrincipalFragment extends Fragment implements IDiscussaoCardViewAdapterCallback {

    private Button btnNovaDiscussao;
    private Button btnProcurarDiscussao;
    private Button btnAtualizarDiscussoes;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ProgressBar pgbLoading;

    private DiscussaoDAO objDiscussaoDAO;
    private LinkedList<DiscussaoDAO> listaDiscussoes;

    public static ForumPrincipalFragment newInstance() {
        return new ForumPrincipalFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forum_principal, container, false);
        carregarComponentes(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        carregarComponentes(this.getView());
    }

    private void carregarComponentes(View view) {
        btnNovaDiscussao = (Button) view.findViewById(R.id.btnNovaDiscussao);
        btnProcurarDiscussao = (Button) view.findViewById(R.id.btnProcurarDiscussao);
        btnAtualizarDiscussoes = (Button) view.findViewById(R.id.btnAtualizarDiscussoes);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        btnNovaDiscussao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamarNovaDiscussao(v);
            }
        });
        btnProcurarDiscussao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamarProcurarDiscussao(v);
            }
        });
        btnAtualizarDiscussoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarDiscussoes(v);
            }
        });

        pgbLoading = (ProgressBar) view.findViewById(R.id.pgbLoading);

        objDiscussaoDAO = new DiscussaoDAO();

        carregarDados(view);
    }

    private void carregarDados(final View view) {

        pgbLoading.setVisibility(View.VISIBLE);
        pgbLoading.bringToFront();

        final Handler hCarregar = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                listaDiscussoes = objDiscussaoDAO.carregarDiscussoes(view.getContext());
                tratarLoading(hCarregar, listaDiscussoes);
            }
        }).start();
    }

    private void tratarLoading(Handler hCarregar, final LinkedList<DiscussaoDAO> listaDiscussoes){
        hCarregar.post(new Runnable() {
            @Override
            public void run() {

                if (listaDiscussoes != null) {
                    if (!listaDiscussoes.isEmpty()) {
                        adapter = new DiscussaoCardViewAdapter(ForumPrincipalFragment.this.getActivity(), ForumPrincipalFragment.this, listaDiscussoes);
                        recyclerView.setAdapter(adapter);
                    }
                }

                pgbLoading.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void chamarNovaDiscussao(View view) {
        Utils.chamarActivity((Activity) view.getContext(), Activities.FORUM_NOVA_DISCUSSAO);
    }

    public void chamarProcurarDiscussao(View view) {
        Utils.chamarActivity((Activity) view.getContext(), Activities.FORUM_PESQUISA);
    }

    public void atualizarDiscussoes(View view) {
        carregarDados(view);
    }

    @Override
    public void desativarDiscussao(final Discussao discussao) {
        DialogConfirmar confirmar = new DialogConfirmar(this.getActivity());

        if (discussao.isAtiva()) {
            confirmar.setMensagem(this.getContext().getString(R.string.confirmar_desativar_discussao));
        }
        else {
            confirmar.setMensagem(this.getContext().getString(R.string.confirmar_reativar_discussao));
        }

        confirmar.setYesClick(new DialogConfirmar.DialogInterface() {
            @Override
            public void onClick() {
                desativar(discussao);
            }
        });

        confirmar.show();
    }

    public void desativar(Discussao discussao) {
        DialogInformar informar = new DialogInformar(this.getActivity());
        DiscussaoDAO objDiscussao = new DiscussaoDAO();

        objDiscussao.setId(discussao.getId());
        objDiscussao.setAtiva(!discussao.isAtiva());

        if (objDiscussao.desativarDiscussao(ForumPrincipalFragment.this.getContext())) {
            discussao.setAtiva(objDiscussao.isAtiva());

            informar.setMensagem(getString(R.string.sucesso_discussao_excluida));
        }
        else {
            informar.setMensagem(this.getContext().getString(R.string.erro_excluir_discussao));
        }

        informar.show();
    }
}