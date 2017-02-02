package br.com.wiser.presenters.mensagens;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import br.com.wiser.Sistema;
import br.com.wiser.APIClient;
import br.com.wiser.models.conversas.Conversas;
import br.com.wiser.models.mensagens.Mensagem;
import br.com.wiser.dialogs.DialogSugestoes;
import br.com.wiser.models.mensagens.IMensagensService;
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

    public void onCreate(IMensagensView view, Conversas conversa) {
        super.onCreate(view);
        view.onInitView();

        service = APIClient.getClient().create(IMensagensService.class);
        this.conversa = conversa;

        view.onSetTitleActionBar(conversa.getDestinatario().getPerfil().getFullName());
        carregarMensagens();
    }

    public void onStart() {
        EventBus.getDefault().register(view);
    }

    public void onStop() {
        EventBus.getDefault().unregister(view);
    }

    public void onEvent(LinkedList<Conversas> conversas) {

        for (Conversas conversa : conversas) {
            if (this.conversa.getDestinatario().getUserID() == conversa.getDestinatario().getUserID()) {
                this.conversa = conversa;
                carregarMensagens();
                break;
            }
        }
    }

    private void carregarMensagens() {
        boolean hasNewMessages = conversa.getMensagens().size() != view.onGetQntMensagens();

        view.onLoadListaMensagens(conversa.getMensagens());

        if (hasNewMessages) {
            vibrar();
            view.onSetPositionRecyclerView(view.onGetQntMensagens());
            atualizarMensagensLidas();
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
                        if (m.isDestinatario()) {
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
            map.put("destinatario", String.valueOf(conversa.getDestinatario().getUserID()));
            map.put("data", new Date().toString());
            map.put("mensagem", textoMensagem.trim());

            Call<Object> call = service.enviarMensagem(map);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    // TODO tratar retorno

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

        if (!conversa.isCarregouSugestoes()) {
            carregarSugestoesAssuntos();
        }

        sugestoes.show(conversa.getSugestoes());
    }

    private void carregarSugestoesAssuntos() {

        // TODO Refazer
//        Facebook facebook = new Facebook(context);
//        ICallbackPaginas callbackPaginas;
//
//        try {
//            callbackPaginas = new ICallbackPaginas() {
//                @Override
//                public void setResponse(HashSet<Pagina> paginas) {
//                    Map<String, Assunto> mapAssuntos = new HashMap<>();
//
//                    for (Assunto assunto : Sistema.assuntos) {
//                        for (String categoria : assunto.getCategorias()) {
//                            mapAssuntos.put(categoria, assunto);
//                        }
//                    }
//
//                    for (Pagina pagina : paginas) {
//                        if (mapAssuntos.containsKey(pagina.getCategoria())) {
//                            Assunto assunto = mapAssuntos.get(pagina.getCategoria());
//
//                            int item = new Random().nextInt(assunto.getItens().size());
//                            conversa.getSugestoes().add(assunto.getItens().get(item)
//                                    .replace("%a", pagina.getNome())
//                                    .replace("%i", Utils.getDescricaoIdioma(Sistema.getUsuario(context).getIdioma()))
//                                    .replace("%u", conversa.getDestinatario().getPerfil().getFirstName()));
//                        }
//                    }
//
//                    conversa.setCarregouSugestoes(true);
//                }
//            };
//
//            facebook.carregarPaginasEmComum(Sistema.getUsuario(context), conversa, callbackPaginas);
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }
}
