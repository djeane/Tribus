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

public class ContactsInvitationsFragmentAdapter extends RecyclerView.Adapter<ContactsInvitationsFragmentAdapter.ContactsInvitationsFragmentViewHolder>{

    private Context mContext;
    private List<Talk> mContactsList;
    private TalksFragmentView mView;
    private ContactsInvitationdFragmentAdapterListener mListener;


    public ContactsInvitationsFragmentAdapter(Context context, List<Talk> contactsList, TalksFragmentView view,
                                              TalksFragmentPresenter presenter){
        this.mContext = context;
        this.mContactsList = contactsList;
        this.mView = view;

        if (presenter != null){
            mListener = presenter;
        }
    }

    @NonNull
    @Override
    public ContactsInvitationsFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_talker_waiting_permission, parent, false);

        return new ContactsInvitationsFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsInvitationsFragmentViewHolder holder, int position) {

        Talk contact = mContactsList.get(position);

        holder.initContactsInvitationsVH(contact);
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }

    public class ContactsInvitationsFragmentViewHolder extends RecyclerView.ViewHolder{


        private Context mContext;

        @BindView(R.id.constraint_layout)
        ConstraintLayout mConstraintLayout;

        @BindView(R.id.card_user_waiting_permission)
        CardView mCardUserWaitingPermission;

        @BindView(R.id.circle_image_of_user)
        SimpleDraweeView mImageUser;

        @BindView(R.id.circle_image_of_tribu)
        SimpleDraweeView mImageTribu;

        @BindView(R.id.tv_name_of_user)
        TextView mTvUsersName;

        @BindView(R.id.tv_username)
        TextView mTvUsername;

        @BindView(R.id.tv_request_date)
        TextView mTvRequestSince;

        @BindView(R.id.tv_name_of_tribu)
        TextView mTvNameOfTribu;

        @BindView(R.id.tv_unique_name)
        TextView mTvUniqueNameOfTribu;

        @BindView(R.id.btn_accept_follower)
        Button mBtnAcceptTalker;

        @BindView(R.id.btn_deny)
        Button mBtnDenyInvitation;

        //FIRESTORE INSTANCE
        private FirebaseFirestore mFirestore;

        //FIRESTORE COLLECTIONS REFERENCES
        private CollectionReference mUsersCollection;
        private CollectionReference mTribusCollection;



        public ContactsInvitationsFragmentViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

            mImageUser.bringToFront();
            mConstraintLayout.invalidate();

            mFirestore = FirebaseFirestore.getInstance();
            mUsersCollection = mFirestore.collection(GENERAL_USERS);
            mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);

        }

        private void setImageUser(String url){

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
                    .setOldController(mImageUser.getController())
                    .build();
            mImageUser.setController(dc);

        }

        private void setTvUsernameUserPermission(String usernameUserPermission){
            mTvUsername.setText(usernameUserPermission);
        }

        private void setTvUsersName(String usersName){
            mTvUsersName.setText(usersName);
        }

        private void setImageTribu(String url){

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


        private void setDate(Date date){
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault());
            String time = sfd.format(date.getTime());

            mTvRequestSince.setText(time);
        }

        //set Tribu's name
        private void setTvNameOfTribu(String nameOfTribu){
            mTvNameOfTribu.setText(nameOfTribu);
        }

        //set Tribu's unique name
        private void setTvUniqueOfTribu(String uniqueNameOfTribu){
            mTvUniqueNameOfTribu.setText(uniqueNameOfTribu);
        }


        private void initContactsInvitationsVH(Talk contact) {

            mUsersCollection
                    .document(contact.getTalkerId())
                    .get().addOnSuccessListener(documentSnapshot -> {

                User userContact = documentSnapshot.toObject(User.class);

                mTribusCollection
                        .document(contact.getTribuKey())
                        .get()
                        .addOnSuccessListener(documentSnapshot1 -> {

                            Tribu tribu = documentSnapshot1.toObject(Tribu.class);

                            setTvUsersName(userContact.getName());
                            setTvUsernameUserPermission(userContact.getUsername());

                            if (userContact.getThumb() != null) {
                                setImageUser(userContact.getThumb());
                            } else {
                                setImageUser(userContact.getImageUrl());
                            }

                            setImageTribu(tribu.getProfile().getImageUrl());
                            setTvNameOfTribu(tribu.getProfile().getNameTribu());
                            setTvUniqueOfTribu(tribu.getProfile().getUniqueName());
                            setDate(contact.getDateInvitation());


                            mBtnAcceptTalker.setOnClickListener(v -> {
                                //CHECK INTERNET CONNECTION
                                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                    ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                                } else {
                                    ShowSnackBarInfoInternet.showSnack(true, mView);
                                    mListener.btnAcceptContactOnClickListener(contact, userContact);
                                }
                            });

                            mBtnDenyInvitation.setOnClickListener(v -> {
                                //CHECK INTERNET CONNECTION
                                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                    ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                                } else {
                                    ShowSnackBarInfoInternet.showSnack(true, mView);
                                    mListener.btnDenyInvitationOnClickListener(contact, userContact);
                                }
                            });

                            mImageUser.setOnClickListener(v -> {
                                mListener.openUserProfileOnClickListener(contact);
                            });

                            mTvUsersName.setOnClickListener(v -> {
                                mListener.openUserProfileOnClickListener(contact);
                            });

                            mTvUsername.setOnClickListener(v -> {
                                mListener.openUserProfileOnClickListener(contact);
                            });
                        })
                        .addOnFailureListener(Throwable::printStackTrace);

            })
                    .addOnFailureListener(Throwable::printStackTrace);

        }
    }

    public interface ContactsInvitationdFragmentAdapterListener {
        void btnAcceptContactOnClickListener(Talk contact, User userContact);
        void btnDenyInvitationOnClickListener(Talk contact, User userContact);
        void openUserProfileOnClickListener(Talk contact);

    }
}
