package br.com.wiser.business.app.servidor;

import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import br.com.wiser.Sistema;
import br.com.wiser.business.app.facebook.Facebook;
import br.com.wiser.business.app.usuario.Usuario;
import br.com.wiser.business.chat.conversas.Conversas;
import br.com.wiser.business.chat.conversas.ConversasDAO;
import br.com.wiser.business.chat.mensagem.Mensagem;
import br.com.wiser.business.encontrarusuarios.pesquisa.Pesquisa;
import br.com.wiser.business.forum.discussao.Discussao;
import br.com.wiser.business.forum.discussao.DiscussaoDAO;
import br.com.wiser.business.forum.resposta.Resposta;
import br.com.wiser.enums.Models;
import br.com.wiser.utils.ComboBoxItem;

/**
 * Created by Jefferson on 30/07/2016.
 */
public class Servidor {

    private SimpleDateFormat formatarData = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public class App {

        public AccessToken getAccessToken() throws Exception {
            AccessToken accessToken;
            JSONObject json;

            //TODO colocar try/catch
            Response response = requestGET(Models.FACEBOOK, "getAccessToken");

            if (response.getCodeResponse() != HttpURLConnection.HTTP_OK)
            {
                throw new Exception();
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

        public List<ComboBoxItem> getIdiomas(boolean itemTodos) {
            List<ComboBoxItem> idiomas = new LinkedList<ComboBoxItem>();
            GETParametros parametros = new GETParametros();

            JSONArray jsonArray;
            JSONObject json;

            try {
                parametros.put("linguagem", Sistema.APP_LINGUAGEM);
                parametros.put("todos", itemTodos);

                jsonArray = new JSONArray(requestGET(Models.IDIOMA, "getIdiomas", parametros).getMessageResponse());

                for (int i = 0; i < jsonArray.length(); i++) {
                    json = jsonArray.getJSONObject(i);
                    ComboBoxItem item = new ComboBoxItem(json.getInt("cod_idioma"), json.getString("descricao"));
                    idiomas.add(item);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return idiomas;
        }

        public List<ComboBoxItem> getFluencias(boolean itemTodos) {
            List<ComboBoxItem> fluencias = new LinkedList<ComboBoxItem>();
            GETParametros parametros = new GETParametros();

            JSONArray jsonArray;
            JSONObject json;

            try {
                parametros.put("linguagem", Sistema.APP_LINGUAGEM);
                parametros.put("todos", itemTodos);

                jsonArray = new JSONArray(requestGET(Models.FLUENCIA, "getFluencias", parametros).getMessageResponse());

                for (int i = 0; i < jsonArray.length(); i++) {
                    json = jsonArray.getJSONObject(i);
                    ComboBoxItem item = new ComboBoxItem(json.getInt("nivel"), json.getString("descricao"));
                    fluencias.add(item);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return fluencias;
        }
    }

    public class Usuarios extends AbstractServidor {

        public Usuarios(Usuario usuario) {
            super(usuario);
        }

        public Usuario salvarLogin(Usuario usuario) throws Exception {
            JSONObject json = new JSONObject();
            Response response;

            try {
                json.put("facebook_id", usuario.getFacebookID());
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
                usuario.setStatus(json.optString("status"));
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
                json.put("status", usuario.getStatus());

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
                json.put("id", usuario);

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

        public LinkedList<Usuario> pesquisarUsuarios(Pesquisa pesquisa) {
            LinkedList<Usuario> listaResultados = new LinkedList<Usuario>();
            GETParametros parametros = new GETParametros();

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
                    listaResultados.add(getUsuarioJSON(jsonResultados.getJSONObject(i), true));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return listaResultados;
        }
    }

    public class Chat extends AbstractServidor {

        public Chat(Usuario usuario) {
            super(usuario);
        }

        public LinkedList<ConversasDAO> carregarGeral() {
            LinkedList<ConversasDAO> listaConversas = new LinkedList<ConversasDAO>();
            GETParametros parametros = new GETParametros();
            JSONArray jsonConversas;

            try {
                parametros.put("usuario", usuario);

                jsonConversas = new JSONArray(requestGET(Models.CONVERSAMENSAGEM, "carregarMensagens", parametros).getMessageResponse());

                for(int i = 0; i < jsonConversas.length(); i++) {
                    JSONObject jsonConversa = jsonConversas.getJSONObject(i);
                    JSONArray jsonUsuarios = jsonConversa.getJSONArray("usuarios");
                    JSONArray jsonMensagens = jsonConversa.getJSONArray("mensagens");

                    ConversasDAO conversa = new ConversasDAO();
                    Mensagem mensagem;
                    Usuario destinatario;

                    conversa.setId(jsonConversa.getLong("id"));

                    for (int j = 0; j < jsonUsuarios.length(); j++) {
                        if (jsonUsuarios.getJSONObject(j).getLong("usuario") != usuario.getUserID()) {
                            destinatario = new Usuario(jsonUsuarios.getJSONObject(j).getLong("usuario"));
                            conversa.setDestinatario(destinatario);
                        }
                    }

                    for (int j = 0; j < jsonMensagens.length(); j++) {
                        JSONObject json = new JSONObject();
                        mensagem = new Mensagem();

                        mensagem.setId(json.getLong("id"));
                        mensagem.setDestinatario(json.getLong("usuario") == usuario.getUserID());
                        mensagem.setData(formatarData.parse(json.getString("data").replaceAll("Z$", "+0000")));
                        mensagem.setLida(json.getBoolean("lida"));
                        mensagem.setMensagem(json.getString("mensagem"));

                        conversa.getMensagens().add(mensagem);
                    }

                    listaConversas.add(conversa);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            return listaConversas;
        }

        public void atualizarMsgsLidas(int idMensagem) {
            // TODO Atualizar as Conversas Lidas
        }

        public void enviarMensagem(String destinatarioID, String conteudo) {

        }
    }

    public class Forum extends AbstractServidor {

        public Forum(Usuario usuario) {
            super(usuario);
        }

        public LinkedList<DiscussaoDAO> carregarDiscussoes(boolean minhasDiscussoes) {
            LinkedList<DiscussaoDAO> listaDiscussao = new LinkedList<DiscussaoDAO>();
            GETParametros parametros = new GETParametros();
            JSONArray jsonDiscussoes;

            try {
                if (minhasDiscussoes) {
                    parametros.put("usuario", usuario.getUserID());
                }

                jsonDiscussoes = new JSONArray(requestGET(Models.DISCUSSAO, "carregarDiscussoes", parametros).getMessageResponse());

                for (int i = 0; i < jsonDiscussoes.length(); i++) {
                    listaDiscussao.add(getDiscussaoJSON(jsonDiscussoes.getJSONObject(i), minhasDiscussoes, usuario));
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            return listaDiscussao;
        }

        public boolean salvarDiscussao(Discussao discussao) {
            JSONObject json;
            JSONObject jsonParametros;

            try {
                jsonParametros = new JSONObject();
                jsonParametros.put("id", String.valueOf(discussao.getId()));
                jsonParametros.put("usuario", usuario.getUserID());
                jsonParametros.put("titulo", discussao.getTitulo());
                jsonParametros.put("descricao", discussao.getDescricao());
                jsonParametros.put("data", discussao.getDataHora());

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
            JSONObject json;
            JSONArray jsonDiscussoes;

            try {
                parametros = new GETParametros();
                parametros.put("chave", chave);

                jsonDiscussoes = new JSONArray(requestGET(Models.DISCUSSAO, "procurarDiscussoes", parametros).getMessageResponse());

                for (int i = 0; i < jsonDiscussoes.length(); i++) {
                    listaDiscussoes.add(getDiscussaoJSON(jsonDiscussoes.getJSONObject(i)));
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
                json.put("desativar", discussao.getDiscussaoAtiva());

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

        public boolean enviarResposta(Discussao discussao, Resposta resposta) {
            JSONObject json;
            Response response;

            try {
                json = new JSONObject();
                json.put("id", String.valueOf(discussao.getId()));
                json.put("usuario", usuario.getUserID());
                json.put("data", resposta.getDataHora());
                json.put("resposta", resposta.getResposta());

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

    private Usuario getUsuarioJSON(JSONObject json) throws JSONException {
        return getUsuarioJSON(json, false);
    }

    private Usuario getUsuarioJSON(JSONObject json, boolean carregarInfoPessoais) throws JSONException {
        Usuario usuario = new Usuario(json.getLong("id"));

        try {
            usuario.setFacebookID(json.getString("facebook_id"));
            usuario.setDataUltimoAcesso(formatarData.parse(json.getString("data_ultimo_acesso").replaceAll("Z$", "+0000")));
            usuario.setLatitude(json.getDouble("latitude"));
            usuario.setLongitude(json.getDouble("longitude"));
            usuario.setContaAtiva(json.getBoolean("conta_ativa"));
            usuario.setSetouConfiguracoes(json.getBoolean("setou_configuracoes"));

            if (usuario.isSetouConfiguracoes()) {
                usuario.setIdioma(json.getInt("idioma"));
                usuario.setFluencia(json.getInt("fluencia"));
                usuario.setStatus(json.getString("status"));
            }

            if (carregarInfoPessoais) {
                Facebook.getProfile(usuario);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return usuario;
    }

    private DiscussaoDAO getDiscussaoJSON(JSONObject json) throws Exception {
        return getDiscussaoJSON(json, false, null);
    }

    private DiscussaoDAO getDiscussaoJSON(JSONObject json, boolean minhasDiscussoes, Usuario usuario) throws Exception {
        DiscussaoDAO discussao = new DiscussaoDAO();

        discussao.setUsuario(minhasDiscussoes ? usuario : getUsuarioJSON(json.getJSONObject("usuario"), true));
        discussao.setId(json.getLong("id"));
        discussao.setTitulo(json.getString("titulo"));
        discussao.setDescricao(json.getString("descricao"));
        discussao.setDiscussaoAtiva(json.getBoolean("discussao_ativa"));
        discussao.setDataHora(formatarData.parse(json.getString("data").replaceAll("Z$", "+0000")));

        for (int j = 0; j < json.getJSONArray("respostas").length(); j++) {
            JSONObject jsonResp = json.getJSONArray("respostas").getJSONObject(j);
            Resposta resposta = new Resposta();

            resposta.setId(jsonResp.getLong("id"));
            /* TODO Arrumar isso
                Não esta sendo trazido as informações do Usuario
                resposta.setUsuario(getUsuarioJSON(json.getJSONObject("usuario")));
             */
            resposta.setUsuario(new Usuario(jsonResp.getLong("usuario")));
            resposta.setDataHora(formatarData.parse(jsonResp.getString("data").replaceAll("Z$", "+0000")));
            resposta.setResposta(jsonResp.getString("resposta"));

            discussao.getListaRespostas().add(resposta);
        }

        return discussao;
    }

    private Response requestGET(Models model) {
        return requestGET(model, "", new GETParametros());
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

    private Response requestPOST(Models model, String postParametros) {
        return requestPOST(model, "", postParametros);
    }

    private Response requestPOST(Models model, String endPoint, String postParametros) {
        String url = "http://" + Sistema.SERVIDOR_WS + "/" + model.name() + "/" + endPoint;
        Response response = new Response();

        URL obj;
        HttpURLConnection con;
        DataOutputStream wr;
        BufferedReader in;

        StringBuffer stringBuffer;
        String inputLine;

        response.setMessageResponse("");
        response.setCodeResponse(0);

        try {
            obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setDoOutput(true);

            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParametros);
            wr.flush();
            wr.close();

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            stringBuffer = new StringBuffer();

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
}
