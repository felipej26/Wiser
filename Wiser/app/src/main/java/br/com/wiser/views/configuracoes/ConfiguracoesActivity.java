package br.com.wiser.views.configuracoes;

import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.presenters.configuracoes.ConfiguracoesPresenter;
import br.com.wiser.utils.ComboBoxItem;
import br.com.wiser.views.AbstractActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ConfiguracoesActivity extends AbstractActivity implements IConfiguracoesView {

    private ConfiguracoesPresenter configuracoesPresenter;

    @BindView(R.id.cmbIdiomaConfig) Spinner cmbIdioma;
    @BindView(R.id.cmbFluenciaConfig) Spinner cmbFluencia;
    @BindView(R.id.lblContLetras) TextView lblContLetras;
    @BindView(R.id.txtStatus) EditText txtStatus;
    @BindView(R.id.btnDesativar) Button btnDesativar;

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
        configuracoesPresenter.salvar();
        return true;
    }

    @Override
    public void onInitView() {
        ButterKnife.bind(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Sistema.carregarComboIdiomas(cmbIdioma, ConfiguracoesActivity.this, false);
        Sistema.carregarComboFluencia(cmbFluencia, ConfiguracoesActivity.this, false);
    }

    @Override
    public void onSetSelectionCmbIdioma(int idioma) {
        cmbIdioma.setSelection(Sistema.getPosicaoItemComboBox(cmbIdioma, idioma));
    }

    @Override
    public void onSetSelectionCmbFluencia(int fluencia) {
        cmbFluencia.setSelection(Sistema.getPosicaoItemComboBox(cmbFluencia, fluencia));
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

    @OnClick(R.id.btnDesativar)
    public void onDesableClicked() {
        configuracoesPresenter.confirmarDesativarConta();
    }

    @OnTextChanged(value = R.id.txtStatus, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onStatusTextChanged(Editable editable) {
        configuracoesPresenter.setTextChangedTxtStatus(editable.length());
    }
}
