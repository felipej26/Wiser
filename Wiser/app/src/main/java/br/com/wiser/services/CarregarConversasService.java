package br.com.wiser.services;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import br.com.wiser.APIClient;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.features.conversa.ConversaDAO;
import br.com.wiser.features.conversa.IConversaService;
import br.com.wiser.features.mensagem.Mensagem;
import br.com.wiser.features.mensagem.MensagemDAO;
import br.com.wiser.models.usuario.IUsuarioService;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.utils.Utils;
import br.com.wiser.views.splashscreen.SplashScreenActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 18/09/2016.
 */
public class CarregarConversasService extends Service implements Observer {

    private interface CallbackConversas {
        void onLoad(List<String> listaNovasMensagens);
    }

    private IConversaService service;
    private IUsuarioService usuarioService;

    private LinkedList<Conversa> listaConversas;
    private boolean lock = false;
    private boolean lockCarregarUsuarios = false;

    private Map<Long, List<String>> listaMensagensNaoEnviadas;

    private long idUltimaMensagem;

    @Override
    public void onCreate() {
        super.onCreate();

        service = APIClient.getClient().create(IConversaService.class);
        usuarioService = APIClient.getClient().create(IUsuarioService.class);

        listaConversas = new LinkedList<>();
        listaMensagensNaoEnviadas = new HashMap<>();

        EventBus.builder().logNoSubscriberMessages(false).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final ConversaDAO conversasDAO = new ConversaDAO(this);
        final MensagemDAO mensagensDAO = new MensagemDAO(this);
        idUltimaMensagem = mensagensDAO.getMaxIdServer();

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        if (Sistema.getUsuario() == null) {
                            stopSelf();
                            return;
                        }

                        Call<LinkedList<Conversa>> call = service.carregarConversas(Sistema.getUsuario().getUserID(), idUltimaMensagem);
                        call.enqueue(new Callback<LinkedList<Conversa>>() {
                            @Override
                            public void onResponse(Call<LinkedList<Conversa>> call, Response<LinkedList<Conversa>> response) {
                                Map<Long, List<Mensagem>> mapMensagens = new HashMap<>();

                                if (response.isSuccessful()) {
                                    for (Conversa conversa : response.body()) {
                                        if (conversa.getMensagens().size() > 0) {
                                            mapMensagens.put(conversa.getId(), conversa.getMensagens());
                                            conversasDAO.insert(conversa);
                                        }
                                    }

                                    if (mapMensagens.size() > 0) {
                                        mensagensDAO.insert(mapMensagens);
                                        Utils.vibrar(CarregarConversasService.this, 150);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<LinkedList<Conversa>> call, Throwable t) {
                                Log.e("Serviço", "Erro ao Carregar Conversa", t);
                                lock = false;
                            }
                        });

                        while (lock) ;
                    }
                    catch (Exception e) {
                        Log.e("Serviço", "Erro", e);
                    }

                    try {
                        Thread.sleep(3000);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof Usuario) {
            if (listaMensagensNaoEnviadas.containsKey(((Usuario) observable).getUserID())) {
                notificar(listaMensagensNaoEnviadas.get(((Usuario) observable).getUserID()));
            }
        }
    }

    private void adicionarNovasMensagens(final LinkedList<Conversa> listaConversas, final CallbackConversas callback) {
        final List<String> listaNovasMensagens = new LinkedList<>();
        List<Long> listaUsuarios = new LinkedList<>();

        Conversa conversaAux;

        /* Adiciona na Lista de Conversa as novas conversas
         * e na Lista de Usuarios, os novos usuarios dessas conversas */
        for (Conversa conversa : listaConversas) {
            conversaAux = null;

            if (conversa.getMensagens().size() > 0) {

                for (Conversa c : this.listaConversas) {
                    if (c.getId() == conversa.getId()) {
                        conversaAux = c;
                        break;
                    }
                }

                if (conversaAux == null) {
                    conversaAux = new Conversa();
                    conversaAux.setId(conversa.getId());
                    conversaAux.setDestinatario(conversa.getDestinatario());

                    listaUsuarios.add(conversa.getDestinatario());
                    this.listaConversas.add(conversaAux);
                }
            }
        }

        carregarUsuarios(listaUsuarios, new ICallback() {
            @Override
            public void onSuccess() {
                /* Adiciona na Lista de Conversa e na Lista de Novas Mensagens as novas mensagens recebidas */
                for (Conversa conversa : listaConversas) {
                    if (conversa.getMensagens().size() > 0) {

                        for (Conversa c : CarregarConversasService.this.listaConversas) {
                            if (c.getId() == conversa.getId()) {
                                c.getMensagens().addAll(conversa.getMensagens());

                                carregarMensagens(c, listaNovasMensagens);
                            }
                        }
                    }
                }

                callback.onLoad(listaNovasMensagens);
            }

            @Override
            public void onError(String mensagemErro) {
                Log.e("Serviço", mensagemErro);
                callback.onLoad(listaNovasMensagens);
            }
        });
    }

    private void carregarMensagens(Conversa conversa, List<String> listaNovasMensagens) {

        List<String> listaAux;

        for (Mensagem mensagem : conversa.getMensagens()) {
            if (mensagem.isDestinatario() && !mensagem.isLida()) {
                if (Sistema.getListaUsuarios().get(conversa.getDestinatario()).isPerfilLoaded()) {
                    listaNovasMensagens.add(
                            Sistema.getListaUsuarios().get(conversa.getDestinatario()).getPerfil().getFirstName() + ": " +
                                    Utils.decode(mensagem.getMensagem()));
                }
                else {
                    try {
                        Sistema.getListaUsuarios().get(conversa.getDestinatario()).addObserver(this);
                    }
                    catch (Exception e) {
                        Log.e("Serviço", "Erro ao adicionar o Observer. " + e.getMessage());
                    }

                    if (!listaNovasMensagens.contains(conversa.getDestinatario())) {
                        listaAux = new LinkedList<>();
                        listaMensagensNaoEnviadas.put(conversa.getDestinatario(), listaAux);
                    }
                    else {
                        listaAux = listaMensagensNaoEnviadas.get(conversa.getDestinatario());
                    }

                    listaAux.add(Sistema.getListaUsuarios().get(conversa.getDestinatario()).getPerfil().getFirstName() + ": " +
                            Utils.decode(mensagem.getMensagem()));
                }
            }

            mensagem.setMensagem(Utils.decode(mensagem.getMensagem()));
        }
    }

    private void carregarUsuarios(List<Long> listaUsuarios, final ICallback callback) {
        Sistema.carregarUsuarios(this, listaUsuarios, callback);
    }

    private void notificar(List<String> listaMensagensNaoLidas) {

        final int NOTIFICATION_ID = 1;

        if (listaMensagensNaoLidas.size() == 0) {
            return;
        }

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_wiser_notificacao)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Chegaram " + listaMensagensNaoLidas.size() + " novas mensagens!");

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.app_name));

        for (String mensagem : listaMensagensNaoLidas) {
            System.out.println(mensagem);
            inboxStyle.addLine(mensagem);
        }

        builder.setStyle(inboxStyle);

        Intent resultIntent = new Intent(this, SplashScreenActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SplashScreenActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
