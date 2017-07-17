package br.com.wiser.views.procurarusuarios;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import br.com.wiser.R;

import br.com.wiser.Sistema;
import br.com.wiser.models.procurarusuarios.Pesquisa;
import br.com.wiser.presenters.procurarusuarios.ProcurarUsuariosPresenter;
import br.com.wiser.views.AbstractActivity;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Jefferson on 31/03/2016.
 */
public class ProcurarUsuariosActivity extends AbstractActivity implements IProcurarUsuariosView {

    private ProcurarUsuariosPresenter procurarContatosPresenter;

    private Spinner cmbIdioma;
    private Spinner cmbFluencia;
    private SeekBar skrDistancia;
    private Button btnProcurar;
    private ProgressBar pgbLoading;
    private TextView lblDistanciaSelecionada;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contatos_encontrar_pessoas);

        procurarContatosPresenter = new ProcurarUsuariosPresenter();
        procurarContatosPresenter.onCreate(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onInitView() {
        getActionBar().setDisplayHomeAsUpEnabled(true);

        cmbIdioma = (Spinner) findViewById(R.id.cmbIdiomaProcurar);
        cmbFluencia = (Spinner) findViewById(R.id.cmbFluenciaProcurar);

        skrDistancia = (SeekBar) findViewById(R.id.skrDistancia);
        lblDistanciaSelecionada = (TextView) findViewById(R.id.lblDistanciaSelecionada);

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

        btnProcurar = (Button) findViewById(R.id.btnProcurar);
        btnProcurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procurarContatos();
            }
        });

        pgbLoading = (ProgressBar) findViewById(R.id.pgbLoading);

        Sistema.carregarComboIdiomas(cmbIdioma, this, true);
        Sistema.carregarComboFluencia(cmbFluencia, this, true);
    }

    @Override
    public void onSetVisibilityProgressBar(int visibility) {
        pgbLoading.setVisibility(visibility);

        if (visibility == View.VISIBLE) {
            pgbLoading.bringToFront();
        }
    }

    private void procurarContatos() {
        Pesquisa pesquisa = new Pesquisa();

        pesquisa.setIdioma(Sistema.getIDComboBox(cmbIdioma));
        pesquisa.setFluencia(Sistema.getIDComboBox(cmbFluencia));
        pesquisa.setDistancia(skrDistancia.getProgress());

        procurarContatosPresenter.procurarUsuarios(pesquisa);
    }
}
