package br.com.wiser.features.usuariosencontrados;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import br.com.wiser.AbstractActivity;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.dialogs.DialogPerfilUsuario;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.IClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jefferson on 31/03/2016.
 */
public class UsuariosEncontradosActivity extends AbstractActivity {

    private UsuariosEncontradosPresenter usuariosEncontradosPresenter;

    private UsuariosEncontradosAdapter adapter;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.pgbLoading) ProgressBar pgbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contatos_encontrar_usuarios_resultados);

        usuariosEncontradosPresenter = new UsuariosEncontradosPresenter(
                (ArrayList<Usuario>) getIntent().getBundleExtra(Sistema.LISTAUSUARIOS).get(Sistema.LISTAUSUARIOS)
        );

        onLoad();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void onLoad() {
        ButterKnife.bind(this);

        onPrgLoadingChanged(View.VISIBLE);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UsuariosEncontradosAdapter(this, usuariosEncontradosPresenter.getUsuarios());
        adapter.setClickListener(new IClickListener() {
            @Override
            public void itemClicked(View view, int posicao) {
                DialogPerfilUsuario perfil = new DialogPerfilUsuario();
                perfil.show(getContext(), usuariosEncontradosPresenter.getUsuarios().get(posicao));
            }
        });
        recyclerView.setAdapter(adapter);

        onPrgLoadingChanged(View.INVISIBLE);
    }

    private void onPrgLoadingChanged(int visibility) {
        pgbLoading.bringToFront();
        pgbLoading.setVisibility(visibility);
    }
}