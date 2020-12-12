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
 * Created by User on 11/11/2017.
 */

public class PresenceSytemAndLastSeenChatTalker {

    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);


    public static void presenceSystem() {

        //UPDATE ONLINE
        Map<String, Object> updateOnline = new HashMap<>();
        updateOnline.put("onlineInChat", true);

        if (mAuth.getCurrentUser() != null) {

            mReferenceUser
                    .child(mAuth.getCurrentUser().getUid())
                    .updateChildren(updateOnline);
        }

    }

    public static void lastSeen() {
        Map<String, Object> updateOnline = new HashMap<>();
        updateOnline.put("onlineInChat", false);

        if (mAuth.getCurrentUser() != null) {
            mReferenceUser
                    .child(mAuth.getCurrentUser().getUid())
                    .updateChildren(updateOnline);

        }

    }

}
