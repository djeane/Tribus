package apptribus.com.tribus.activities.show_profile_image.repository;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by User on 12/25/2017.
 */

public class ShowProfileImageAPI {

    private static File mImageFolder;

    public static void downloadImage(AppCompatActivity activity, Uri imagem){

        /*String filename = "filename.jpg";
        String downloadUrlOfImage = "YOUR_LINK_THAT_POINTS_IMG_ON_WEBSITE";
        File direct =
                new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath() + "/" + "Tribus Imagens" + "/");


        if (!direct.exists()) {
            direct.mkdir();
        }*/

        File jpegFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        mImageFolder = new File(jpegFile, "Tribus Imagens");
        if (!mImageFolder.exists()) {
            mImageFolder.mkdirs();
        }

        String currentTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String prepend = "JPEG_" + currentTimestamp + "_";
        File imageFile = null;
        String mImageFileName = null;
        try {
            imageFile = File.createTempFile(prepend, ".jpg", mImageFolder);
            mImageFileName = imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }


        DownloadManager dm = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        //Uri downloadUri = imagem;
        DownloadManager.Request request = new DownloadManager.Request(imagem);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(mImageFileName)
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                        File.separator + "Tribus Imagens" + File.separator + mImageFileName);

        assert dm != null;
        dm.enqueue(request);
        Toast.makeText(activity, "Imagem salva com sucesso!", Toast.LENGTH_SHORT).show();

    }


    public static void openShareImage(Context context, Uri image) {

        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            sendIntent.setData(image);
            sendIntent.putExtra(Intent.EXTRA_STREAM, image);
            sendIntent.setType("image/*");
            context.startActivity(Intent.createChooser(sendIntent, "Compartilhar imagem com..."));

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Não foi encontrada uma tela para compartilhamento.", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
