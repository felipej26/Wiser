package br.com.wiser.business.app.servidor;

import android.content.Context;

import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import br.com.wiser.Sistema;
import br.com.wiser.business.app.facebook.Facebook;
import br.com.wiser.business.app.facebook.ICallbackPaginas;
import br.com.wiser.business.app.usuario.Usuario;
import br.com.wiser.business.chat.assunto.Assunto;
import br.com.wiser.business.chat.conversas.Conversas;
import br.com.wiser.business.chat.conversas.ConversasDAO;
import br.com.wiser.business.chat.mensagem.Mensagem;
import br.com.wiser.business.chat.paginas.Pagina;
import br.com.wiser.business.encontrarusuarios.pesquisa.Pesquisa;
import br.com.wiser.business.forum.discussao.Discussao;
import br.com.wiser.business.forum.discussao.DiscussaoDAO;
import br.com.wiser.business.forum.resposta.Resposta;
import br.com.wiser.enums.Models;
import br.com.wiser.utils.ComboBoxItem;
import br.com.wiser.utils.Utils;
import br.com.wiser.utils.UtilsDate;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Jefferson on 30/07/2016.
 */
public class Servidor {

    public class App {

        public AccessToken getAccessToken() throws Exception {
            AccessToken accessToken;
            JSONObject json;

            Response response = requestGET(Models.FACEBOOK, "getAccessToken");

            if (response.getCodeResponse() != HttpURLConnection.HTTP_OK)
            {
                throw new Exception("Não foi possível receber as configurações de Acesso a API do Facebook!");
            }

            json = new JSONObject(response.getMessageResponse());
            accessToken = new AccessToken(
                    json.getString("accessToken"),
                    json.getString("appID"),
                    json.getString("userID"),
                    Arrays.asList(json.getString("permissions").split(",")),
                    null, null, null, null
            );

            return accessToken;
        }

        public List<ComboBoxItem> getIdiomas() {
            List<ComboBoxItem> idiomas = new LinkedList<ComboBoxItem>();
            GETParametros parametros = new GETParametros();

            JSONArray jsonArray;
            JSONObject json;

            try {
                parametros.put("linguagem", Sistema.APP_LINGUAGEM);
                // TODO REMOVER esta linha
                parametros.put("todos", true);

                jsonArray = new JSONArray(requestGET(Models.IDIOMA, "getIdiomas", parametros).getMessageResponse());

                for (int i = 0; i < jsonArray.length(); i++) {
                    json = jsonArray.getJSONObject(i);
                    ComboBoxItem item = new ComboBoxItem(json.getInt("cod_idioma"), decode(json.getString("descricao")));
                    idiomas.add(item);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return idiomas;
        }

        public List<ComboBoxItem> getFluencias() {
            List<ComboBoxItem> fluencias = new LinkedList<ComboBoxItem>();
            GETParametros parametros = new GETParametros();

            JSONArray jsonArray;
            JSONObject json;

            try {
                parametros.put("linguagem", Sistema.APP_LINGUAGEM);
                // TODO REMOVER esta linha
                parametros.put("todos", true);

                jsonArray = new JSONArray(requestGET(Models.FLUENCIA, "getFluencias", parametros).getMessageResponse());

                for (int i = 0; i < jsonArray.length(); i++) {
                    json = jsonArray.getJSONObject(i);
                    ComboBoxItem item = new ComboBoxItem(json.getInt("nivel"), decode(json.getString("descricao")));
                    fluencias.add(item);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return fluencias;
        }
    }

    public class Usuarios extends AbstractServidor {

        public Usuarios(Context context) {
            super(context);
        }

        public Usuario salvarLogin(Usuario usuario) throws Exception {
            JSONObject json = new JSONObject();
            Response response;

            try {
                json.put("facebook_id", usuario.getFacebookID());
                json.put("access_token", usuario.getAccessToken());
                json.put("data_ultimo_acesso", usuario.getDataUltimoAcesso());
                json.put("latitude", usuario.getLatitude());
                json.put("longitude", usuario.getLongitude());

                response = requestPOST(Models.USUARIO, "updateOrCreate", json.toString());

                if (response.getCodeResponse() != HttpURLConnection.HTTP_OK) {
                    throw new Exception(response.getCodeResponse() + "-" + response.getMessageResponse());
                }

                json = new JSONObject(response.getMessageResponse());
                usuario.setUserID(json.getLong("id"));
                usuario.setSetouConfiguracoes(json.getBoolean("setou_configuracoes"));
                usuario.setIdioma(json.optInt("idioma"));
                usuario.setFluencia(json.optInt("fluencia"));
                usuario.setStatus(decode(json.optString("status")));
            }
            catch (Exception e) {
                throw new Exception(e.getMessage());
            }

            return usuario;
        }

        public boolean salvarConfiguracoes(Usuario usuario) {
            JSONObject json = new JSONObject();
            Response response;

            try {
                json.put("id", usuario.getUserID());
                json.put("idioma", usuario.getIdioma());
                json.put("fluencia", usuario.getFluencia());
                json.put("status", encode(usuario.getStatus()));

                response = requestPOST(Models.USUARIO, "salvarConfiguracoes", json.toString());

                if (response.getCodeResponse() != HttpURLConnection.HTTP_OK) {
                    return false;
                }
            }
            catch (Exception ex) {
                return false;
            }

            return true;
        }

        public boolean desativarConta() {
            JSONObject json = new JSONObject();
            Response response;

            try {
                json.put("id", Sistema.getUsuario(context));

                response = requestPOST(Models.USUARIO, "desativarConta", json.toString());

                if (response.getCodeResponse() != HttpURLConnection.HTTP_OK) {
                    return false;
                }
            }
            catch (Exception ex) {
                return false;
            }

            return true;
        }

        public Usuario carregarUsuario(Usuario usuario) {
            GETParametros parametros = new GETParametros();
            JSONObject json;

            try {
                parametros.put("id", usuario.getUserID());

                json = new JSONObject(requestGET(Models.USUARIO, parametros).getMessageResponse());
                usuario = getUsuarioJSON(json, true, context);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return usuario;
        }
    }

    public class Contatos extends AbstractServidor {

        public Contatos(Context context) {
            super(context);
        }

        public boolean adicionarContato(Usuario contato) {
            Response response;
            JSONObject json;

            try {
                json = new JSONObject();
                json.put("usuario", Sistema.getUsuario(context).getUserID());
                json.put("contato", contato.getUserID());

                response = requestPOST(Models.CONTATO, "adicionarContato", json.toString());

                if (response.getCodeResponse() != HttpURLConnection.HTTP_OK) {
                    return false;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }

            return true;
        }

        public LinkedList<Usuario> carregarContatos() {
            JSONArray jsonContatos;
            Response response;
            GETParametros parametros = new GETParametros();

            LinkedList<Usuario> contatos = new LinkedList<>();

            try {
                parametros.put("usuario", Sistema.getUsuario(context).getUserID());

                response = requestGET(Models.CONTATO, "carregarContatos", parametros);

                if (response.getCodeResponse() == HttpURLConnection.HTTP_OK) {
                    jsonContatos = new JSONArray(response.getMessageResponse());

                    for (int i = 0; i < jsonContatos.length(); i++) {
                        contatos.add(getUsuarioJSON(jsonContatos.getJSONObject(i).getJSONObject("contato"), true, context));
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            return contatos;
        }

        public LinkedList<Usuario> encontrarUsuarios(Pesquisa pesquisa) {
            LinkedList<Usuario> listaResultados = new LinkedList<Usuario>();
            GETParametros parametros = new GETParametros();

            Usuario usuario = Sistema.getUsuario(context);
            JSONArray jsonResultados;

            try {
                parametros.put("id", usuario.getUserID());
                parametros.put("latitude", String.valueOf(usuario.getLatitude()));
                parametros.put("longitude", String.valueOf(usuario.getLongitude()));

                if (pesquisa.getIdioma() > 0)
                    parametros.put("idioma", pesquisa.getIdioma());

                if (pesquisa.getFluencia() > 0)
                    parametros.put("fluencia", pesquisa.getFluencia());

                parametros.put("distancia", pesquisa.getDistancia());

                jsonResultados = new JSONArray(requestGET(Models.USUARIO, "encontrarUsuarios", parametros).getMessageResponse());

                for (int i = 0; i < jsonResultados.length(); i++) {
                    listaResultados.add(getUsuarioJSON(jsonResultados.getJSONObject(i), true, context));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return listaResultados;
        }
    }

    public class Chat extends AbstractServidor {

        public Chat(Context context) {
            super(context);
        }

        /**
         *
         * @param conversas
         * @return Retorna uma Lista contendo as Novas Mensagens
         */
        public List<String> carregarGeral(LinkedList<ConversasDAO> conversas) {
            GETParametros parametros = new GETParametros();
            Usuario usuario = Sistema.getUsuario(context);
            long id_ultima_mensagem = 0;

            List<String> listaNovasMensagens = new LinkedList<>();

            Response response;
            JSONArray jsonConversas;

            try {
                parametros.put("usuario", usuario.getUserID());

                for (ConversasDAO conversa : conversas) {
                    if (conversa.getMensagens().size() > 0) {
                        if (conversa.getMensagens().getLast().getId() > id_ultima_mensagem) {
                            id_ultima_mensagem = conversa.getMensagens().getLast().getId();
                        }
                    }
                }
                parametros.put("mensagem", id_ultima_mensagem);

                response = requestGET(Models.CONVERSA, "carregarConversas", parametros);

                if (response.getCodeResponse() == HttpURLConnection.HTTP_OK) {
                    jsonConversas = new JSONArray(response.getMessageResponse());

                    for (int i = 0; i < jsonConversas.length(); i++) {
                        JSONObject jsonConversa = jsonConversas.getJSONObject(i);
                        JSONArray jsonUsuarios = jsonConversa.getJSONArray("usuarios");
                        JSONArray jsonMensagens = jsonConversa.getJSONArray("mensagens");
                        ConversasDAO conversa = null;
                        Mensagem mensagem;
                        Usuario destinatario = null;

                        // Verifica se a Conversa já esta na Lista
                        for (ConversasDAO c : conversas) {
                            if (c.getId() == jsonConversa.getLong("id")) {
                                conversa = c;
                                break;
                            }
                        }

                        // Se a conversa não tiver na Lista, cria uma Nova e adiciona
                        if (conversa == null) {
                            conversa = new ConversasDAO();
                            conversa.setId(jsonConversa.getLong("id"));
                            conversas.add(conversa);
                        }

                        for (int j = 0; j < jsonUsuarios.length(); j++) {
                            if (jsonUsuarios.getJSONObject(j).getLong("usuario") != usuario.getUserID() &&
                                    jsonUsuarios.getJSONObject(j).getLong("usuario") != conversa.getDestinatario().getUserID()) {
                                destinatario = new Usuario(jsonUsuarios.getJSONObject(j).getLong("usuario"));
                                destinatario = new Usuarios(context).carregarUsuario(destinatario);
                                destinatario.setContato(jsonUsuarios.getJSONObject(j).getBoolean("isContato"));
                                conversa.setDestinatario(destinatario);
                            }
                        }

                        // Verifica se tem alguma mensagem e adiona a Lista de Mensagens da Conversa
                        for (int j = 0; j < jsonMensagens.length(); j++) {
                            JSONObject json = jsonMensagens.getJSONObject(j);
                            mensagem = new Mensagem();

                            mensagem.setId(json.getLong("id"));
                            mensagem.setDestinatario(json.getLong("usuario") != usuario.getUserID());
                            mensagem.setData(UtilsDate.parseDateJson(json.getString("data")));
                            mensagem.setLida(json.getBoolean("lida"));
                            mensagem.setMensagem(decode(json.getString("mensagem")));

                            conversa.getMensagens().add(mensagem);

                            if (mensagem.isDestinatario() && !mensagem.isLida()) {
                                listaNovasMensagens.add(conversa.getDestinatario().getPerfil().getFirstName() + ": " + mensagem.getMensagem());
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            return listaNovasMensagens;
        }

        public void atualizarLidas(Conversas conversa) {
            JSONObject json;
            Response response;

            try {
                json = new JSONObject();
                json.put("conversa", conversa.getId());
                json.put("usuario", Sistema.getUsuario(context).getUserID());
                json.put("mensagem", conversa.getMensagens().getLast().getId());

                response = requestPOST(Models.CONVERSA, "atualizarLidas", json.toString());

                if (response.getCodeResponse() == HttpURLConnection.HTTP_OK) {
                    for (Mensagem m : conversa.getMensagens()) {
                        if (m.isDestinatario()) {
                            m.setLida(true);
                        }
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public boolean enviarMensagem(Conversas conversa, long destinatario, Mensagem mensagem) {
            JSONObject json = new JSONObject();

            try {
                if (conversa.getId() == 0) {
                    json.put("conversa", "0");
                }
                else {
                    json.put("conversa", conversa.getId());
                }
                json.put("usuario", Sistema.getUsuario(context).getUserID());
                json.put("destinatario", destinatario);
                json.put("data", mensagem.getData());
                json.put("mensagem", encode(mensagem.getMensagem()));

                json = new JSONObject(requestPOST(Models.CONVERSA, "enviarMensagem", json.toString()).getMessageResponse());

                mensagem.setId(json.getLong("id"));
                conversa.setId(json.getLong("conversa"));
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }

            return true;
        }

        public void carregarSugestoesAssuntos(final Conversas conversa) {
            Facebook facebook = new Facebook(context);
            ICallbackPaginas callbackPaginas;

            try {
                callbackPaginas = new ICallbackPaginas() {
                    @Override
                    public void setResponse(HashSet<Pagina> paginas) {
                        Map<String, Assunto> mapAssuntos = new HashMap<>();

                        for (Assunto assunto : Sistema.ASSUNTOS) {
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
                                        .replace("%i", Utils.getDescricaoIdioma(Sistema.getUsuario(context).getIdioma()))
                                        .replace("%u", conversa.getDestinatario().getPerfil().getFirstName()));
                            }
                        }
                    }
                };

                facebook.carregarPaginasEmComum(Sistema.getUsuario(context), conversa, callbackPaginas);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public class Forum extends AbstractServidor {

        public Forum(Context context) {
            super(context);
        }

        public LinkedList<DiscussaoDAO> carregarDiscussoes(boolean minhasDiscussoes) {
            LinkedList<DiscussaoDAO> listaDiscussao = new LinkedList<DiscussaoDAO>();
            GETParametros parametros = new GETParametros();
            JSONArray jsonDiscussoes;

            try {
                parametros.put("usuario", Sistema.getUsuario(context).getUserID());
                parametros.put("minhasDiscussoes", minhasDiscussoes);

                jsonDiscussoes = new JSONArray(requestGET(Models.DISCUSSAO, "carregarDiscussoes", parametros).getMessageResponse());

                for (int i = 0; i < jsonDiscussoes.length(); i++) {
                    listaDiscussao.add(getDiscussaoJSON(jsonDiscussoes.getJSONObject(i), minhasDiscussoes, context));
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            return listaDiscussao;
        }

        public boolean salvarDiscussao(Discussao discussao) {
            JSONObject jsonParametros;

            try {
                jsonParametros = new JSONObject();
                jsonParametros.put("id", String.valueOf(discussao.getId()));
                jsonParametros.put("usuario", Sistema.getUsuario(context).getUserID());
                jsonParametros.put("titulo", encode(discussao.getTitulo()));
                jsonParametros.put("descricao", encode(discussao.getDescricao()));
                jsonParametros.put("data", discussao.getDataHora());

                // TODO se der erro, deve ser informado ao Usuario
                requestPOST(Models.DISCUSSAO, "updateOrCreate", jsonParametros.toString());
            }
            catch (Exception e) {
                return false;
            }

            return true;
        }

        public LinkedList<DiscussaoDAO> procurarDiscussoes(String chave) {
            LinkedList<DiscussaoDAO> listaDiscussoes = new LinkedList<DiscussaoDAO>();
            GETParametros parametros;
            JSONArray jsonDiscussoes;

            try {
                parametros = new GETParametros();
                parametros.put("usuario", Sistema.getUsuario(context).getUserID());
                parametros.put("chave", encode(chave));

                jsonDiscussoes = new JSONArray(requestGET(Models.DISCUSSAO, "procurarDiscussoes", parametros).getMessageResponse());

                for (int i = 0; i < jsonDiscussoes.length(); i++) {
                    listaDiscussoes.add(getDiscussaoJSON(jsonDiscussoes.getJSONObject(i), context));
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            return listaDiscussoes;
        }

        public boolean desativarDiscussao(Discussao discussao) {
            JSONObject json;

            try {
                json = new JSONObject();
                json.put("id", String.valueOf(discussao.getId()));
                json.put("desativar", discussao.isAtiva());

                Response response = requestPOST(Models.DISCUSSAO, "desativarDiscussao", json.toString());

                if (response.getCodeResponse() != HttpURLConnection.HTTP_OK) {
                    throw new Exception();
                }
            }
            catch (Exception e) {
                return false;
            }

            return true;
        }

        public boolean responderDiscussao(Discussao discussao, Resposta resposta) {
            JSONObject json;
            Response response;

            try {
                json = new JSONObject();
                json.put("id", String.valueOf(discussao.getId()));
                json.put("usuario", Sistema.getUsuario(context).getUserID());
                json.put("data", resposta.getDataHora());
                json.put("resposta", encode(resposta.getResposta()));

                response = requestPOST(Models.DISCUSSAO, "responderDiscussao", json.toString());

                if (response.getCodeResponse() != HttpURLConnection.HTTP_OK) {
                    throw new Exception(response.getCodeResponse() + "-" + response.getMessageResponse());
                }

                json = new JSONObject(response.getMessageResponse());
                resposta.setId(json.getLong("id"));
            }
            catch (Exception e) {
                return false;
            }

            return true;
        }
    }

    private Usuario getUsuarioJSON(JSONObject json, boolean carregarInfoPessoais, Context context) throws JSONException {
        Usuario usuario = new Usuario(json.getLong("id"));
        Facebook facebook;

        try {
            usuario.setFacebookID(json.getString("facebook_id"));
            usuario.setAccessToken(json.getString("access_token"));
            usuario.setDataUltimoAcesso(UtilsDate.parseDateJson(json.getString("data_ultimo_acesso")));
            usuario.setLatitude(json.getDouble("latitude"));
            usuario.setLongitude(json.getDouble("longitude"));

            try {
                usuario.setContaAtiva(json.getBoolean("conta_ativa"));
            }
            catch (JSONException e) {
                usuario.setContaAtiva(json.getInt("conta_ativa") == 1);
            }

            try {
                usuario.setSetouConfiguracoes(json.getBoolean("setou_configuracoes"));
            }
            catch (JSONException e) {
                usuario.setSetouConfiguracoes(json.getInt("setou_configuracoes") == 1);
            }

            if (json.has("isContato")) {
                try {
                    usuario.setContato(json.getBoolean("isContato"));
                }
                catch (JSONException e) {
                    usuario.setContato(json.getInt("isContato") == 1);
                }
            }

            if (usuario.isSetouConfiguracoes()) {
                usuario.setIdioma(json.getInt("idioma"));
                usuario.setFluencia(json.getInt("fluencia"));
                usuario.setStatus(decode(json.getString("status")));
            }

            if (carregarInfoPessoais) {
                facebook = new Facebook(context);
                facebook.carregarPerfil(usuario);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return usuario;
    }

    private DiscussaoDAO getDiscussaoJSON(JSONObject json, Context context) throws Exception {
        return getDiscussaoJSON(json, false, context);
    }

    private DiscussaoDAO getDiscussaoJSON(JSONObject json, boolean minhasDiscussoes, Context context) throws Exception {
        DiscussaoDAO discussao = new DiscussaoDAO();
        Map<Long, Usuario> mapUsuarios = new HashMap<>();

        discussao.setUsuario(minhasDiscussoes ? Sistema.getUsuario(context) : getUsuarioJSON(json.getJSONObject("usuario"), true, context));
        mapUsuarios.put(discussao.getUsuario().getUserID(), discussao.getUsuario());

        discussao.setId(json.getLong("id"));
        discussao.setTitulo(decode(json.getString("titulo")));
        discussao.setDescricao(decode(json.getString("descricao")));
        discussao.setAtiva(json.getBoolean("discussao_ativa"));
        discussao.setDataHora(UtilsDate.parseDateJson(json.getString("data")));

        for (int j = 0; j < json.getJSONArray("respostas").length(); j++) {
            JSONObject jsonResp = json.getJSONArray("respostas").getJSONObject(j);
            Resposta resposta = new Resposta();

            resposta.setId(jsonResp.getLong("id"));

            if (!mapUsuarios.containsKey(jsonResp.getLong("usuario"))) {
                mapUsuarios.put(jsonResp.getLong("usuario"), new Usuarios(context).carregarUsuario(new Usuario(jsonResp.getLong("usuario"))));
            }
            resposta.setUsuario(mapUsuarios.get(jsonResp.getLong("usuario")));
            resposta.setDataHora(UtilsDate.parseDateJson(jsonResp.getString("data")));
            resposta.setResposta(decode(jsonResp.getString("resposta")));

            discussao.getListaRespostas().add(resposta);
        }

        return discussao;
    }

    private String encode(String texto) throws UnsupportedEncodingException {
        return URLEncoder.encode(texto, "UTF-8");
    }

    private String decode(String texto) throws UnsupportedEncodingException {
        return URLDecoder.decode(texto, "UTF-8");
    }

    private Response requestGET(Models model, String endPoint) {
        return requestGET(model, endPoint, new GETParametros());
    }

    private Response requestGET(Models model, GETParametros parametros) {
        return requestGET(model, "", parametros);
    }

    private Response requestGET(Models model, String endPoint, GETParametros parametros) {
        String url = "http://" + Sistema.SERVIDOR_WS + "/" + model.name();

        if (endPoint.length() > 0)
            url += "/" + endPoint;

        url += "?" + parametros.toString();
        Response response = new Response();

        URL obj = null;
        HttpURLConnection con = null;
        BufferedReader in = null;

        StringBuffer stringBuffer = new StringBuffer();
        String inputLine;

        response.setMessageResponse("");
        response.setCodeResponse(0);

        try {
            obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            while ((inputLine = in.readLine()) != null) {
                stringBuffer.append(inputLine);
            }
            response.setMessageResponse(stringBuffer.toString());
            response.setCodeResponse(con.getResponseCode());

            in.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return response;
    }

    private Response requestPOST(Models model, String endPoint, String postParametros) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        Response response = new Response();
        okhttp3.Response r;

        String url = "http://" + Sistema.SERVIDOR_WS + "/" + model.name().toLowerCase() + "/" + endPoint;

        RequestBody body = RequestBody.create(JSON, postParametros);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            r = client.newCall(request).execute();
            response.setMessageResponse(r.body().string());
            response.setCodeResponse(r.code());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return response;
    }
}
