package br.com.wiser.features.procurarusuarios;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.wiser.AbstractActivity;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.usuariosencontrados.UsuariosEncontradosActivity;
import br.com.wiser.interfaces.ICallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jefferson on 31/03/2016.
 */
public class ProcurarUsuariosActivity extends AbstractActivity {

    private ProcurarUsuariosPresenter procurarContatosPresenter;

    @BindView(R.id.cmbIdiomaProcurar) Spinner cmbIdioma;
    @BindView(R.id.cmbFluenciaProcurar) Spinner cmbFluencia;
    @BindView(R.id.skrDistancia) SeekBar skrDistancia;
    @BindView(R.id.btnProcurar) Button btnProcurar;
    @BindView(R.id.pgbLoading) ProgressBar pgbLoading;
    @BindView(R.id.lblDistanciaSelecionada) TextView lblDistanciaSelecionada;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contatos_encontrar_pessoas);

        procurarContatosPresenter = new ProcurarUsuariosPresenter();

        ButterKnife.bind(this);
        onInitView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void onInitView() {
        getActionBar().setDisplayHomeAsUpEnabled(true);

        skrDistancia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                lblDistanciaSelecionada.setText(progressValue + " Km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        Sistema.carregarComboIdiomas(cmbIdioma, this, true);
        Sistema.carregarComboFluencia(cmbFluencia, this, true);
    }

    @OnClick(R.id.btnProcurar)
    public void onProcurarClicked() {
        Pesquisa pesquisa = new Pesquisa();

        onPrgLoadingChanged(View.VISIBLE);

        pesquisa.setIdioma(Sistema.getIDComboBox(cmbIdioma));
        pesquisa.setFluencia(Sistema.getIDComboBox(cmbFluencia));
        pesquisa.setDistancia(skrDistancia.getProgress());

        procurarContatosPresenter.procurarUsuarios(pesquisa, new ICallback() {
            @Override
            public void onSuccess() {

                if (procurarContatosPresenter.getUsuarios().size() <= 0) {
                    showToast(getString(R.string.usuarios_nao_encontrados));
                }
                else {
                    startUsuariosEncontradosActivity();
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

    public void onPrgLoadingChanged(int visibility) {
        if (visibility == View.VISIBLE) {
            pgbLoading.bringToFront();
        }

        pgbLoading.setVisibility(visibility);
    }

    private void startUsuariosEncontradosActivity() {
        Bundle bundle = new Bundle();
        Intent i = new Intent(this, UsuariosEncontradosActivity.class);

        bundle.putSerializable(Sistema.LISTAUSUARIOS, (ArrayList) procurarContatosPresenter.getUsuarios());
        i.putExtra(Sistema.LISTAUSUARIOS, bundle);
        startActivity(i);
    }
}
