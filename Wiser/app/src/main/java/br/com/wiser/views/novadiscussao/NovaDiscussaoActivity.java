package br.com.wiser.views.novadiscussao;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.com.wiser.R;
import br.com.wiser.presenters.novadiscussao.NovaDiscussaoPresenter;
import br.com.wiser.views.AbstractActivity;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class NovaDiscussaoActivity extends AbstractActivity implements INovaDiscussaoView {

    private NovaDiscussaoPresenter novaDiscussaoPresenter;

    private EditText txtTituloDiscussao;
    private EditText txtDescricaoDiscussao;

    private TextView lblContTitulo;
    private TextView lblContDescricao;

    private Button btnCriarDiscussao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_nova_discussao);

        novaDiscussaoPresenter = new NovaDiscussaoPresenter();
        novaDiscussaoPresenter.onCreate(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onInitView() {
        getActionBar().setDisplayHomeAsUpEnabled(true);

        lblContTitulo = (TextView) findViewById(R.id.lblContTitulo);
        lblContDescricao = (TextView) findViewById(R.id.lblContDescricao);

        txtTituloDiscussao = (EditText) findViewById(R.id.txtTituloDiscussao);
        txtTituloDiscussao.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void afterTextChanged(Editable s) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                novaDiscussaoPresenter.setTextChangedLblTitulo(s.length());
                lblContTitulo.setText(String.valueOf(s.length()) + " / 30");
            }
        });

        txtDescricaoDiscussao = (EditText) findViewById(R.id.txtDescricaoDiscussao);
        txtDescricaoDiscussao.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void afterTextChanged(Editable s) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                novaDiscussaoPresenter.setTextChangedLblDescricao(s.length());
            }
        });

        btnCriarDiscussao = (Button) findViewById(R.id.btnCriarDiscussao);
        btnCriarDiscussao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                novaDiscussaoPresenter.criarNovaDiscussao(txtTituloDiscussao.getText().toString(), txtDescricaoDiscussao.getText().toString());
            }
        });

    }

    @Override
    public void onSetTextLblContTitulo(String texto) {
        lblContTitulo.setText(texto);
    }

    @Override
    public void onSetTextLblContDescricao(String texto) {
        lblContDescricao.setText(texto);
    }
}
