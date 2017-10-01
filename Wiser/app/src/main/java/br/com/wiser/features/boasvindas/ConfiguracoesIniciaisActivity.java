package br.com.wiser.features.boasvindas;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.os.Build;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfiguracoesIniciaisActivity extends AbstractAppCompatActivity {

    private ConfiguracoesIniciaisAdapter adapter;

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.animated_color) View animated_color;
    @BindView(R.id.indicator) CirclePageIndicator indicator;

    @BindView(R.id.btnFeito) Button btnFeito;

    private int[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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

        setUpColors();
        animated_color.setBackgroundColor(colors[0]);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new CustomOnPageChangeListener());

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
        configuracoes.setStatus("");

        configuracoesPresenter.salvar(configuracoes);
    }

    private void setUpColors() {
        colors = new int[] {
                getResourceColor(R.color.colorPrimary),
                getResourceColor(R.color.colorAccent),
                getResourceColor(R.color.colorPrimaryDark)
        };
    }

    public int getResourceColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(color);
        }
        else {
            return getResources().getColor(color);
        }
    }

    private class CustomOnPageChangeListener implements ViewPager.OnPageChangeListener {

        private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position < (adapter.getCount() - 1) && position < (colors.length - 1)) {
                viewPager.setBackgroundColor(
                        (Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1])
                );
            }
            else {
                viewPager.setBackgroundColor(colors[colors.length - 1]);
            }
        }

        @Override
        public void onPageSelected(int position) { }

        @Override
        public void onPageScrollStateChanged(int state) { }
    }
}
