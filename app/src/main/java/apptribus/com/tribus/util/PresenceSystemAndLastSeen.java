package apptribus.com.tribus.util;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;

/**
 * Created by User on 9/4/2017.
 */

public class PresenceSystemAndLastSeen {

    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);


    public static void presenceSystem() {

        //UPDATE ONLINE
        Map<String, Object> updateOnline = new HashMap<>();
        updateOnline.put("online", true);



        if (mAuth.getCurrentUser() != null) {

            Log.d("Valor", "presenceSystem: mAuth.getCurrentUser() != null - set to true");
            mReferenceUser
                    .child(mAuth.getCurrentUser().getUid())
                    .updateChildren(updateOnline);
        }

    }

    public static void lastSeen() {
        if (mAuth.getCurrentUser() != null) {

            Log.d("Valor", "lastSeen: mAuth.getCurrentUser() != null - set to false");
            mReferenceUser
                    .child(mAuth.getCurrentUser().getUid())
                    .child("online")
                    .setValue(false);

            mReferenceUser
                    .child(mAuth.getCurrentUser().getUid())
                    .child("lastSeen")
                    .setValue(new Date(System.currentTimeMillis()));
        }

    }

}
