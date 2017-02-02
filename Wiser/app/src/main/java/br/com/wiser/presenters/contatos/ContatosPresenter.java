package br.com.wiser.presenters.contatos;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.APIClient;
import br.com.wiser.facebook.Facebook;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.models.contatos.Contato;
import br.com.wiser.models.conversas.Conversas;
import br.com.wiser.models.contatos.IContatosService;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.views.contatos.IContatosView;
import br.com.wiser.views.mensagens.MensagensActivity;
import br.com.wiser.views.procurarusuarios.ProcurarUsuariosActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 23/01/2017.
 */
public class ContatosPresenter extends Presenter<IContatosView> implements Observer {

    private IContatosService service;
    private ArrayList<Contato> listaContatos;

    @Override
    protected void onCreate() {
        super.onCreate();
        view.onInitView();

        service = APIClient.getClient().create(IContatosService.class);

        listaContatos = new ArrayList<>();
        carregarContatos();
    }

    private void carregarContatos() {

        Call<ArrayList<Contato>> call = service.carregarContatos(Sistema.getUsuario().getUserID());
        call.enqueue(new Callback<ArrayList<Contato>>() {
            @Override
            public void onResponse(Call<ArrayList<Contato>> call, Response<ArrayList<Contato>> response) {
                if (response.isSuccessful()) {
                    listaContatos = response.body();
                    carregarPerfis();
                }
                else {
                    view.showToast(getContext().getString(R.string.erro));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Contato>> call, Throwable t) {
                view.showToast(getContext().getString(R.string.erro));
            }
        });
    }

    private void carregarPerfis() {
        new Facebook(getContext()).carregarContatos(listaContatos, new ICallback() {

            @Override
            public void onSuccess() {
                view.onLoadListaContatos(listaContatos);

                for (Contato contato : listaContatos) {
                    try {
                        contato.getUsuario().addObserver(ContatosPresenter.this);
                    }
                    catch (Exception e) {
                        Log.e("Contatos", "Erro ao adicionar Observer. " + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(String mensagemErro) {
                Log.e("Carregar Contatos", mensagemErro);
                view.showToast(getContext().getString(R.string.erro));
            }
        });
    }

    public void startProcurarUsuarios() {
        getContext().startActivity(new Intent(getContext(), ProcurarUsuariosActivity.class));
    }

    public void startChat(int posicao) {
        Intent i = new Intent(getContext(), MensagensActivity.class);
        Conversas conversa = new Conversas();
        conversa.setDestinatario(listaContatos.get(posicao).getUsuario());

        i.putExtra(Sistema.CONVERSA, conversa);
        getContext().startActivity(i);
    }

    @Override
    public void update(Observable observable, Object data) {
        view.onNotifyDataSetChanged();
    }
}
