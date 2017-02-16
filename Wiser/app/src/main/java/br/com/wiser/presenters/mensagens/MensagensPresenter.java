package br.com.wiser.presenters.mensagens;

import android.util.Log;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import br.com.wiser.Sistema;
import br.com.wiser.APIClient;
import br.com.wiser.facebook.Facebook;
import br.com.wiser.facebook.ICallbackPaginas;
import br.com.wiser.models.assunto.Assunto;
import br.com.wiser.models.assunto.Pagina;
import br.com.wiser.models.conversas.Conversas;
import br.com.wiser.models.mensagens.Mensagem;
import br.com.wiser.dialogs.DialogSugestoes;
import br.com.wiser.models.mensagens.IMensagensService;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.utils.Utils;
import br.com.wiser.views.mensagens.IMensagensView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class MensagensPresenter extends Presenter<IMensagensView> {

    private IMensagensService service;
    private Conversas conversa;

    private long qntdMensagens = 0;

    public void onCreate(IMensagensView view, Conversas conversa) {
        super.onCreate(view);
        view.onInitView();

        service = APIClient.getClient().create(IMensagensService.class);
        this.conversa = conversa;

        view.onSetTitleActionBar(Sistema.getListaUsuarios().get(conversa.getDestinatario()).getPerfil().getFullName());

        carregarMensagens();

        if (!conversa.isCarregouSugestoes()) {
            view.onSetVisibilityBtnSugestoes(View.INVISIBLE);
            carregarSugestoesAssuntos();
        }
    }

    public void onStart() {
        EventBus.getDefault().register(view);
    }

    public void onStop() {
        EventBus.getDefault().unregister(view);
    }

    public void onEvent(LinkedList<Conversas> conversas) {

        for (Conversas conversa : conversas) {
            Usuario usuario = Sistema.getListaUsuarios().get(conversa.getDestinatario());

            if (Sistema.getListaUsuarios().get(this.conversa.getDestinatario()).getUserID() == usuario.getUserID()) {
                this.conversa = conversa;
                carregarMensagens();
                break;
            }
        }
    }

    private void carregarMensagens() {
        view.onLoadListaMensagens(conversa.getMensagens());

        if (qntdMensagens != conversa.getMensagens().size()) {
            view.onSetPositionRecyclerView(conversa.getMensagens().size() - 1);
            atualizarMensagensLidas();

            qntdMensagens = conversa.getMensagens().size();
        }
    }

    private void atualizarMensagensLidas() {
        Map<String, Long> map = new HashMap<>();

        map.put("conversa", conversa.getId());
        map.put("usuario", Sistema.getUsuario().getUserID());
        map.put("mensagem", conversa.getMensagens().getLast().getId());

        Call<Object> call = service.atualizarMensagensLidas(map);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    for (Mensagem m : conversa.getMensagens()) {
                        if (!m.isDestinatario()) {
                            m.setLida(true);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("Atualizar Lidas", "Erro ao fazer a requisição", t);
            }
        });
    }

    public void enviarMensagem(String textoMensagem) {
        Map<String, String> map = new HashMap<>();

        if (!textoMensagem.trim().isEmpty()) {
            map.put("conversa", String.valueOf(conversa.getId()));
            map.put("usuario", String.valueOf(Sistema.getUsuario().getUserID()));
            map.put("destinatario", String.valueOf(conversa.getDestinatario()));
            map.put("data", new Date().toString());
            map.put("mensagem", textoMensagem.trim());

            Call<Object> call = service.enviarMensagem(map);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    view.onClearCampos();
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    view.showToast("Falha ao enviar");
                }
            });
        }
    }

    public void vibrar() {
        if (conversa.getContMsgNaoLidas() > 0) {
            Utils.vibrar(view.getContext(), 150);
        }
    }

    public void setTextChangedTxtResposta(int length) {
        view.onSetTextLblContLetras(length + " / 250");
    }

    public void mostrarSugestoes() {
        DialogSugestoes sugestoes = new DialogSugestoes(view);
        sugestoes.show(conversa.getSugestoes());
    }

    private void carregarSugestoesAssuntos() {
        Facebook facebook = new Facebook(getContext());

        facebook.carregarPaginasEmComum(conversa.getDestinatario(), new ICallbackPaginas() {
            @Override
            public void setResponse(HashSet<Pagina> paginas) {
                Map<String, Assunto> mapAssuntos = new HashMap<>();

                for (Assunto assunto : Sistema.getAssuntos()) {
                    for (String categoria : assunto.getCategorias()) {
                        mapAssuntos.put(categoria, assunto);
                    }
                }

                for (Pagina pagina : paginas) {
                    if (mapAssuntos.containsKey(pagina.getCategoria())) {
                        Assunto assunto = mapAssuntos.get(pagina.getCategoria());

                        int item = new Random().nextInt(assunto.getItens().size());
                        conversa.getSugestoes().add(assunto.getItens().get(item)
                            .replace("%a", pagina.getNome())
                            .replace("%i", Sistema.getDescricaoIdioma(Sistema.getUsuario().getIdioma()))
                            .replace("%u", Sistema.getListaUsuarios().get(conversa.getDestinatario()).getPerfil().getFirstName()));
                    }
                }

                if (conversa.getSugestoes().size() > 0) {
                    view.onSetVisibilityBtnSugestoes(View.VISIBLE);
                }

                conversa.setCarregouSugestoes(true);
            }
        });
    }
}
