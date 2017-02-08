package br.com.wiser.presenters.principal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.views.minhasdiscussoes.MinhasDiscussoesActivity;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.views.configuracoes.ConfiguracoesActivity;
import br.com.wiser.views.principal.IPrincipalView;
import br.com.wiser.views.sobre.SobreActivity;

/**
 * Created by Jefferson on 23/01/2017.
 */
public class PrincipalPresenter extends Presenter<IPrincipalView> {

    @Override
    protected void onCreate() {
        super.onCreate();
        view.onInitView();

        if (!Sistema.getUsuario().isSetouConfiguracoes()) {
            startConfiguracoesActivity();
        }

        showSnackBar();
    }

    public void setMenuItemSelected(int itemId) {
        switch (itemId) {
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
                Sistema.logout(view.getActivity());
                break;
        }
    }

    private void startMinhasDiscussoesActivity() {
        getContext().startActivity(new Intent(getContext(), MinhasDiscussoesActivity.class));
    }

    private void startConfiguracoesActivity() {
        getContext().startActivity(new Intent(getContext(), ConfiguracoesActivity.class));
    }

    private void startSobreActivity() {
        getContext().startActivity(new Intent(getContext(), SobreActivity.class));
    }

    private void showSnackBar() {
        try {
            Picasso.with(getContext())
                    .load(Sistema.getUsuario().getPerfil().getUrlProfilePicture())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            view.showSnackBarImage(view.onGetSnackBarView(),
                                    view.getContext().getString(R.string.boas_vindas, Sistema.getUsuario().getPerfil().getFirstName()),
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
}
