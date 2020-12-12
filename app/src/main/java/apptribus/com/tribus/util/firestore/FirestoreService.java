package apptribus.com.tribus.util.firestore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apptribus.com.tribus.pojo.Admin;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Survey;
import apptribus.com.tribus.pojo.SurveyVote;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.ADMIN;
import static apptribus.com.tribus.util.Constantes.CONTACTS;
import static apptribus.com.tribus.util.Constantes.CONTACTS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.FOLLOWERS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.INDIVIDUAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.INDIVIDUAL_USERS;
import static apptribus.com.tribus.util.Constantes.PARTICIPATING;
import static apptribus.com.tribus.util.Constantes.SURVEYS;
import static apptribus.com.tribus.util.Constantes.SURVEY_VOTES;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_PARTICIPANTS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_UNIQUE;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;
import static apptribus.com.tribus.util.Constantes.USER_INVITATIONS;
import static apptribus.com.tribus.util.Constantes.USER_PERMISSIONS;

public class FirestoreService {

    //FIREBASE
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUniqueTribu;
    private DatabaseReference mReferenceTribus;
    private DatabaseReference mReferenceUsernames;
    private DatabaseReference mReferenceContacts;
    private DatabaseReference mReferenceFollowers;
    private DatabaseReference mReferenceTalksMessages;


    //FIRESTORE INSTANCE
    private FirebaseFirestore mFirestore;


    //FIRESTORE COLLECTIONS REFERENCES
    private CollectionReference mUsersCollection;
    private CollectionReference mUsersnameCollection;
    private CollectionReference mTribusCollection;
    private CollectionReference mTribusUniqueNameCollection;

    private Context mContext;

    public FirestoreService(Context context) {
        this.mContext = context;

        mFirestore = FirebaseFirestore.getInstance();
        mUsersCollection = mFirestore.collection(GENERAL_USERS);
        mUsersnameCollection = mFirestore.collection(INDIVIDUAL_USERS);
        mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
        mTribusUniqueNameCollection = mFirestore.collection(TRIBUS_UNIQUE);
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUniqueTribu = mDatabase.getReference().child(INDIVIDUAL_TRIBUS);
        mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);
        mReferenceUsernames = mDatabase.getReference().child(INDIVIDUAL_USERS);
        mReferenceContacts = mDatabase.getReference().child(USERS_TALKS);
        mReferenceFollowers = mDatabase.getReference().child(FOLLOWERS);
        mReferenceTalksMessages = mDatabase.getReference().child(TALKERS_MESSAGES);

    }

    //GET ADMINS NUMBER
    public Observable<List<Tribu>> getAdminsNumber(List<Tribu> tribus, String userId) {

        return Observable.create(subscriber -> {

            mTribusCollection
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                            for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()) {

                                Tribu tribu = dc.toObject(Tribu.class);

                                if (tribu.getAdmin().getUidAdmin().equals(userId)) {
                                    tribus.add(tribu);
                                }

                            }

                            subscriber.onNext(tribus);

                        } else {
                            subscriber.onNext(null);
                        }

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        subscriber.onNext(null);
                    });

        });

    }


    public void getAllContactsMessages() {

        mReferenceTalksMessages
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot contact : dataSnapshot.getChildren()) {

                            String contactId = contact.getKey();

                            mReferenceTalksMessages
                                    .child(contactId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot anotherContact : dataSnapshot.getChildren()) {

                                                String anotherContactId = anotherContact.getKey();

                                                mReferenceTalksMessages
                                                        .child(contactId)
                                                        .child(anotherContactId)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                for (DataSnapshot uniqueMessage : dataSnapshot.getChildren()) {

                                                                    MessageUser oldMessage = uniqueMessage.getValue(MessageUser.class);

                                                                    MessageUser newMessage = new MessageUser();
                                                                    newMessage.setAccepetd(oldMessage.isAccepetd());
                                                                    newMessage.setDate(oldMessage.getDate());
                                                                    newMessage.setContentType(oldMessage.getContentType());
                                                                    newMessage.setKey(oldMessage.getKey());
                                                                    newMessage.setMessage(oldMessage.getMessage());
                                                                    newMessage.setUidUser(oldMessage.getUidUser());
                                                                    newMessage.setVisualized(true);

                                                                    mUsersCollection
                                                                            .document(contactId)
                                                                            .collection(CONTACTS)
                                                                            .document(anotherContactId)
                                                                            .collection(CONTACTS_MESSAGES)
                                                                            .document(oldMessage.getKey())
                                                                            .set(newMessage)
                                                                            .addOnSuccessListener(aVoid -> {

                                                                                mUsersCollection
                                                                                        .document(anotherContactId)
                                                                                        .collection(CONTACTS)
                                                                                        .document(contactId)
                                                                                        .collection(CONTACTS_MESSAGES)
                                                                                        .document(oldMessage.getKey())
                                                                                        .set(newMessage)
                                                                                        .addOnFailureListener(Throwable::printStackTrace);

                                                                            })
                                                                            .addOnFailureListener(Throwable::printStackTrace);

                                                                }


                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                databaseError.getMessage();
                                                            }
                                                        });
                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            databaseError.getMessage();
                                        }
                                    });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.getMessage();
                    }
                });

    }

    public void getAllParticipanting() {

        mReferenceFollowers
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshotFollower) {

                        for (DataSnapshot uniqueFollower : dataSnapshotFollower.getChildren()) {

                            String followerId = uniqueFollower.getKey();

                            mReferenceFollowers
                                    .child(followerId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshotTribuFollowed) {

                                            for (DataSnapshot uniqueTribu : dataSnapshotTribuFollowed.getChildren()) {

                                                String tribuUniqueName = uniqueTribu.getKey();

                                                Tribu followedTribu = uniqueTribu.getValue(Tribu.class);

                                                mReferenceTribus
                                                        .child(tribuUniqueName)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                Tribu tribu = dataSnapshot.getValue(Tribu.class);

                                                                Tribu tribuFollowed = new Tribu();
                                                                tribuFollowed.setDate(followedTribu.getDate());
                                                                tribuFollowed.setKey(tribu.getKey());
                                                                //tribuFollowed.setUidUser(followerId);
                                                                tribuFollowed.setThematic(tribu.getProfile().getThematic());

                                                                mUsersCollection
                                                                        .document(followerId)
                                                                        .collection(PARTICIPATING)
                                                                        .document(tribu.getKey())
                                                                        .set(tribuFollowed)
                                                                        .addOnFailureListener(Throwable::printStackTrace);

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                databaseError.getMessage();
                                                            }
                                                        });


                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            databaseError.getMessage();
                                        }
                                    });


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.getMessage();
                    }
                });





        /*mTribusCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot tribuDc : queryDocumentSnapshots.getDocuments()){

                        Tribu tribu = tribuDc.toObject(Tribu.class);

                        mTribusCollection
                                .document(tribu.getKey())
                                .collection(TRIBUS_PARTICIPANTS)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots1 -> {

                                    for (DocumentSnapshot participant : queryDocumentSnapshots.getDocuments()) {

                                        String participantId = participant.getId();

                                        //Follower follower = participant.toObject(Follower.class);

                                        Date followerDate = (Date) participant.get("date");

                                        Tribu newTribu = new Tribu();
                                        newTribu.setKey(tribu.getKey());
                                        newTribu.setDate(followerDate);
                                        newTribu.setThematic(tribu.getProfile().getThematic());

                                        mUsersCollection
                                                .document(participantId)
                                                .collection(PARTICIPATING)
                                                .document(tribu.getKey())
                                                .set(newTribu)
                                                .addOnFailureListener(e -> {
                                                    e.printStackTrace();
                                                });

                                    }

                                })
                                .addOnFailureListener(Throwable::printStackTrace);

                    }

                })
                .addOnFailureListener(Throwable::printStackTrace);*/


    }

    /*private void addAdminIntoFirestore2(Tribu tribu) {

        mTribusCollection
                .document(tribu.getKey())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    Admin admin = new Admin();
                    admin.setUidAdmin(tribu.getAdmin().getUidAdmin());
                    admin.setDate(tribu.getProfile().getCreationDate());
                    admin.setTribuKey(tribu.getKey());

                    mTribusCollection
                            .document(tribu.getKey())
                            .collection(ADMIN)
                            .document(tribu.getAdmin().getUidAdmin())
                            .set(admin)
                            .addOnFailureListener(Throwable::printStackTrace);

                })
                .addOnFailureListener(Throwable::printStackTrace);

    }

    public void addTribuIntoFirestore(){

        mReferenceTribus
                .child("@nomadesdigitais")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Tribu tribuNomades = dataSnapshot.getValue(Tribu.class);
                            assert tribuNomades != null;
                            addTribu(tribuNomades);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.getMessage();
                    }
                });

        mReferenceTribus
                .child("@thebigbangtheory")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Tribu tribuBigBang = dataSnapshot.getValue(Tribu.class);
                        assert tribuBigBang != null;
                        addTribu(tribuBigBang);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.getMessage();
                    }
                });

        mReferenceTribus
                .child("@videosmusicadashakirawakawakathis")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Tribu tribuVideo = dataSnapshot.getValue(Tribu.class);
                        assert tribuVideo != null;
                        addTribu(tribuVideo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.getMessage();
                    }
                });

    }

    private void addTribu(Tribu tribu){
        mTribusCollection
                .document(tribu.getKey())
                .set(tribu)
                .addOnSuccessListener(aVoid -> {
                    addAdminIntoFirestore2(tribu);
                })
                .addOnFailureListener(Throwable::printStackTrace);

    }*/

    public Observable<String> verifyTribuUniqueName(String uniqueName) {
        return Observable.create(subscriber -> {
            mTribusUniqueNameCollection
                    .document(uniqueName)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (/*queryDocumentSnapshots == null ||*/ !queryDocumentSnapshots.exists()) {
                            subscriber.onNext(null);
                        } else {

                            String tribuUniqueName = queryDocumentSnapshots.getId();

                            subscriber.onNext(tribuUniqueName);

                        }
                    })
                    .addOnFailureListener(Throwable::printStackTrace);
        });
    }

    public Observable<String> verifyUsername(String username) {
        return Observable.create(subscriber -> {
            mUsersnameCollection
                    .document(username)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots == null) {
                            subscriber.onNext(null);
                        } else {

                            subscriber.onNext(username);

                        }
                    })
                    .addOnFailureListener(Throwable::printStackTrace);
        });
    }

    public void addAdminIntoFirestore(Tribu tribu) {

        Admin admin = new Admin(tribu.getProfile().getUniqueName(), tribu.getAdmin().getUidAdmin());
        admin.setDate(tribu.getDate());


        mTribusCollection
                .document(tribu.getKey())
                .collection(ADMIN)
                .document(tribu.getAdmin().getUidAdmin())
                .set(admin)
                .addOnFailureListener(e -> {

                    e.printStackTrace();
                    Toast.makeText(mContext, "Erro ao cadastrar admin.", Toast.LENGTH_SHORT).show();

                });

    }

    public void addUsernameIntoFirestore() {

        mReferenceUsernames
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot usersNames : dataSnapshot.getChildren()) {

                            String username = usersNames.getKey();
                            String name = usersNames.getValue(String.class);


                            Map<String, Object> setUsersName = new HashMap<>();
                            setUsersName.put(username, name);

                            mUsersnameCollection
                                    .document(username)
                                    .set(setUsersName)
                                    .addOnFailureListener(Throwable::printStackTrace);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("tribu: ", "erro add usersName: " + databaseError.getMessage());
                    }
                });
    }

    //ADD USER TO FIRESTORE
    public void addUserIntoFirestore(User user) {

        mUsersCollection
                .document(user.getId())
                .set(user);
    }

    //ADD PARTICIPANTS
    public void addParticipantsIntoFirestore(Follower follower, String TribuKey) {

        mTribusCollection
                .document(TribuKey)
                .collection(TRIBUS_PARTICIPANTS)
                .document(follower.getUidFollower())
                .set(follower);

    }

    //ADD PARTICIPATING
    public void addParticipatingIntoFirestore(String userId, String tribuKey, Tribu participating) {

        Tribu newFollower = new Tribu();
        newFollower.setDate(participating.getDate());
        newFollower.setKey(tribuKey);


        mUsersCollection
                .document(userId)
                .collection(PARTICIPATING)
                .document(tribuKey)
                .set(newFollower);
    }


    //ADD TOPICS
    public void addTopicsIntoFirebase(String tribuKey, ConversationTopic topic) {

        mTribusCollection
                .document(tribuKey)
                .collection(TOPICS)
                .document(topic.getKey())
                .set(topic);
    }


    //ADD MESSAGES INTO THOSE TOPICS
    public void addMessagesIntoTopics(String tribuKey, String topicKey, MessageUser message) {

        mTribusCollection
                .document(tribuKey)
                .collection(TOPICS)
                .document(topicKey)
                .collection(TOPIC_MESSAGES)
                .document(message.getKey())
                .set(message)
                .addOnFailureListener(e -> {
                    Log.e("tribu: ", "Erro ao enviar menssagem: " + e.getMessage());
                });
    }

    public void addSurveysIntoFirestore(String tribuKey, Survey survey) {
        mTribusCollection
                .document(tribuKey)
                .collection(SURVEYS)
                .document(survey.getKey())
                .set(survey);
    }

    public void addVoteIntoSurvey(String tribuKey, Survey survey, SurveyVote surveyVote, String surveyVoteKey) {

        mTribusCollection
                .document(tribuKey)
                .collection(SURVEYS)
                .document(survey.getKey())
                .collection(SURVEY_VOTES)
                .document(surveyVoteKey)
                .set(surveyVote);
    }


    //GET CURRENT USER
    public Observable<User> getCurrentUser(String userId) {

        return Observable.create(subscriber -> {

            mUsersCollection
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        subscriber.onNext(documentSnapshot.toObject(User.class));

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        subscriber.onNext(null);
                    });
        });

    }

    //GET CURRENT TRIBU
    public Observable<Tribu> getTribu(String tribuKey) {

        return Observable.create(subscriber -> {

            mTribusCollection
                    .document(tribuKey)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        subscriber.onNext(documentSnapshot.toObject(Tribu.class));

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        subscriber.onNext(null);
                    });
        });

    }

    //GET ADMIN
    public Observable<User> getAdmin(String adminId) {

        return Observable.create(subscriber -> {

            mUsersCollection
                    .document(adminId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        subscriber.onNext(documentSnapshot.toObject(User.class));

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        subscriber.onNext(null);
                    });
        });

    }

    public Observable<Tribu> getTribuFollower(String currentUserId, String tribuKey) {

        return Observable.create(subscriber -> {

            mUsersCollection
                    .document(currentUserId)
                    .collection(PARTICIPATING)
                    .document(tribuKey)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {
                            subscriber.onNext(documentSnapshot.toObject(Tribu.class));
                        } else {
                            subscriber.onNext(null);
                        }

                    })
                    .addOnFailureListener(Throwable::printStackTrace);


        });
    }


    //GET CONTACT USER
    public Observable<User> getContactUser(String contactId, String currentUserId) {

        return Observable.create(subscriber -> {

            mUsersCollection
                    .document(currentUserId)
                    .collection(CONTACTS)
                    .document(contactId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot != null && documentSnapshot.exists()) {

                            mUsersCollection
                                    .document(contactId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot2 -> {

                                        subscriber.onNext(documentSnapshot2.toObject(User.class));

                                    })
                                    .addOnFailureListener(e -> {
                                        e.printStackTrace();
                                        subscriber.onNext(null);
                                    });

                        } else {
                            subscriber.onNext(null);
                        }

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        subscriber.onNext(null);
                    });
        });

    }

    //GET USER WAITING PERMISSION
    public Observable<User> getUserWaitingPermission(String contactId, String currentUserId) {

        return Observable.create(subscriber -> {

            mUsersCollection
                    .document(currentUserId)
                    .collection(USER_PERMISSIONS)
                    .document(contactId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            mUsersCollection
                                    .document(contactId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot2 -> {

                                        subscriber.onNext(documentSnapshot2.toObject(User.class));

                                    })
                                    .addOnFailureListener(e -> {
                                        e.printStackTrace();
                                        subscriber.onNext(null);
                                    });

                        } else {
                            subscriber.onNext(null);
                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        subscriber.onNext(null);
                    });
        });

    }

    //GET USER INVITED
    public Observable<User> getUserInvited(String contactId, String currentUserId) {

        return Observable.create(subscriber -> {

            mUsersCollection
                    .document(currentUserId)
                    .collection(USER_INVITATIONS)
                    .document(contactId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot != null && documentSnapshot.exists()) {

                            mUsersCollection
                                    .document(contactId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot2 -> {

                                        subscriber.onNext(documentSnapshot2.toObject(User.class));

                                    })
                                    .addOnFailureListener(e -> {
                                        e.printStackTrace();
                                        subscriber.onNext(null);
                                    });

                        } else {
                            subscriber.onNext(null);
                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        subscriber.onNext(null);
                    });
        });

    }

    //GET USER THAT IS NOT A CONTACT
    public Observable<User> getUserThatIsNotAContact(String currentUserId, String userNotAContactId) {

        return Observable.create(subscriber -> {

            mUsersCollection
                    .document(currentUserId)
                    .collection(CONTACTS)
                    .document(userNotAContactId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot == null || !documentSnapshot.exists()) {

                            mUsersCollection
                                    .document(userNotAContactId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot2 -> {

                                        subscriber.onNext(documentSnapshot2.toObject(User.class));

                                    })
                                    .addOnFailureListener(e -> {
                                        e.printStackTrace();
                                        subscriber.onNext(null);
                                    });

                        } else {
                            subscriber.onNext(null);
                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        subscriber.onNext(null);
                    });
        });

    }

    //GET USER NUM TRIBUS
    public Observable<Integer> getNumTribus(String contactId) {

        return Observable.create(subscriber -> {

            mUsersCollection
                    .document(contactId)
                    .collection(PARTICIPATING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            subscriber.onNext(queryDocumentSnapshots.size());

                        } else {
                            subscriber.onNext(0);
                        }

                    })

                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        subscriber.onNext(null);
                    });
        });

    }

    //GET USER NUM CONTACTS
    public Observable<Integer> getNumContacts(String contactId) {

        return Observable.create(subscriber -> {

            mUsersCollection
                    .document(contactId)
                    .collection(CONTACTS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            subscriber.onNext(queryDocumentSnapshots.size());

                        } else {
                            subscriber.onNext(0);
                        }

                    })

                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        subscriber.onNext(null);
                    });
        });

    }


}