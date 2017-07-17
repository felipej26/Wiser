package br.com.wiser.views.usuariosencontrados;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.wiser.R;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.utils.Utils;

/**
 * Created by Wesley on 08/04/2016.
 */
public class UsuariosEncontradosAdapter extends ArrayAdapter<Usuario> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Usuario> listaUsuarios = null;

    public UsuariosEncontradosAdapter(Context context, int layoutResourceId, ArrayList<Usuario> listaUsuarios) {
        super(context, layoutResourceId, listaUsuarios);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.listaUsuarios = listaUsuarios;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View objView = convertView;;
        RecordHolder objHolder;

        if (objView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            objView = inflater.inflate(layoutResourceId, parent, false);

            objHolder = new RecordHolder();
            objHolder.imgPerfil = (ImageView) objView.findViewById(R.id.imgPerfil);
            objHolder.prgBarra = (ProgressBar) objView.findViewById(R.id.prgBarra);
            objHolder.txtNome = (TextView) objView.findViewById(R.id.txtNomeLista);

            objView.setTag(objHolder);
        }
        else {
            objHolder = (RecordHolder) objView.getTag();
        }

        Usuario usuario = listaUsuarios.get(position);
        objHolder.txtNome.setText(usuario.getPrimeiroNome());
        Utils.loadImageInBackground(context, usuario.getUrlFotoPerfil(), objHolder.imgPerfil, objHolder.prgBarra);

        return objView;
    }

    private static class RecordHolder {
        ImageView imgPerfil;
        ProgressBar prgBarra;
        TextView txtNome;
    }
}