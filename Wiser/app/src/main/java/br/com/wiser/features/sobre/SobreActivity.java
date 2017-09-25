package br.com.wiser.features.sobre;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import br.com.wiser.AbstractAppCompatActivity;
import br.com.wiser.R;
import br.com.wiser.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SobreActivity extends AbstractAppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.lblVersao) TextView lblVersao;
    @BindView(R.id.btnCompartilhar) Button btnCompartilhar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_sobre);

        ButterKnife.bind(this);
        onLoad();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void onLoad() {

        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            lblVersao.setText(getString(R.string.app_sobre_item_versao,
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnCompartilhar)
    public void onCompatilharClicked() {
        Utils.compartilharAppComoTexto(this);
    }
}
