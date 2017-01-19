package br.com.wiser.activity.app.configuracoes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;

import br.com.wiser.Sistema;
import br.com.wiser.R;
import br.com.wiser.business.app.usuario.UsuarioDAO;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.dialogs.DialogInformar;
import br.com.wiser.dialogs.IDialog;
import br.com.wiser.utils.ComboBoxItem;
import br.com.wiser.utils.Utils;

import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AppConfiguracoesActivity extends Activity {

    private Spinner cmbIdioma;
    private Spinner cmbFluencia;
    private TextView lblContLetras;
    private EditText txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_configuracoes);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        carregarComponentes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        salvar();
        return true;
    }

    private void carregarComponentes() {
        cmbIdioma = (Spinner) findViewById(R.id.cmbIdiomaConfig);
        cmbFluencia = (Spinner) findViewById(R.id.cmbFluenciaConfig);

        lblContLetras = (TextView) findViewById(R.id.lblContLetras);
        txtStatus = (EditText) findViewById(R.id.txtStatus);
        txtStatus.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void afterTextChanged(Editable s) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lblContLetras.setText(String.valueOf(s.length()) + " / 30");
            }
        });

        Utils.carregarComboIdiomas(cmbIdioma, AppConfiguracoesActivity.this, false);
        Utils.carregarComboFluencia(cmbFluencia, AppConfiguracoesActivity.this, false);

        carregarDados();
    }

    private void carregarDados() {
        if (Sistema.getUsuario(this).isSetouConfiguracoes()) {
            cmbIdioma.setSelection(Utils.getPosicaoItemComboBox(cmbIdioma, Sistema.getUsuario(this).getIdioma()));
            cmbFluencia.setSelection(Utils.getPosicaoItemComboBox(cmbFluencia, Sistema.getUsuario(this).getFluencia()));
            txtStatus.setText(Sistema.getUsuario(this).getStatus());
            txtStatus.setSelection(txtStatus.getText().length());
        }
    }

    private void salvar(){
        UsuarioDAO usuario = new UsuarioDAO(Sistema.getUsuario(this).getUserID());

        ComboBoxItem comboBoxItem = (ComboBoxItem)cmbIdioma.getItemAtPosition(cmbIdioma.getSelectedItemPosition());
        usuario.setIdioma(comboBoxItem.getId());

        comboBoxItem = (ComboBoxItem)cmbFluencia.getItemAtPosition(cmbFluencia.getSelectedItemPosition());
        usuario.setFluencia(comboBoxItem.getId());

        usuario.setStatus(txtStatus.getText().toString());

        if (usuario.salvarConfiguracoes(this)) {
            Sistema.getUsuario(this).setIdioma(usuario.getIdioma());
            Sistema.getUsuario(this).setFluencia(usuario.getFluencia());
            Sistema.getUsuario(this).setStatus(usuario.getStatus());
            Sistema.getUsuario(this).setSetouConfiguracoes(true);
        }
        else {
            Toast.makeText(AppConfiguracoesActivity.this, getString(R.string.erro_salvar_configuracao), Toast.LENGTH_SHORT).show();
        }
    }

    public void desativar(View view){
        DialogConfirmar confirmar = new DialogConfirmar(this);

        confirmar.setMensagem(getString(R.string.confirmar_desativar_conta));
        confirmar.setYesClick(new IDialog() {
            @Override
            public void onClick() {
                desativar();
            }
        });

        confirmar.show();
    }

    private void desativar() {
        DialogInformar informar = new DialogInformar(this);

        if (Sistema.getUsuario(this).desativarConta(this)) {
            informar.setMensagem(getString(R.string.sucesso_conta_desativada));
            informar.setOkClick(new IDialog() {
                @Override
                public void onClick() {
                    Utils.logout(AppConfiguracoesActivity.this);
                }
            });
        }
        else {
            informar.setMensagem(getString(R.string.erro_desativar_conta));
        }

        informar.show();
    }
}
