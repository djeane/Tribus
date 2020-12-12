package apptribus.com.tribus.activities.main_activity.fragment_talks.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_talks.mvp.TalksFragmentPresenter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.mvp.TalksFragmentView;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.GetTimeAgo;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.CONTACTS;
import static apptribus.com.tribus.util.Constantes.CONTACTS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.IMAGE;
import static apptribus.com.tribus.util.Constantes.LINK;
import static apptribus.com.tribus.util.Constantes.TEXT;
import static apptribus.com.tribus.util.Constantes.VIDEO;
import static apptribus.com.tribus.util.Constantes.VOICE;

public class ContactsAddedFragmentAdapter extends RecyclerView.Adapter<ContactsAddedFragmentAdapter.ContactsAddedViewHolder>{

    private Context mContext;
    private List<Talk> mContactsList;
    private TalksFragmentView mView;
    private ContactsAddedAdapterClickListener mListener;


    public ContactsAddedFragmentAdapter(Context context, List<Talk> contactsList, TalksFragmentView view,
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
    public ContactsAddedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_talks, parent, false);

        return new ContactsAddedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAddedViewHolder holder, int position) {

        Talk contact = mContactsList.get(position);

        holder.initContactsAddedFragmentVH(contact);

    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }




    public class ContactsAddedViewHolder extends RecyclerView.ViewHolder{

        private Context mContext;

        @BindView(R.id.circle_image_of_talker)
        public SimpleDraweeView mCircleContactImage;

        @BindView(R.id.constraint_layout)
        public ConstraintLayout mConstraintLayout;

        @BindView(R.id.tv_name_of_talker)
        TextView mTvContactName;

        @BindView(R.id.tv_username)
        TextView mTvContactUsername;

        @BindView(R.id.iv_online)
        public ImageView mIvOnline;

        @BindView(R.id.tv_count_badge)
        public TextView mTvCountBadge;

        @BindView(R.id.tv_data)
        public TextView mTvData;

        @BindView(R.id.tv_message)
        public TextView mTvMessage;

        //FIRESTORE INSTANCE
        private FirebaseFirestore mFirestore;

        //FIRESTORE COLLECTIONS REFERENCES
        private CollectionReference mUsersCollection;

        //FIREBASE INSTANCES
        private FirebaseAuth mAuth;


        public ContactsAddedViewHolder(View itemView) {
            super(itemView);

            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

            mCircleContactImage.bringToFront();
            mConstraintLayout.invalidate();

            mAuth = FirebaseAuth.getInstance();
            mFirestore = FirebaseFirestore.getInstance();
            mUsersCollection = mFirestore.collection(GENERAL_USERS);

        }

        private void initContactsAddedFragmentVH(Talk contact){

            mUsersCollection
                    .document(contact.getTalkerId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        User userContact = documentSnapshot.toObject(User.class);

                        if (userContact.getThumb() != null) {
                            setContactsImage(userContact.getThumb());
                        }
                        else {
                            setContactsImage(userContact.getImageUrl());
                        }

                        setContactName(userContact.getName());
                        setTvContactUsername(userContact.getUsername());
                        getLastMessage(contact);

                        if (userContact.isOnline()) {
                            //CHECK INTERNET CONNECTION
                            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                mIvOnline.setVisibility(INVISIBLE);
                            } else {
                                mIvOnline.setVisibility(VISIBLE);

                            }
                        } else {
                            mIvOnline.setVisibility(INVISIBLE);
                        }

                        itemView.setOnClickListener(v -> {
                            mListener.openChatUser(userContact.getId());
                        });

                        mCircleContactImage.setOnClickListener(v -> {
                            openContactImage(userContact);
                        });

                        mIvOnline.setOnClickListener(v -> {
                            showToast(userContact);
                        });


                    })
                    .addOnFailureListener(Throwable::printStackTrace);

        }

        private void getLastMessage(Talk contact){
            if(mView.mFragment.getActivity() != null) {
                mUsersCollection
                        .document(mAuth.getCurrentUser().getUid())
                        .collection(CONTACTS)
                        .document(contact.getTalkerId())
                        .collection(CONTACTS_MESSAGES)
                        .orderBy(DATE, Query.Direction.DESCENDING)
                        .limit(1)
                        .addSnapshotListener(mView.mFragment.getActivity(), (queryDocumentSnapshots, e) -> {

                            if (e != null) {
                                return;
                            }

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                                    MessageUser message = dc.getDocument().toObject(MessageUser.class);

                                    setMessage(message);

                                }
                            } else {
                                mTvMessage.setText("Não há mensagem no momento.");
                            }

                        });

            }

        }

        private void setMessage(MessageUser message){

            switch (message.getContentType()){
                case TEXT:
                case LINK:
                    mTvMessage.setText(message.getMessage());
                    break;

                case IMAGE:
                    mTvMessage.setText("Mensagem com imagem.");
                    break;

                case VIDEO:
                    mTvMessage.setText("Mensagem de vídeo.");
                    break;

                case VOICE:
                    mTvMessage.setText("Mensagem cde áudio.");
                    break;
            }

        }

        private void setContactsImage(String url) {

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
                    .setOldController(mCircleContactImage.getController())
                    .build();
            mCircleContactImage.setController(dc);

        }

        private void setTvContactUsername(String contactUsername) {
            mTvContactUsername.setText(contactUsername);
        }


        //SET NAME OF TALKER
        private void setContactName(String contactName) {
            mTvContactName.setText(contactName);
        }

        private void setTvCountBadge(int countBadge) {
            String countUnreadMessages = String.valueOf(countBadge);
            mTvCountBadge.setText(countUnreadMessages);
        }

        private void setLastMessageDate(Date date) {
            String time = GetTimeAgo.getTimeAgo(date, mContext);
            mTvData.setText(time);

        }

        //SET LAST MESSAGE
        private void setTvMessageChildAdded(MessageUser messageUser){

            if (messageUser != null && messageUser.getContentType() != null) {
                switch (messageUser.getContentType()) {
                    case "TEXT":
                    case "LINK":
                        mTvMessage.setText(messageUser.getMessage());
                        break;
                    case "VOICE":
                        String messageAudio = "Mensagem de Audio";
                        mTvMessage.setText(messageAudio);
                        break;
                    case "IMAGE":
                        String messageImage = "Mensagem com Imagem";
                        mTvMessage.setText(messageImage);
                        break;
                    case "VIDEO": {
                        String messageVideo = "Mensagem de Video";
                        mTvMessage.setText(messageVideo);
                        break;
                    }
                    default: {
                        String emptyMessage = "Não há mensagem no momento.";
                        mTvMessage.setText(emptyMessage);
                        break;
                    }
                }

            }
            else {
                if (mTvMessage != null) {
                    String emptyMessage = "Não há mensagem no momento.";
                    mTvMessage.setText(emptyMessage);
                }
            }
        }

        private void openContactImage(User user) {

            //CREATE DIALOG TO SHOW NEW IMAGE
            //CONFIGURATION OF DIALOG
            AlertDialog.Builder builder = new AlertDialog.Builder(mView.mFragment.getActivity(), R.style.MyDialogTheme);
            LayoutInflater inflater = mView.mFragment.getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_image_tribu, null);
            SimpleDraweeView mSdImageTalker = dialogView.findViewById(R.id.sd_image_tribu);
            TextView mTvNameOfTribu = dialogView.findViewById(R.id.tv_name_of_tribu);
            TextView mTvUniqueName = dialogView.findViewById(R.id.tv_unique_name);
            TextView mTvAdminSince = dialogView.findViewById(R.id.tv_created_date);


            mTvNameOfTribu.setText(user.getName());
            mTvUniqueName.setText(user.getUsername());
            builder.setView(dialogView);

        /*if (mTribu.getTimestampCreated() != null) {
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM HH:mm");
            String time = sfd.format(new Date(mTribu.getTimestampCreatedLong()));

            String appendTime = "Criada em: " + time;
            Log.d("Valor: ", "appendTime: " + appendTime);
            mTvAdminSince.setText(appendTime);

        } else {*/
            String time = GetTimeAgo.getTimeAgo(user.getDate(), mView.getContext());
            String append = "Perfil criado ";
            String appendDate = append + time;
            mTvAdminSince.setText(appendDate);
            //}

            //Log.d("Valor: ", "image - View: " + admin.getImageUrl());
            ControllerListener listener = new BaseControllerListener() {
                @Override
                public void onFailure(String id, Throwable throwable) {
                    super.onFailure(id, throwable);
                    //Log.d("Valor: ", "onFailure - View: " + throwable.toString());

                }

                @Override
                public void onIntermediateImageFailed(String id, Throwable throwable) {
                    super.onIntermediateImageFailed(id, throwable);
                }

                @Override
                public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                }

                @Override
                public void onIntermediateImageSet(String id, Object imageInfo) {
                    super.onIntermediateImageSet(id, imageInfo);
                }

                @Override
                public void onRelease(String id) {
                    super.onRelease(id);
                }

                @Override
                public void onSubmit(String id, Object callerContext) {
                    super.onSubmit(id, callerContext);
                    //Log.d("Valor: ", "onSubmit");

                }
            };
            DraweeController dc = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse(user.getImageUrl()))
                    .setControllerListener(listener)
                    .setOldController(mSdImageTalker.getController())
                    .build();
            mSdImageTalker.setController(dc);

            AlertDialog dialog = builder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams wmlp = dialog.getWindow()
                    .getAttributes();
            wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            wmlp.gravity = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
            dialog.getWindow().setGravity(wmlp.gravity);


            dialog.show();

        }

        private void showToast(User userContact) {

            String message = userContact.getName() + " está online";

            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }

    }


    public interface ContactsAddedAdapterClickListener{

        void openChatUser(String contactId);
    }
}
