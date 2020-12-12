package apptribus.com.tribus.activities.chat_user;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;

import javax.inject.Inject;

import apptribus.com.tribus.activities.chat_user.dagger.ChatUserModule;
import apptribus.com.tribus.activities.chat_user.dagger.DaggerChatUserComponent;
import apptribus.com.tribus.activities.chat_user.mvp.ChatUserPresenter;
import apptribus.com.tribus.activities.chat_user.mvp.ChatUserView;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.application.dagger.TribusApplication;

import static apptribus.com.tribus.activities.chat_user.mvp.ChatUserModel.GALLERY_REQUEST;
import static apptribus.com.tribus.activities.chat_user.mvp.ChatUserModel.REQUEST_EXTERNAL_STORAGE;
import static apptribus.com.tribus.activities.chat_user.mvp.ChatUserModel.REQUEST_RECORD_AUDIO_CHAT_USER;
import static apptribus.com.tribus.activities.chat_user.mvp.ChatUserModel.VIDEO_REQUEST;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.UPDATE_LAST_MESSAGE;


public class ChatUserActivity extends AppCompatActivity {

    @Inject
    ChatUserView view;

    @Inject
    ChatUserPresenter presenter;

    public static void start(Context context){
        Intent intent = new Intent(context, ChatUserActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerChatUserComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .chatUserModule(new ChatUserModule(this))
                .build().inject(this);

        setContentView(view);


    }


    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.onRestart();
    }

    @Override
    protected void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (view.fromNotification == null){
            presenter.backToMainActivity();
        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(UPDATE_LAST_MESSAGE, "true");
            intent.putExtra(CONTACT_ID, view.mTalkerId);
            startActivity(intent);
            finish();
        }
    }


    //REQUEST PERMISSION TO USE MICROPHONE
    //GET IMAGE FROM GALLERY
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){

            Uri selectedImage = data.getData();
            //presenter.setImageUser(selectedImage);
            File imageFile = new File(data.getData().getPath());

            //send image to SendImageActivity
            MediaScannerConnection.scanFile(ChatUserActivity.this, new String[]{imageFile.getPath()},
                /*mimeTypes*/null, new MediaScannerConnection.MediaScannerConnectionClient() {
                        @Override
                        public void onMediaScannerConnected() {
                            // Do nothing
                        }

                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
            int fileSize = Integer.parseInt(String.valueOf(imageFile.length() / 1024));

            presenter.sendImageToSendImageActivity(selectedImage, fileSize, view.mTalkerId);


        }

        if(requestCode == VIDEO_REQUEST && resultCode == RESULT_OK && data != null){

            Uri selectedVideo = data.getData();

            File videoFile = new File(data.getData().getPath());

            int fileSize = Integer.parseInt(String.valueOf(videoFile.length() / 1024));

            //presenter.setImageUser(selectedImage);

            //send video to SendVideoActivity
            presenter.sendVideoToSendVideoActivity(selectedVideo, fileSize, view.mTalkerId);
        }
    }

    //REQUEST PERMISSION TO USE MICROPHONE
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //return;
                    presenter.getImage();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case REQUEST_RECORD_AUDIO_CHAT_USER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //presenter.openVoiceRecord();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "Permissão concedida!", Toast.LENGTH_LONG).show();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

}
