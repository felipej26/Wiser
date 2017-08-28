package br.com.wiser.features.sobre;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.wiser.R;
import br.com.wiser.AbstractActivity;

public class SobreActivity extends AbstractActivity implements ISobreView {

    private SobrePresenter sobrePresenter;
    private TextView lblVersao;

    private Button btnCompartilhar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_sobre);

        sobrePresenter = new SobrePresenter();
        sobrePresenter.onCreate(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onInitView() {
        lblVersao = (TextView) findViewById(R.id.lblVersao);
        lblVersao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sobrePresenter.compartilharApp();
            }
        });

        btnCompartilhar = (Button) findViewById(R.id.btnCompartilhar);
        btnCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sobrePresenter.compartilharApp();
            }
        });

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSetTextLblVersao(String versao){
        lblVersao.setText(versao);
    }
}
