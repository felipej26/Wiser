package br.com.wiser.views.configuracoes;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.models.configuracoes.Configuracoes;
import br.com.wiser.presenters.configuracoes.ConfiguracoesPresenter;
import br.com.wiser.views.AbstractActivity;
import br.com.wiser.utils.ComboBoxItem;
import br.com.wiser.utils.Utils;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ConfiguracoesActivity extends AbstractActivity implements IConfiguracoesView {

    private ConfiguracoesPresenter configuracoesPresenter;

    private Spinner cmbIdioma;
    private Spinner cmbFluencia;
    private TextView lblContLetras;
    private EditText txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_configuracoes);

        configuracoesPresenter = new ConfiguracoesPresenter();
        configuracoesPresenter.onCreate(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        salvar();
        return true;
    }

    @Override
    public void onInitView() {
        getActionBar().setDisplayHomeAsUpEnabled(true);

        cmbIdioma = (Spinner) findViewById(R.id.cmbIdiomaConfig);
        cmbFluencia = (Spinner) findViewById(R.id.cmbFluenciaConfig);

        lblContLetras = (TextView) findViewById(R.id.lblContLetras);
        txtStatus = (EditText) findViewById(R.id.txtStatus);
        txtStatus.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void afterTextChanged(Editable s) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                configuracoesPresenter.setTextChangedTxtStatus(s.length());
            }
        });

        Sistema.carregarComboIdiomas(cmbIdioma, ConfiguracoesActivity.this, false);
        Sistema.carregarComboFluencia(cmbFluencia, ConfiguracoesActivity.this, false);
    }

    @Override
    public Spinner getCmbIdioma() {
        return cmbIdioma;
    }

    @Override
    public Spinner getCmbFluencia() {
        return cmbFluencia;
    }

    @Override
    public String getTextTxtStatus() {
        return txtStatus.getText().toString();
    }

    @Override
    public void onSetSelectionCmbIdioma(int posicao) {
        cmbIdioma.setSelection(posicao);
    }

    @Override
    public void onSetSelectionCmbFluencia(int posicao) {
        cmbFluencia.setSelection(posicao);
    }

    @Override
    public void onSetSelectionTxtStatus(int tamanho) {
        txtStatus.setSelection(tamanho);
    }

    @Override
    public void onSetTextTxtStatus(String status) {
        txtStatus.setText(status);
    }

    @Override
    public void onSetTextLblContLetras(String contLetras) {
        lblContLetras.setText(contLetras);
    }

    @Override
    public int onGetItemIdCmbIdioma() {
        ComboBoxItem comboBoxItem = (ComboBoxItem) cmbIdioma.getItemAtPosition(cmbIdioma.getSelectedItemPosition());
        return comboBoxItem.getId();
    }

    @Override
    public int onGetItemIdCmbFluencia() {
        ComboBoxItem comboBoxItem = (ComboBoxItem) cmbFluencia.getItemAtPosition(cmbFluencia.getSelectedItemPosition());
        return comboBoxItem.getId();
    }

    @Override
    public String onGetTextTxtStatus() {
        return txtStatus.getText().toString();
    }

    public void salvar() {
        configuracoesPresenter.salvar();
    }

    public void desativar(View view){
        configuracoesPresenter.confirmarDesativarConta();
    }
}
