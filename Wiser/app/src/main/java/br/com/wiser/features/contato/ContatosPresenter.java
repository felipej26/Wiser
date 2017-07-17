package br.com.wiser.features.contato;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import br.com.wiser.APIClient;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.features.mensagem.MensagemActivity;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.features.usuario.UsuarioDAO;
import br.com.wiser.features.usuario.UsuarioPresenter;
import br.com.wiser.presenters.Presenter;
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

    /*
    public void onResume(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                view.onNotifyDataSetChanged();
            }
        }, 1500);
    }
    */

    private void carregarContatos() {
        final Set<Long> idsUsuariosParaCarregar = new HashSet<>();

        Call<ArrayList<Contato>> call = service.carregarContatos(Sistema.getUsuario().getId());
        call.enqueue(new Callback<ArrayList<Contato>>() {
            @Override
            public void onResponse(Call<ArrayList<Contato>> call, Response<ArrayList<Contato>> response) {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                UsuarioPresenter usuarioPresenter = new UsuarioPresenter();

                if (response.isSuccessful()) {
                    listaContatos = response.body();
                    for (Contato contato : listaContatos) {
                        if (usuarioDAO.exist(contato.getIdContato())) {
                            contato.setDestinatario(usuarioDAO.getById(contato.getIdContato()));
                        }
                        else {
                            idsUsuariosParaCarregar.add(contato.getIdContato());
                        }
                    }

                    if (idsUsuariosParaCarregar.size() > 0) {
                        usuarioPresenter.getInServer(idsUsuariosParaCarregar, new UsuarioPresenter.ICallback() {
                            @Override
                            public void onFinished(List<Usuario> usuarios) {
                                view.onLoadListaContatos(listaContatos);
                                view.onNotifyDataSetChanged();
                            }
                        });
                    }
                }
                else {
                    view.showToast(getContext().getString(R.string.erro));
                    Log.e("Contatos", "Erro ao carregar os Contatos. " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Contato>> call, Throwable t) {
                view.showToast(getContext().getString(R.string.erro));
                Log.e("Contatos", "Erro ao carregar os Contatos", t);
            }
        });
    }

    public void startProcurarUsuarios() {
        getContext().startActivity(new Intent(getContext(), ProcurarUsuariosActivity.class));
    }

    public void startChat(int posicao) {
        Intent i = new Intent(getContext(), MensagemActivity.class);
        Conversa conversa = new Conversa();
        conversa.setUsuario(listaContatos.get(posicao).getDestinatario());

        i.putExtra(Sistema.CONVERSA, conversa);
        getContext().startActivity(i);
    }

    @Override
    public void update(Observable observable, Object data) {
        view.onNotifyDataSetChanged();
    }
}
