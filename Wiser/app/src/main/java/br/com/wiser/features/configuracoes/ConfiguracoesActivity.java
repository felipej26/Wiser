package br.com.wiser.features.configuracoes;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.dialogs.DialogInformar;
import br.com.wiser.dialogs.IDialog;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.utils.ComboBoxItem;
import br.com.wiser.utils.Utils;
import br.com.wiser.views.AbstractActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ConfiguracoesActivity extends AbstractActivity {

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

        onInitView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        salvar();
        return true;
    }

    private void onInitView() {
        ButterKnife.bind(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Sistema.carregarComboIdiomas(cmbIdioma, ConfiguracoesActivity.this, false);
        Sistema.carregarComboFluencia(cmbFluencia, ConfiguracoesActivity.this, false);

        cmbIdioma.setSelection(Sistema.getPosicaoItemComboBox(cmbIdioma, Sistema.getUsuario().getIdioma()));
        cmbFluencia.setSelection(Sistema.getPosicaoItemComboBox(cmbFluencia, Sistema.getUsuario().getFluencia()));
        txtStatus.setText(Utils.decode(Sistema.getUsuario().getStatus()));
        lblContLetras.setText(txtStatus.length() + " / 30");
    }

    @OnClick(R.id.btnDesativar)
    public void onDesableClicked() {
        confirmarDesativarConta();
    }

    @OnTextChanged(value = R.id.txtStatus, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onStatusTextChanged(Editable editable) {
        txtStatus.setSelection(editable.length());
    }

    private void salvar() {
        Configuracoes configuracoes = new Configuracoes();

        configuracoes.setId(Sistema.getUsuario().getId());
        configuracoes.setIdioma(((ComboBoxItem) cmbIdioma.getItemAtPosition(cmbIdioma.getSelectedItemPosition())).getId());
        configuracoes.setFluencia(((ComboBoxItem) cmbFluencia.getItemAtPosition(cmbFluencia.getSelectedItemPosition())).getId());
        configuracoes.setStatus(Utils.encode(txtStatus.getText().toString()));

        configuracoesPresenter.salvar(configuracoes);
    }

    private void confirmarDesativarConta() {
        DialogConfirmar confirmar = new DialogConfirmar(this);

        confirmar.setMensagem(getString(R.string.confirmar_desativar_conta));
        confirmar.setYesClick(new IDialog() {
            @Override
            public void onClick() {
                desativarConta();
            }
        });

        confirmar.show();
    }

    private void desativarConta() {
        final DialogInformar informar = new DialogInformar(this);

        configuracoesPresenter.desativarConta(Sistema.getUsuario().getId(), new ICallback() {
            @Override
            public void onSuccess() {
                informar.setMensagem(getString(R.string.sucesso_conta_desativada));
                informar.setOkClick(new IDialog() {
                    @Override
                    public void onClick() {
                        Sistema.logout(ConfiguracoesActivity.this);
                    }
                });
                informar.show();
            }

            @Override
            public void onError(String mensagemErro) {
                Log.e("Desativar Conta", mensagemErro);
                informar.setMensagem(getString(R.string.erro_desativar_conta));
                informar.show();
            }
        });
    }
}
