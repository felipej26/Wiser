package br.com.wiser.activity.forum;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.business.forum.resposta.Resposta;
import br.com.wiser.utils.UtilsDate;
import br.com.wiser.utils.Utils;

/**
 * Created by Jefferson on 20/05/2016.
 */
public class DiscussaoRespostaAdapter extends RecyclerView.Adapter<DiscussaoRespostaAdapter.ViewHolder> {

    private Context context;
    private LinkedList<Resposta> listaRespostas;

    public DiscussaoRespostaAdapter(Context context, LinkedList<Resposta> listaRespostas) {
        this.context = context;
        this.listaRespostas = listaRespostas;
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

        holder.viewSeparator.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);

        Utils.loadImageInBackground(context, r.getUsuario().getPerfil().getUrlProfilePicture(), holder.imgPerfil, holder.prgBarra);
        holder.lblIDResposta.setText("#" + (position + 1));
        holder.lblAutor.setText(r.getUsuario().getPerfil().getFirstName());
        holder.lblDataHora.setText(UtilsDate.formatDate(r.getDataHora(), UtilsDate.DDMMYYYY_HHMMSS));
        holder.lblResposta.setText(r.getResposta());
    }

    @Override
    public int getItemCount() {
        return listaRespostas.size();
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