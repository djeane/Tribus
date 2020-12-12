package apptribus.com.tribus.activities.block_user.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
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

import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.block_user.mvp.BlockUserPresenter;
import apptribus.com.tribus.activities.block_user.mvp.BlockUserView;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;

public class BlockUserAdapter extends RecyclerView.Adapter<BlockUserAdapter.BlockUserViewHolder>{

    private Context mContext;
    private List<Talk> mContactsList;
    private BlockUserView mView;
    private OnBlockUserAdapterListener mOnBlockUserAdapterListener;

    public BlockUserAdapter(Context context, List<Talk> contactsList, BlockUserView view, BlockUserPresenter presenter){
        this.mContext = context;
        this.mContactsList = contactsList;
        this.mView = view;
        if (presenter != null){
            mOnBlockUserAdapterListener = presenter;
        }
    }


    @NonNull
    @Override
    public BlockUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_block_talkers, parent, false);

        return new BlockUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockUserViewHolder holder, int position) {

        Talk contact = mContactsList.get(position);

        holder.initViewHolder(contact, mView);
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }

    public class BlockUserViewHolder extends RecyclerView.ViewHolder{

        private Context mContext;

        @BindView(R.id.card_block_talkers)
        public CardView mCardBlockTalkers;

        @BindView(R.id.circle_image_of_talker)
        public SimpleDraweeView mImageTalkers;

        @BindView(R.id.tv_name_of_talker)
        public TextView mTvTalkersName;

        @BindView(R.id.tv_username_talker)
        public TextView mTvUsernameTalkers;

        @BindView(R.id.tv_talker_since)
        public TextView mTvTalkerSince;

        @BindView(R.id.btn_remove_talker)
        public Button mBtnRemoveTalker;

        //FIRESTORE INSTANCE
        private FirebaseFirestore mFirestore;


        //FIRESTORE COLLECTIONS REFERENCES
        private CollectionReference mUsersCollection;

        public BlockUserViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);
            mFirestore = FirebaseFirestore.getInstance();
            mUsersCollection = mFirestore.collection(GENERAL_USERS);

        }

        private void setImageTalker(String url){

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
                    .setControllerListener(listener)
                    .setOldController(mImageTalkers.getController())
                    .build();
            mImageTalkers.setController(dc);

        }

        //set username
        private void setTvUsernameTalkers(String usernameTalkers){
            mTvUsernameTalkers.setText(usernameTalkers);
        }

        //set Talker's name
        private void setTvTalkersName(String talkersName){
            mTvTalkersName.setText(talkersName);
        }


        public void initViewHolder(Talk contact, BlockUserView view){

            mUsersCollection
                    .document(contact.getTalkerId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        User userTalker = documentSnapshot.toObject(User.class);


                        setTvTalkersName(userTalker.getName());
                        setTvUsernameTalkers(userTalker.getUsername());

                        if (userTalker.getThumb() != null) {
                            setImageTalker(userTalker.getThumb());
                        }
                        else {
                            setImageTalker(userTalker.getImageUrl());
                        }

                        mBtnRemoveTalker.setOnClickListener(v -> {

                            //CHECK INTERNET CONNECTION
                            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                            } else {
                                ShowSnackBarInfoInternet.showSnack(true, view);
                                mOnBlockUserAdapterListener.btnRemoveContactOnClickListener(userTalker);
                                //showDialog(userTalker);
                            }

                        });

                        //LISTENER TO OPEN CONTACT ACTIVITY
                        mImageTalkers.setOnClickListener(v -> {
                            mOnBlockUserAdapterListener.openContactProfiletOnClickListener(contact);

                        });

                        mTvTalkersName.setOnClickListener(v -> {
                            mOnBlockUserAdapterListener.openContactProfiletOnClickListener(contact);

                        });

                        mTvUsernameTalkers.setOnClickListener(v -> {
                            mOnBlockUserAdapterListener.openContactProfiletOnClickListener(contact);

                        });


                    })
                    .addOnFailureListener(Throwable::printStackTrace);

        }

    }

    public interface OnBlockUserAdapterListener {
        void btnRemoveContactOnClickListener(User contact);
        void openContactProfiletOnClickListener(Talk contact);
    }
}
