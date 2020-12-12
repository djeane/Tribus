package apptribus.com.tribus.util;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import apptribus.com.tribus.R;

/**
 * Created by User on 11/2/2017.
 */

public class ShowSnackBarInfoInternet {

    private static boolean isConnected;
    private static Snackbar snackbar;

    // Method to manually check connection status
    public static void checkConnection(View view) {
        isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected, view);
    }

    public static boolean checkConnectionAnother() {
        return ConnectivityReceiver.isConnected();
    }


    // Showing the status in Snackbar
    public static void showSnack(boolean isConnected, View view) {
        String message = "Sem acesso à internet. Muitas funcionalidades deste app requerem acesso à internet.";
        int color = Color.RED;
        snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_INDEFINITE);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);

        if (!isConnected) {
            if(snackbar != null) {
                snackbar.show();
            }
        }
        else {
            if (snackbar != null) {
                snackbar.dismiss();
            }
        }
    }

    public static void showToastInfoInternet(Context context){
        Toast.makeText(context, "Esta funcionalidade requer acesso à internet.",
                Toast.LENGTH_LONG).show();
    }

    public static void showToastInfoInternetToUpdateImage(Context context){
        Toast.makeText(context, "Sem conexão com a internet. Atualização de imagem cancelada.",
                Toast.LENGTH_LONG).show();
    }
}