package br.com.wiser.features.principal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.AbstractAppCompatActivity;
import br.com.wiser.features.configuracoes.ConfiguracoesActivity;
import br.com.wiser.features.minhasdiscussoes.MinhasDiscussoesActivity;
import br.com.wiser.features.sobre.SobreActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PrincipalActivity extends AbstractAppCompatActivity {

    private PrincipalTabs adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    @BindView(R.id.snackbar_view) View snackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_principal);

        ButterKnife.bind(this);
        onInitView();

        if (Sistema.getUsuario().isSetouConfiguracoes()) {
            showSnackBar();
        }
        else {
            startConfiguracoesActivity();
        }
    }

    private void showSnackBar() {
        try {
            Picasso.with(getContext())
                    .load(Sistema.getUsuario().getUrlFotoPerfil())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            showSnackBarImage(snackBar,
                                    getString(R.string.boas_vindas, Sistema.getUsuario().getPrimeiroNome()),
                                    bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) { }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) { }
                    });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return (true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itmMinhasDiscussoes:
                startMinhasDiscussoesActivity();
                break;

            case R.id.itmConfiguracoes:
                startConfiguracoesActivity();
                break;

            case R.id.itmSobre:
                startSobreActivity();
                break;

            case R.id.itmSair:
                Sistema.logout(this);
                break;
        }
        return (true);
    }

    private void startMinhasDiscussoesActivity() {
        startActivity(new Intent(this, MinhasDiscussoesActivity.class));
    }

    private void startConfiguracoesActivity() {
        startActivity(new Intent(this, ConfiguracoesActivity.class));
    }

    private void startSobreActivity() {
        startActivity(new Intent(this, SobreActivity.class));
    }

    public void onInitView() {
        setSupportActionBar(toolbar);

        adapter = new PrincipalTabs(getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(1);
    }
}
