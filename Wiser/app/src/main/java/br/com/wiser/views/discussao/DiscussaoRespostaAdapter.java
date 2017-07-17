package br.com.wiser.views.discussao;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.models.forum.Resposta;

/**
 * Created by Jefferson on 20/05/2016.
 */
public class DiscussaoRespostaAdapter extends RecyclerView.Adapter<DiscussaoRespostaAdapter.ViewHolder> {

    private Context context;
    private LinkedList<Resposta> listaRespostas;

    public DiscussaoRespostaAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.forum_discussao_resposta, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Resposta r = listaRespostas.get(position);
        Usuario usuario = null;

        /*
        if (Sistema.getListaUsuarios().containsKey(r.getDestinatario())) {
            usuario = Sistema.getListaUsuarios().get(r.getDestinatario());
        }

        holder.viewSeparator.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);

        Utils.loadImageInBackground(context, usuario.getPerfil().getUrlProfilePicture(), holder.imgPerfil, holder.prgBarra);
        holder.lblIDResposta.setText("#" + (position + 1));
        holder.lblAutor.setText(usuario.getPerfil().getFirstName());
        holder.lblDataHora.setText(UtilsDate.formatDate(r.getData(), UtilsDate.DDMMYYYY_HHMMSS));
        holder.lblResposta.setText(Utils.decode(r.getResposta()));
        */
    }

    @Override
    public int getItemCount() {
        return listaRespostas.size();
    }

    public void setItems(LinkedList<Resposta> listaRespostas) {
        this.listaRespostas = listaRespostas;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View viewSeparator;

        public ImageView imgPerfil;
        public ProgressBar prgBarra;
        public TextView lblIDResposta;
        public TextView lblAutor;
        public TextView lblDataHora;
        public TextView lblResposta;

        public ViewHolder(View view) {
            super(view);

            viewSeparator = view.findViewById(R.id.viewSeparator);

            imgPerfil = (ImageView) view.findViewById(R.id.imgPerfil);
            prgBarra = (ProgressBar) view.findViewById(R.id.prgBarra);
            lblIDResposta = (TextView) view.findViewById(R.id.lblIDResposta);
            lblAutor = (TextView) view.findViewById(R.id.lblAutor);
            lblDataHora = (TextView) view.findViewById(R.id.lblDataHora);
            lblResposta = (TextView) view.findViewById(R.id.lblResposta);
        }
    }
}