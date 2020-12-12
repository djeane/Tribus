package apptribus.com.tribus.activities.sharing.view_holder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.conversation_topics.ConversationTopicsActivity;
import apptribus.com.tribus.pojo.Tribu;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.LINK_INTO_MESSAGE;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 12/12/2017.
 */

public class SharingTribusViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;

    @BindView(R.id.row_sharing_tribus)
    CardView mCardView;

    @BindView(R.id.view_row_top)
    View mView;

    @BindView(R.id.constraint_layout)
    ConstraintLayout mConstraintLayout;

    @BindView(R.id.circle_image_of_tribu)
    SimpleDraweeView mImageTribu;

    @BindView(R.id.tv_name_of_tribu)
    TextView mTvNameTribu;

    @BindView(R.id.tv_unique_name)
    TextView mTvUniqueName;

    //INSTANCES - FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    //REFERENCES - FIREBASE
    private DatabaseReference mReferenceTribus;


    public SharingTribusViewHolder(View itemView) {
        super(itemView);

        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);

    }

    private void setImage(String url){

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


    private void setTvNameTribu(String name){
        mTvNameTribu.setText(name);
    }


    private void setTvUniqueName(String uniqueName){
        mTvUniqueName.setText(uniqueName);
    }

    public void initSharingTribusVH(Tribu tribuParam, String mLink, Fragment fragment){

        mReferenceTribus
                .child(tribuParam.getUniqueNameTribu())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Tribu tribu = dataSnapshot.getValue(Tribu.class);

                        if(tribu != null){
                            setImage(tribu.getProfile().getImageUrl());
                            setTvNameTribu(tribu.getProfile().getNameTribu());
                            setTvUniqueName(tribu.getProfile().getUniqueName());


                            mCardView.setOnClickListener(v -> {
                                //openConversationTopicActivity(mTribu, view.mLink, activity);
                                openConversationTopicActivity(tribu, mLink, fragment);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });

    }

    private void openConversationTopicActivity(Tribu tribu, String mLink, Fragment fragment) {

        Intent intent = new Intent(fragment.getActivity(), ConversationTopicsActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        intent.putExtra(LINK_INTO_MESSAGE, mLink);
        fragment.getActivity().startActivity(intent);
        fragment.getActivity().finish();
    }

}
