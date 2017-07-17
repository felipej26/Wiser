package br.com.wiser.presenters.procurarusuarios;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.APIClient;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.models.procurarusuarios.Pesquisa;
import br.com.wiser.models.procurarusuarios.IProcurarUsuariosService;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.views.procurarusuarios.IProcurarUsuariosView;
import br.com.wiser.views.usuariosencontrados.UsuariosEncontradosActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 23/01/2017.
 */
public class ProcurarUsuariosPresenter extends Presenter<IProcurarUsuariosView> {

    private IProcurarUsuariosService service;
    private LinkedList<Usuario> listaUsuarios;

    @Override
    protected void onCreate() {
        super.onCreate();
        view.onInitView();

        service = APIClient.getClient().create(IProcurarUsuariosService.class);
    }

    public void procurarUsuarios(Pesquisa pesquisa) {
        listaUsuarios = new LinkedList<>();

        view.onSetVisibilityProgressBar(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("usuario", String.valueOf(Sistema.getUsuario().getId()));
        map.put("latitude", String.valueOf(Sistema.getUsuario().getLatitude()));
        map.put("longitude", String.valueOf(Sistema.getUsuario().getLongitude()));
        map.put("distancia", String.valueOf(pesquisa.getDistancia()));

        if (pesquisa.getIdioma() > 0)
            map.put("idioma", String.valueOf(pesquisa.getIdioma()));

        if (pesquisa.getFluencia() > 0)
            map.put("fluencia", String.valueOf(pesquisa.getFluencia()));

        Call<LinkedList<Usuario>> call = service.procurarUsuarios(map);
        call.enqueue(new Callback<LinkedList<Usuario>>() {
            @Override
            public void onResponse(Call<LinkedList<Usuario>> call, Response<LinkedList<Usuario>> response) {
                if (response.isSuccessful()) {
                    listaUsuarios = response.body();

                    if (listaUsuarios.size() == 0) {
                        view.showToast(view.getContext().getString(R.string.usuarios_nao_encontrados));
                        view.onSetVisibilityProgressBar(View.INVISIBLE);
                        return;
                    }

                    startUsuariosEncontradosActivity();
                }
                else {
                    Log.e("Procurar Usuarios", response.message());
                    view.showToast(view.getContext().getString(R.string.erro));
                }

                view.onSetVisibilityProgressBar(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<LinkedList<Usuario>> call, Throwable t) {
                Log.e("Procurar Usuarios", t.getMessage(), t);
                view.showToast(view.getContext().getString(R.string.erro));
                view.onSetVisibilityProgressBar(View.INVISIBLE);
            }
        });
    }

    private void startUsuariosEncontradosActivity() {
        Bundle bundle = new Bundle();
        Intent i = new Intent(getContext(), UsuariosEncontradosActivity.class);

        bundle.putSerializable(Sistema.LISTAUSUARIOS, listaUsuarios);
        i.putExtra(Sistema.LISTAUSUARIOS, bundle);
        getContext().startActivity(i);
    }
}
