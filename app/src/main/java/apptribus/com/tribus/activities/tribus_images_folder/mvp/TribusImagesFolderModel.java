package apptribus.com.tribus.activities.tribus_images_folder.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.List;

import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.activities.profile_tribu_admin.ProfileTribuAdminActivity;
import apptribus.com.tribus.activities.profile_tribu_user.ProfileTribuUserActivity;
import apptribus.com.tribus.activities.tribus_images_folder.TribusImagesFolderActivity;
import apptribus.com.tribus.activities.tribus_images_folder.adapter.TribusImagesFolderAdapter;
import apptribus.com.tribus.activities.tribus_images_folder.adapter.TribusVideosFolderAdapter;
import apptribus.com.tribus.activities.tribus_images_folder.repository.TribusImagesFolderAPI;
import apptribus.com.tribus.activities.tribus_images_folder.view_holder.TribusImagesFolderViewHolder;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 9/23/2017.
 */

public class TribusImagesFolderModel {

    private final AppCompatActivity activity;

    public TribusImagesFolderModel(AppCompatActivity activity) {
        this.activity = activity;
    }

    //Get the current mTribu
    public Observable<Tribu> getTribu(String uniqueName) {
        return TribusImagesFolderAPI.getTribu(uniqueName);
    }

    public void backToMainActivity(){
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    //LOAD IMAGES
    public void loadImages(String mTribusKey, TribusImagesFolderAdapter mTribusImagesFolderAdapter, List<MessageUser> mMessagesList,
                           RecyclerView mRvTribusImagesFolder, int totalItemsToLoad, int mCurrentPage){
        TribusImagesFolderAPI.loadImages(mTribusKey, mTribusImagesFolderAdapter, mMessagesList, mRvTribusImagesFolder,
                totalItemsToLoad, mCurrentPage);
    }

    //LOAD VIDEOS
    public void loadVideos(String mTribusKey, TribusVideosFolderAdapter mTribusVideosFolderAdapter, List<MessageUser> mMessagesList,
                           RecyclerView mRvTribusImagesFolder, int totalItemsToLoad, int mCurrentPage){
        TribusImagesFolderAPI.loadVideos(mTribusKey, mTribusVideosFolderAdapter, mMessagesList, mRvTribusImagesFolder,
                totalItemsToLoad, mCurrentPage);
    }


    public FirebaseRecyclerAdapter<MessageUser, TribusImagesFolderViewHolder> getImages(String mTribuskey){
        return TribusImagesFolderAPI.getImages(mTribuskey);
    }

    public void removeListeners(){
        if (TribusImagesFolderAPI.mChildListenerVideos != null){
            TribusImagesFolderAPI.mRefTribusMessage.removeEventListener(TribusImagesFolderAPI.mChildListenerVideos);
        }
        if (TribusImagesFolderAPI.mChildListenerImages != null){
            TribusImagesFolderAPI.mRefTribusMessage.removeEventListener(TribusImagesFolderAPI.mChildListenerImages);
        }
    }
}
