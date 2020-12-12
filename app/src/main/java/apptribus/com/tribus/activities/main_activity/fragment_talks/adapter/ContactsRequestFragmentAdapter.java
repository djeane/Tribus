package apptribus.com.tribus.activities.main_activity.fragment_talks.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.invitations_request_user.mvp.InvitationRequestUserPresenter;
import apptribus.com.tribus.activities.invitations_request_user.mvp.InvitationRequestUserView;
import apptribus.com.tribus.activities.main_activity.fragment_talks.mvp.TalksFragmentPresenter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.mvp.TalksFragmentView;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;

public class ContactsRequestFragmentAdapter extends RecyclerView.Adapter<ContactsRequestFragmentAdapter.ContactsRequestFragmentViewHolder>{

    private Context mContext;
    private List<Talk> mListContacts;
    private TalksFragmentView mView;
    private InvitationRequestUserListener mListener;

    public ContactsRequestFragmentAdapter(Context context, List<Talk> listContacts, TalksFragmentView view,
                                          TalksFragmentPresenter presenter){
        this.mContext = context;
        this.mListContacts = listContacts;
        this.mView = view;

        if (presenter != null){
            mListener = presenter;
        }
    }

    @NonNull
    @Override
    public ContactsRequestFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_invitation_request_user, parent, false);

        return new ContactsRequestFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsRequestFragmentViewHolder holder, int position) {

        Talk contact = mListContacts.get(position);

        holder.initContactsRequestUserVH(contact);
    }

    @Override
    public int getItemCount() {
        return mListContacts.size();
    }

    public class ContactsRequestFragmentViewHolder extends RecyclerView.ViewHolder{

        private Context mContext;

        @BindView(R.id.constraint_layout)
        ConstraintLayout mConstraintLayout;

        @BindView(R.id.card_invitations_request_user)
        CardView mInvitationsRequest;

        @BindView(R.id.circle_image_of_tribu)
        SimpleDraweeView mImageTribu;

        @BindView(R.id.circle_image_of_user_invited)
        SimpleDraweeView mImageUserInvited;

        @BindView(R.id.tv_name_of_user_invited)
        TextView mTvUsersNameInvited;

        @BindView(R.id.tv_username_user_invited)
        TextView mTvUsernameUserInvited;

        @BindView(R.id.tv_invitations_date)
        TextView mTvInvitationsDate;

        @BindView(R.id.tv_name_of_tribu)
        TextView mTvTribusName;

        @BindView(R.id.tv_unique_name)
        TextView mTvTribusUniqueName;

        @BindView(R.id.btn_cancel_invitation)
        Button mBtnCancelInvitation;

        //FIRESTORE INSTANCE
        private FirebaseFirestore mFirestore;


        //FIRESTORE COLLECTIONS REFERENCES
        private CollectionReference mUsersCollection;
        private CollectionReference mTribusCollection;


        public ContactsRequestFragmentViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

            mImageUserInvited.bringToFront();
            mConstraintLayout.invalidate();

            mFirestore = FirebaseFirestore.getInstance();
            mUsersCollection = mFirestore.collection(GENERAL_USERS);
            mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);

        }

        private void setImageUser(String url) {

            ControllerListener listener = new BaseControllerListener() {
                @Override
                public void onFailure(String id, Throwable throwable) {
                    super.onFailure(id, throwable);
                    //Log.d("Valor: ", "onFailure - id: " + id + "throwable: " + throwable);
                }

                @Override
                public void onIntermediateImageFailed(String id, Throwable throwable) {
                    super.onIntermediateImageFailed(id, throwable);
                    //Log.d("Valor: ", "onIntermediateImageFailed - id: " + id + "throwable: " + throwable);
                }

                @Override
                public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    //Log.d("Valor: ", "onFinalImageSet - id: " + id + "imageInfo: " + imageInfo + "animatable: " + animatable);
                }

                @Override
                public void onIntermediateImageSet(String id, Object imageInfo) {
                    super.onIntermediateImageSet(id, imageInfo);
                    //Log.d("Valor: ", "onIntermediateImageSet - id: " + id + "imageInfo: " + imageInfo);
                }

                @Override
                public void onRelease(String id) {
                    super.onRelease(id);
                    //Log.d("Valor: ", "onRelease - id: " + id);
                }

                @Override
                public void onSubmit(String id, Object callerContext) {
                    super.onSubmit(id, callerContext);
                    //Log.d("Valor: ", "onSubmit - id: " + id + "callerContext: " + callerContext);
                }
            };

            //SCRIPT - LARGURA DA IMAGEM
            //int w = 0;
        /*if (holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.MATCH_PARENT
                || holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.WRAP_CONTENT) {

            Display display = ((MainActivity) mContext).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            try {
                w = size.x;
                Log.d("Valor: ", "Valor da largura(w) em onStart(FragmentPesquisarTribu): " + w);

            } catch (Exception e) {
                w = display.getWidth();
                e.printStackTrace();
            }
        }*/

            Uri uri = Uri.parse(url);
            DraweeController dc = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    //.setControllerListener(listener)
                    .setOldController(mImageUserInvited.getController())
                    .build();
            mImageUserInvited.setController(dc);

        }

        //set username
        private void setTvUsernameUserInvited(String usernameUserInvited) {
            mTvUsernameUserInvited.setText(usernameUserInvited);
        }

        //set User's name
        private void setTvUsersNameInvited(String usersNameInvited) {
            mTvUsersNameInvited.setText(usersNameInvited);
        }

        //Tribu
        //set image
        private void setImageTribu(String url) {

            ControllerListener listener = new BaseControllerListener() {
                @Override
                public void onFailure(String id, Throwable throwable) {
                    super.onFailure(id, throwable);
                    //Log.d("Valor: ", "onFailure - id: " + id + "throwable: " + throwable);
                }

                @Override
                public void onIntermediateImageFailed(String id, Throwable throwable) {
                    super.onIntermediateImageFailed(id, throwable);
                    //Log.d("Valor: ", "onIntermediateImageFailed - id: " + id + "throwable: " + throwable);
                }

                @Override
                public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    //Log.d("Valor: ", "onFinalImageSet - id: " + id + "imageInfo: " + imageInfo + "animatable: " + animatable);
                }

                @Override
                public void onIntermediateImageSet(String id, Object imageInfo) {
                    super.onIntermediateImageSet(id, imageInfo);
                    //Log.d("Valor: ", "onIntermediateImageSet - id: " + id + "imageInfo: " + imageInfo);
                }

                @Override
                public void onRelease(String id) {
                    super.onRelease(id);
                    //Log.d("Valor: ", "onRelease - id: " + id);
                }

                @Override
                public void onSubmit(String id, Object callerContext) {
                    super.onSubmit(id, callerContext);
                    //Log.d("Valor: ", "onSubmit - id: " + id + "callerContext: " + callerContext);
                }
            };

            //SCRIPT - LARGURA DA IMAGEM
            //int w = 0;
        /*if (holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.MATCH_PARENT
                || holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.WRAP_CONTENT) {

            Display display = ((MainActivity) mContext).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            try {
                w = size.x;
                Log.d("Valor: ", "Valor da largura(w) em onStart(FragmentPesquisarTribu): " + w);

            } catch (Exception e) {
                w = display.getWidth();
                e.printStackTrace();
            }
        }*/

            Uri uri = Uri.parse(url);
            DraweeController dc = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    //.setControllerListener(listener)
                    .setOldController(mImageTribu.getController())
                    .build();
            mImageTribu.setController(dc);

        }

        //set mTribu's name
        private void setTvTribusName(String tribusName) {
            mTvTribusName.setText(tribusName);
        }

        //set mTribu's unique name
        private void setTvTribusUniqueName(String tribusUniqueName) {
            mTvTribusUniqueName.setText(tribusUniqueName);
        }

        private void setDate(Date date){
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault());
            String time = sfd.format(date.getTime());

            String appendTime = "Convite enviado em " + time;
            mTvInvitationsDate.setText(appendTime);
        }


        private void initContactsRequestUserVH(Talk contact) {

            mUsersCollection
                    .document(contact.getTalkerId())
                    .get()
                    .addOnSuccessListener(documentSnapshotUser -> {

                        mTribusCollection
                                .document(contact.getTribuKey())
                                .get()
                                .addOnSuccessListener(documentSnapshotTribu -> {

                                    User userContact = documentSnapshotUser.toObject(User.class);

                                    Tribu tribu = documentSnapshotTribu.toObject(Tribu.class);

                                    setTvUsersNameInvited(userContact.getName());
                                    setTvUsernameUserInvited(userContact.getUsername());

                                    if (userContact.getThumb() != null) {
                                        setImageUser(userContact.getThumb());
                                    } else {
                                        setImageUser(userContact.getImageUrl());
                                    }

                                    setTvTribusName(tribu.getProfile().getNameTribu());
                                    setTvTribusUniqueName(tribu.getProfile().getUniqueName());
                                    setImageTribu(tribu.getProfile().getImageUrl());
                                    setDate(contact.getDateInvitation());

                                    mBtnCancelInvitation.setOnClickListener(v -> {
                                        //CHECK INTERNET CONNECTION
                                        if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                            ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                                        } else {
                                            ShowSnackBarInfoInternet.showSnack(true, mView);
                                            mListener.btnCancelInvitation(userContact);
                                        }
                                    });


                                    mTvUsernameUserInvited.setOnClickListener(v -> {
                                        mListener.openUserProfile(contact);
                                    });

                                    mTvUsersNameInvited.setOnClickListener(v -> {
                                        mListener.openUserProfile(contact);
                                    });

                                    mImageUserInvited.setOnClickListener(v -> {
                                        mListener.openUserProfile(contact);
                                    });

                                })
                                .addOnFailureListener(Throwable::printStackTrace);
                    })
                    .addOnFailureListener(Throwable::printStackTrace);

        }

    }

    public interface InvitationRequestUserListener {
        void btnCancelInvitation(User userContactId);

        void openUserProfile(Talk contact);
    }
}
