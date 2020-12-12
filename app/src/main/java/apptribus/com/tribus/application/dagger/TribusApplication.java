package apptribus.com.tribus.application.dagger;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.leakcanary.LeakCanary;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.Constantes;
import apptribus.com.tribus.util.firestore.FirestoreService;

import apptribus.com.tribus.application.dagger.DaggerAppComponent;

/**
 * Created by User on 5/14/2017.
 */

public class TribusApplication extends MultiDexApplication {

    private AppComponent appComponent;
    private DatabaseReference mUsers;
    private FirebaseAuth mAuth;
    private static TribusApplication mInstance;



    public static TribusApplication get(Activity activity){
        return ((TribusApplication) activity.getApplication());
    }

    public static synchronized TribusApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        Fresco.initialize(this);

        mInstance = this;

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {

            //to handle online
            mUsers = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child(Constantes.GENERAL_USERS)
                    .child(mAuth.getCurrentUser().getUid());


            mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {

                        mUsers.child("online").onDisconnect().setValue(false);
                        mUsers.child("onlineInChat").onDisconnect().setValue(false);

                        mUsers.child("lastSeen").onDisconnect().setValue(new Date(System.currentTimeMillis()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    databaseError.getMessage();
                }
            });

        }


    }



    public AppComponent component(){
        return appComponent;
    }

    /**
     * Configure fresco using ONLY disk cache
     */
    public void configFresco() {
        Supplier<File> diskSupplier = () -> getApplicationContext().getCacheDir();

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryName("TribusImages")
                .setBaseDirectoryPathSupplier(diskSupplier)
                .build();

        ImagePipelineConfig frescoConfig = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();

        Fresco.initialize(this, frescoConfig);
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
