package br.com.wiser.features.novadiscussao;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.com.wiser.AbstractActivity;
import br.com.wiser.R;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.dialogs.DialogInformar;
import br.com.wiser.dialogs.IDialog;
import br.com.wiser.features.minhasdiscussoes.MinhasDiscussoesActivity;
import br.com.wiser.interfaces.ICallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class NovaDiscussaoActivity extends AbstractActivity {

    private NovaDiscussaoPresenter novaDiscussaoPresenter;

    @BindView(R.id.txtTituloDiscussao) EditText txtTituloDiscussao;
    @BindView(R.id.txtDescricaoDiscussao) EditText txtDescricaoDiscussao;
    @BindView(R.id.lblContTitulo) TextView lblContTitulo;
    @BindView(R.id.lblContDescricao) TextView lblContDescricao;
    @BindView(R.id.btnCriarDiscussao) Button btnCriarDiscussao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_nova_discussao);

        novaDiscussaoPresenter = new NovaDiscussaoPresenter();

        onLoad();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void onLoad() {
        ButterKnife.bind(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnTextChanged(value = R.id.txtTituloDiscussao, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onTituloChanged(Editable editable) {
        lblContTitulo.setText(String.valueOf(editable.length()) + " / 30");
    }

    @OnTextChanged(value = R.id.txtTituloDiscussao, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onDescricaoChanged(Editable editable) {
        lblContDescricao.setText(String.valueOf(editable.length()) + " / 30");
    }

    @OnClick(R.id.btnCriarDiscussao)
    public void onCriarDiscussaoClicked() {

        DialogConfirmar confirmar = new DialogConfirmar();
        DialogInformar informar = new DialogInformar(this);

        final String titulo = txtTituloDiscussao.getText().toString();
        final String descricao = txtDescricaoDiscussao.getText().toString();

        if (titulo.trim().isEmpty() || descricao.trim().isEmpty()) {
            informar.setMensagem(getString(R.string.erro_criar_discussao_campos));
            informar.show();
            return;
        }

        confirmar.setMensagem(getString(R.string.confirmar_salvar));
        confirmar.setYesClick(new IDialog() {
            @Override
            public void onClick() {
                novaDiscussaoPresenter.salvarDiscussao(titulo, descricao, new ICallback() {
                    @Override
                    public void onSuccess() {
                        DialogInformar informar = new DialogInformar(NovaDiscussaoActivity.this);
                        informar.setMensagem(getString(R.string.sucesso_criar_discussao));
                        informar.setOkClick(new IDialog() {
                            @Override
                            public void onClick() {
                                Intent intent = new Intent(NovaDiscussaoActivity.this, MinhasDiscussoesActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        });
                        informar.show();
                    }

                    @Override
                    public void onError(String mensagemErro) {
                        showToast(getString(R.string.erro_criar_discussao));
                    }
                });

            }
        });
        confirmar.show(this);
    }
}
