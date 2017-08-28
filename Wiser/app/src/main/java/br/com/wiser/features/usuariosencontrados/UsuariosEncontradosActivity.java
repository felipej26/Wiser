package br.com.wiser.features.usuariosencontrados;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.AdapterView;

import java.util.ArrayList;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.AbstractActivity;
import android.widget.ProgressBar;

/**
 * Created by Jefferson on 31/03/2016.
 */
public class UsuariosEncontradosActivity extends AbstractActivity implements IUsuariosEncontradosView {

    private UsuariosEncontradosPresenter usuariosEncontradosPresenter;

    private UsuariosEncontradosAdapter adapter;
    private GridView grdResultado;
    private ProgressBar pgbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contatos_encontrar_usuarios_resultados);

        usuariosEncontradosPresenter = new UsuariosEncontradosPresenter();
        usuariosEncontradosPresenter.onCreate(this,
                (ArrayList<Usuario>) getIntent().getBundleExtra(Sistema.LISTAUSUARIOS).get(Sistema.LISTAUSUARIOS));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onInitView() {
        getActionBar().setDisplayHomeAsUpEnabled(true);

        grdResultado = (GridView) findViewById(R.id.grdResultado);
        grdResultado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usuariosEncontradosPresenter.abrirPerfil(position);
            }
        });

        pgbLoading = (ProgressBar) findViewById(R.id.pgbLoading);
    }

    @Override
    public void onLoad(ArrayList<Usuario> listaUsuarios) {
        adapter = new UsuariosEncontradosAdapter(getContext(), R.layout.contatos_encontrar_pessoas_resultados_grid, listaUsuarios);
        grdResultado.setAdapter(adapter);
    }

    @Override
    public void onNotifyDataSetChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onSetVisibilityProgressBar(int visibility) {
        if (visibility == View.VISIBLE) {
            pgbLoading.bringToFront();
        }

        pgbLoading.setVisibility(visibility);
    }
}