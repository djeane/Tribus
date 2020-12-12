package apptribus.com.tribus.activities.invitation_request_tribu.adapter;

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
import apptribus.com.tribus.activities.invitation_request_tribu.mvp.InvitationRequestTribuPresenter;
import apptribus.com.tribus.activities.invitation_request_tribu.mvp.InvitationRequestTribuView;
import apptribus.com.tribus.pojo.RequestTribu;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;

public class InvitationRequestTribuAdapter extends RecyclerView.Adapter<InvitationRequestTribuAdapter.InvitationRequestTribuViewHolder> {

    private Context mContext;
    private List<RequestTribu> mListRequests;
    private InvitationRequestTribuView mView;
    private InvitationRequestTribuListener mListener;

    public InvitationRequestTribuAdapter(Context context, List<RequestTribu> listRequests, InvitationRequestTribuView view,
                                         InvitationRequestTribuPresenter presenter) {

        this.mContext = context;
        this.mListRequests = listRequests;
        this.mView = view;

        if (presenter != null) {
            mListener = presenter;
        }
    }


    @NonNull
    @Override
    public InvitationRequestTribuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_invitation_request_tribu, parent, false);

        return new InvitationRequestTribuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationRequestTribuViewHolder holder, int position) {

        RequestTribu requestTribu = mListRequests.get(position);

        holder.initInvitationRequestTribuVH(requestTribu);
    }

    @Override
    public int getItemCount() {
        return mListRequests.size();
    }

    public class InvitationRequestTribuViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;

        @BindView(R.id.card_invitation_request_tribu)
        CardView mCardInvitationRequestTribu;

        @BindView(R.id.circle_image_of_tribu_requested)
        SimpleDraweeView mImageTribu;

        @BindView(R.id.tv_name_of_tribu_requested)
        TextView mTvNameOfTribu;

        @BindView(R.id.tv_unique_name_of_tribu_requested)
        TextView mTvUniqueNameOfTribu;

        @BindView(R.id.tv_request_date)
        TextView mTvRequestDate;

        @BindView(R.id.btn_cancel_request)
        Button mBtnCancelRequest;

        //FIRESTORE INSTANCE
        private FirebaseFirestore mFirestore;

        //FIRESTORE COLLECTIONS REFERENCES
        private CollectionReference mTribusCollection;

        public InvitationRequestTribuViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

            mFirestore = FirebaseFirestore.getInstance();
            mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);


        }

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

        //set username
        private void setTvUniqueNameOfTribu(String usernameUserInvited) {
            mTvUniqueNameOfTribu.setText(usernameUserInvited);
        }

        //set User's name
        private void setTvNameOfTribu(String usersNameInvited) {
            mTvNameOfTribu.setText(usersNameInvited);
        }


        public void initInvitationRequestTribuVH(RequestTribu requestTribu) {

            mTribusCollection
                    .document(requestTribu.getTribuKey())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        Tribu tribu = documentSnapshot.toObject(Tribu.class);

                        setTvNameOfTribu(tribu.getProfile().getNameTribu());
                        setTvUniqueNameOfTribu(tribu.getProfile().getUniqueName());
                        setImageTribu(tribu.getProfile().getImageUrl());

                        mBtnCancelRequest.setOnClickListener(v -> {
                            //CHECK INTERNET CONNECTION
                            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                            } else {
                                ShowSnackBarInfoInternet.showSnack(true, mView);
                                mListener.btnCancelRequestOnClikListener(tribu);
                            }
                        });

                        mTvUniqueNameOfTribu.setOnClickListener(v -> {
                            mListener.openProfileTribuOnClickListener(tribu.getKey());
                        });


                        mTvNameOfTribu.setOnClickListener(v -> {
                            mListener.openProfileTribuOnClickListener(tribu.getKey());
                        });


                        mImageTribu.setOnClickListener(v -> {
                            mListener.openProfileTribuOnClickListener(tribu.getKey());
                        });

                    })
                    .addOnFailureListener(Throwable::printStackTrace);

        }
    }

    public interface InvitationRequestTribuListener {
        void btnCancelRequestOnClikListener(Tribu tribu);

        void openProfileTribuOnClickListener(String tribuKey);
    }
}
