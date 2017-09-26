package br.com.wiser.features.procurarusuarios;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import br.com.wiser.AbstractAppCompatActivity;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.dialogs.DialogPerfilUsuario;
import br.com.wiser.features.conversa.ConversaPresenter;
import br.com.wiser.features.mensagem.MensagemActivity;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.features.usuario.UsuarioPresenter;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.interfaces.ICallbackFinish;
import br.com.wiser.interfaces.IClickListener;
import br.com.wiser.utils.CheckPermissao;
import br.com.wiser.utils.ComboBoxItem;
import br.com.wiser.utils.FiltrosManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProcurarUsuariosActivity extends AbstractAppCompatActivity {

    private ProcurarUsuariosPresenter procurarContatosPresenter;
    private UsuariosEncontradosAdapter adapter;

    private FiltrosManager idiomasManager;
    private FiltrosManager fluenciasManager;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.lytFiltroExpandido) RelativeLayout lytFiltroExpandido;
    @BindView(R.id.lytIdiomas) FlexboxLayout lytIdiomas;
    @BindView(R.id.lytFluencias) FlexboxLayout lytFluencias;
    @BindView(R.id.btnAddFiltro) Button btnAddFiltro;
    @BindView(R.id.btnProcurar) Button btnProcurar;

    @BindView(R.id.lytFiltro) RelativeLayout lytFiltro;
    @BindView(R.id.lblIdiomasEscolhidos) TextView lblIdiomasEscolhidos;
    @BindView(R.id.lblFluenciasEscolhidas) TextView lblFluenciasEscolhidas;
    @BindView(R.id.btnFiltrar) Button btnFiltrar;

    @BindView(R.id.lblUsuariosNaoEncontrados) TextView lblUsuariosNaoEncontrados;
    @BindView(R.id.btnMostrarFiltros) Button btnMostrarFiltros;
    @BindView(R.id.pgbLoading) ProgressBar pgbLoading;
    @BindView(R.id.rcvUsuarios) RecyclerView rcvUsuarios;

    private UsuarioPresenter usuarioPresenter;
    private CheckPermissao checkPermissaoLocalizacao;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procurar_usuarios);

        procurarContatosPresenter = new ProcurarUsuariosPresenter();
        usuarioPresenter = new UsuarioPresenter();
        checkPermissaoLocalizacao = new CheckPermissao(Manifest.permission.ACCESS_COARSE_LOCATION,
                getString(R.string.solicitar_permissao_localizacao));
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        onLoad();

        if (!checkPermissaoLocalizacao.checkPermissions(this)) {
            checkPermissaoLocalizacao.requestPermissions(this);
        }
        else {
            updateLocation();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void onLoad() {
        boolean defaultIdioma;

        ButterKnife.bind(this);

        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        rcvUsuarios.setHasFixedSize(true);
        rcvUsuarios.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UsuariosEncontradosAdapter(new ArrayList<Usuario>());
        adapter.setOnViewClick(new IClickListener() {
            @Override
            public void itemClicked(View view, int posicao) {
                DialogPerfilUsuario perfil = new DialogPerfilUsuario();
                perfil.show(getContext(), procurarContatosPresenter.getUsuarios().get(posicao));
            }
        });
        adapter.setOnChatClick(new IClickListener() {
            @Override
            public void itemClicked(View view, final int posicao) {
                final Usuario usuario = procurarContatosPresenter.getUsuarios().get(posicao);

                if (!usuario.isContato()) {
                    procurarContatosPresenter.adicionarContato(usuario, new ICallback() {
                        @Override
                        public void onSuccess() {
                            usuario.setContato(true);
                            adapter.updateItem(posicao);
                        }

                        @Override
                        public void onError(String mensagemErro) {

                        }
                    });
                }
                else {
                    ConversaPresenter conversaPresenter = new ConversaPresenter();

                    conversaPresenter.getIdConversa(usuario.getId(), new ConversaPresenter.ICallbackIdConversa() {
                        @Override
                        public void onSuccess(long idConversa) {
                            Intent i = new Intent(getContext(), MensagemActivity.class);
                            i.putExtra(Sistema.CONVERSA, idConversa);
                            i.putExtra(Sistema.CONTATO, usuario);
                            startActivity(i);
                        }
                    });
                }
            }
        });

        rcvUsuarios.setAdapter(adapter);

        idiomasManager = new FiltrosManager();
        for (ComboBoxItem item : Sistema.getListaIdiomas()) {
            defaultIdioma = item.getId() == Sistema.getUsuario().getIdioma();
            idiomasManager.addFiltro(item.getId(), item.getDescricao(), defaultIdioma, defaultIdioma);
        }

        for (Button button : idiomasManager.getFiltrosAsButton(this, lytIdiomas, getClickListener())) {
            lytIdiomas.addView(button);
        }

        fluenciasManager = new FiltrosManager();
        for (ComboBoxItem item : Sistema.getListaFluencias()) {
            fluenciasManager.addFiltro(item.getId(), item.getDescricao());
        }

        for (CheckBox check : fluenciasManager.getFiltrosAsCheckBox(this, lytFluencias)) {
            lytFluencias.addView(check);
        }

        lblUsuariosNaoEncontrados.setVisibility(View.GONE);
        btnMostrarFiltros.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (checkPermissaoLocalizacao.onRequestPermissionsResult(this, requestCode, permissions, grantResults)) {
            updateLocation();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void updateLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            usuarioPresenter.salvarLocalizacao(
                                    location.getLatitude(), location.getLongitude()
                            );
                        }
                    }
                });
    }

    @OnClick(R.id.btnProcurar)
    public void onProcurarClicked() {
        Pesquisa pesquisa = new Pesquisa();

        if (!checkPermissaoLocalizacao.checkPermissions(this)) {
            checkPermissaoLocalizacao.requestPermissions(this);
            return;
        }

        onPrgLoadingChanged(View.VISIBLE);

        pesquisa.setIdioma(idiomasManager.getSelecionados().toString().replace("[", "").replace("]", ""));
        pesquisa.setFluencia(fluenciasManager.getSelecionados().toString().replace("[", "").replace("]", ""));

        procurarContatosPresenter.procurarUsuarios(pesquisa, new ICallback() {
            @Override
            public void onSuccess() {
                adapter.limparDados();

                if (procurarContatosPresenter.getUsuarios().size() <= 0) {
                    showUsersNotFound(true);
                }
                else {
                    showUsersNotFound(false);
                    adapter.addItems(procurarContatosPresenter.getUsuarios());
                }

                trocarLayoutFiltros(false);
                onPrgLoadingChanged(View.INVISIBLE);
            }

            @Override
            public void onError(String mensagemErro) {
                showToast("Erro ao procurar usuarios");
                onPrgLoadingChanged(View.INVISIBLE);
            }
        });
    }

    @OnClick(R.id.btnLimpar)
    public void onLimparClicked() {
        idiomasManager.limparSelecionados(lytIdiomas);
        fluenciasManager.limparSelecionados(lytFluencias);
        btnAddFiltro.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.btnFiltrar, R.id.btnMostrarFiltros})
    public void onFiltrarClicked() {
        trocarLayoutFiltros(true);
    }

    @OnClick(R.id.btnAddFiltro)
    public void onAddFiltroClicked() {
        idiomasManager.selecionarItens(this, new ICallbackFinish() {
            @Override
            public void onFinish() {
                for (Button button : idiomasManager.getFiltrosAsButton(ProcurarUsuariosActivity.this, lytIdiomas, getClickListener())) {
                    lytIdiomas.addView(button);
                }

                if (idiomasManager.getCountNaoSelecionados() == 0) {
                    btnAddFiltro.setVisibility(View.GONE);
                }
            }
        });
    }

    private void onPrgLoadingChanged(int visibility) {
        pgbLoading.bringToFront();
        pgbLoading.setVisibility(visibility);
    }

    private void trocarLayoutFiltros(boolean expandido) {
        String fluencias;

        lytFiltroExpandido.setVisibility(expandido ? View.VISIBLE : View.GONE);
        lytFiltro.setVisibility(expandido ? View.GONE : View.VISIBLE);

        if (!expandido) {
            lblIdiomasEscolhidos.setText(idiomasManager.getDescricoesSelecionadas().toString()
                    .replace("[", "").replace("]", "").replace(", ", " · "));

            fluencias = fluenciasManager.getDescricoesSelecionadas().toString()
                    .replace("[", "").replace("]", "").replace(", ", " · ");

            if (fluencias.trim().length() == 0) {
                fluencias = getString(R.string.todos);
            }

            lblFluenciasEscolhidas.setText(fluencias);
        }
    }

    private void showUsersNotFound(boolean show) {
        lblUsuariosNaoEncontrados.setVisibility(show ? View.VISIBLE : View.GONE);
        btnMostrarFiltros.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private View.OnClickListener getClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddFiltro.setVisibility(View.VISIBLE);
            }
        };
    }
}
