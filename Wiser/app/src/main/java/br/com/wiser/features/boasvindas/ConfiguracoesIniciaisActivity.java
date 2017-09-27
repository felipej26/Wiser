package br.com.wiser.features.boasvindas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;

import br.com.wiser.AbstractAppCompatActivity;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.configuracoes.Configuracoes;
import br.com.wiser.features.configuracoes.ConfiguracoesPresenter;
import br.com.wiser.features.principal.PrincipalActivity;
import br.com.wiser.interfaces.ICallbackFinish;
import br.com.wiser.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfiguracoesIniciaisActivity extends AbstractAppCompatActivity {

    private ConfiguracoesIniciaisAdapter adapter;

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.indicator) CirclePageIndicator indicator;

    @BindView(R.id.btnFeito) Button btnFeito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_iniciais);

        ButterKnife.bind(this);
        onLoad();
    }

    private void onLoad() {
        adapter = new ConfiguracoesIniciaisAdapter(getSupportFragmentManager(), new ICallbackFinish() {
            @Override
            public void onFinish() {
                btnFeito.setVisibility(View.VISIBLE);
            }
        });

        viewPager.setAdapter(adapter);

        indicator.setViewPager(viewPager);
        indicator.setSnap(true);

        btnFeito.setVisibility(View.GONE);
    }

    @OnClick(R.id.btnFeito)
    public void onFeitoClicked() {
        salvarConfiguracoes();

        Intent i = new Intent(this, PrincipalActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void salvarConfiguracoes() {
        ConfiguracoesPresenter configuracoesPresenter = new ConfiguracoesPresenter();
        Configuracoes configuracoes = new Configuracoes();

        configuracoes.setId(Sistema.getUsuario().getId());
        configuracoes.setIdioma(adapter.getLanguageChoosed());
        configuracoes.setFluencia(adapter.getFluencyChoosed());
        configuracoes.setStatus(Utils.encode(""));

        configuracoesPresenter.salvar(configuracoes);
    }
}
