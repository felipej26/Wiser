package br.com.wiser.features.procurarusuarios;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import br.com.wiser.AbstractAppCompatActivity;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.dialogs.DialogPerfilUsuario;
import br.com.wiser.features.pesquisarusuarios.Pesquisa;
import br.com.wiser.features.pesquisarusuarios.PesquisarUsuariosPresenter;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.features.usuariosencontrados.UsuariosEncontradosAdapter;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.interfaces.IClickListener;
import br.com.wiser.utils.ComboBoxItem;
import br.com.wiser.utils.FiltrosManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProcurarUsuariosActivity extends AbstractAppCompatActivity {

    private PesquisarUsuariosPresenter procurarContatosPresenter;
    private UsuariosEncontradosAdapter adapter;

    private FiltrosManager idiomasManager;
    private FiltrosManager fluenciasManager;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.lytFiltros) LinearLayout lytFiltros;
    @BindView(R.id.lytFluencias) FlexboxLayout lytFluencias;
    @BindView(R.id.btnProcurar) Button btnProcurar;
    @BindView(R.id.pgbLoading) ProgressBar pgbLoading;
    @BindView(R.id.rcvUsuarios) RecyclerView rcvUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procurar_usuarios);

        procurarContatosPresenter = new PesquisarUsuariosPresenter();

        onLoad();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void onLoad() {
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        rcvUsuarios.setHasFixedSize(true);
        rcvUsuarios.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UsuariosEncontradosAdapter(this, new ArrayList<Usuario>());
        adapter.setClickListener(new IClickListener() {
            @Override
            public void itemClicked(View view, int posicao) {
                DialogPerfilUsuario perfil = new DialogPerfilUsuario();
                perfil.show(getContext(), procurarContatosPresenter.getUsuarios().get(posicao));
            }
        });

        rcvUsuarios.setAdapter(adapter);

        idiomasManager = new FiltrosManager();
        for (ComboBoxItem item : Sistema.getListaIdiomas()) {
            idiomasManager.addFiltro(item.getId(), item.getDescricao(), true);
        }

        fluenciasManager = new FiltrosManager();
        for (ComboBoxItem item : Sistema.getListaFluencias()) {
            fluenciasManager.addFiltro(item.getId(), item.getDescricao(), true);
        }

        for (CheckBox chec : fluenciasManager.getFiltrosAsCheckBox(this)) {
            CheckBox check = (CheckBox) LayoutInflater.from(this).inflate(R.layout.frame_checkbox_filter, lytFiltros, false);
        }
    }

    @OnClick(R.id.btnProcurar)
    public void onProcurarClicked() {
        Pesquisa pesquisa = new Pesquisa();

        onPrgLoadingChanged(View.VISIBLE);

        /*
        pesquisa.setIdioma(0);
        pesquisa.setFluencia(0);
        */

        procurarContatosPresenter.procurarUsuarios(pesquisa, new ICallback() {
            @Override
            public void onSuccess() {
                if (procurarContatosPresenter.getUsuarios().size() <= 0) {
                    showToast(getString(R.string.usuarios_nao_encontrados));
                }
                else {
                    adapter.limparDados();

                    adapter.addItems(procurarContatosPresenter.getUsuarios());
                }

                onPrgLoadingChanged(View.INVISIBLE);
            }

            @Override
            public void onError(String mensagemErro) {
                showToast("Erro ao procurar usuarios");
                onPrgLoadingChanged(View.INVISIBLE);
            }
        });
    }

    private void onPrgLoadingChanged(int visibility) {
        pgbLoading.bringToFront();
        pgbLoading.setVisibility(visibility);
    }
}
