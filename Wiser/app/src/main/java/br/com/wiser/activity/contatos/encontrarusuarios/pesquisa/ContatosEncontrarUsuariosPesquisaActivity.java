package br.com.wiser.activity.contatos.encontrarusuarios.pesquisa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import br.com.wiser.R;
import br.com.wiser.activity.contatos.encontrarusuarios.resultados.ChatResultadosActivity;

import br.com.wiser.utils.Utils;
import br.com.wiser.business.encontrarusuarios.pesquisa.PesquisaDAO;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Jefferson on 31/03/2016.
 */
public class ContatosEncontrarUsuariosPesquisaActivity extends Activity {

    private Spinner cmbIdioma;
    private Spinner cmbFluencia;
    private SeekBar skrDistancia;
    private Button btnProcurar;
    private ProgressBar pgbLoading;
    private TextView lblDistanciaSelecionada;

    private PesquisaDAO objProcurar;

    private static boolean achou = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contatos_encontrar_pessoas);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        carregarComponentes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void carregarComponentes() {
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
                procurar(v);
            }
        });

        pgbLoading = (ProgressBar) findViewById(R.id.pgbLoading);

        Utils.carregarComboIdiomas(cmbIdioma, this, true);
        Utils.carregarComboFluencia(cmbFluencia, this, true);

        objProcurar = new PesquisaDAO();
    }

    private void procurar (View view) {
        final Handler hCarregar = new Handler();

        pgbLoading.setVisibility(View.VISIBLE);
        pgbLoading.bringToFront();

        objProcurar.setIdioma(Utils.getIDComboBox(cmbIdioma));
        objProcurar.setFluencia(Utils.getIDComboBox(cmbFluencia));
        objProcurar.setDistancia(skrDistancia.getProgress());

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (objProcurar.procurarUsuarios(ContatosEncontrarUsuariosPesquisaActivity.this)){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("listaUsuarios", objProcurar.getListaResultados());

                    Intent i = new Intent(ContatosEncontrarUsuariosPesquisaActivity.this, ChatResultadosActivity.class);
                    i.putExtra("listaUsuarios", bundle);
                    startActivity(i);

                    achou = true;
                }

                hCarregar.post(new Runnable() {
                    @Override
                    public void run() {
                        pgbLoading.setVisibility(View.INVISIBLE);

                        if (!achou) {
                            Toast.makeText(ContatosEncontrarUsuariosPesquisaActivity.this, getString(R.string.usuarios_nao_encontrados), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }
}
