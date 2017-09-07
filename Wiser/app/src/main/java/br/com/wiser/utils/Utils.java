package br.com.wiser.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

import br.com.wiser.R;

public class Utils {

    public static void loadImageInBackground(Context context, String url, final ImageView imageView, final ProgressBar prgBarra) {

        if (!TextUtils.isEmpty(url)) {
            imageView.setVisibility(View.GONE);
            prgBarra.setVisibility(View.VISIBLE);
            prgBarra.bringToFront();

            Picasso.with(context)
                    .load(url)
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            imageView.setVisibility(View.VISIBLE);
                            prgBarra.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imageView.setVisibility(View.VISIBLE);
                            prgBarra.setVisibility(View.GONE);
                        }
                    });
        }
    }

    public static void compartilharComoImagem(View view){

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        Bitmap img = view.getDrawingCache();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(view.getContext().getContentResolver(),
                img, view.getContext().getString(R.string.compartilhar_titulo), null);

        Intent iCompartilhar = new Intent(Intent.ACTION_SEND);
        Uri imgDiscussao = Uri.parse(path);

        iCompartilhar.setType("image/png");
        iCompartilhar.putExtra(Intent.EXTRA_STREAM, imgDiscussao);
        view.getContext().startActivity(Intent.createChooser(iCompartilhar,
                view.getContext().getString(R.string.compartilhar_discussao_sistema)));
    }

    public static void compartilharAppComoTexto(Context context){

        Intent iCompartilhar = new Intent(Intent.ACTION_SEND);
        iCompartilhar.setType("text/plain");
        iCompartilhar.putExtra(Intent.EXTRA_TEXT,
                context.getString(R.string.sistema_link_playstore));
        context.startActivity(Intent.createChooser(iCompartilhar,
                context.getString(R.string.compartilhar_aplicativo_sistema)));
    }

    public static void vibrar(Context context, long duracao){
        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(duracao);
    }

    public static String encode(String texto) {
        try {
            return URLEncoder.encode(texto, "UTF-8");
        }
        catch(Exception e) {
            Log.e("DECODE", "Erro ao fazer o encode no texto: " + texto);
            return texto;
        }
    }

    public static String decode(String texto) {
        try {
            return URLDecoder.decode(texto, "UTF-8");
        }
        catch(Exception e) {
            Log.e("DECODE", "Erro ao fazer o decode no texto: " + texto);
            return texto;
        }
    }
}