package apptribus.com.tribus.activities.main_activity.fragment_tribus.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp.TribusFragmentPresenter;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp.TribusFragmentView;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.GetTimeAgo;
import apptribus.com.tribus.util.firestore.FirestoreService;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;

public class TribusFragmentAdapter extends RecyclerView.Adapter<TribusFragmentAdapter.TribusFragmentViewHolder>{

    private Context mContext;
    private List<Tribu> mTribusList;
    private TribusFragmentView mView;
    private TribusFragmentAdapterItemClickListener mListener;

    public TribusFragmentAdapter(Context context, List<Tribu> tribusList, TribusFragmentView view, TribusFragmentPresenter presenter){

        this.mContext = context;
        this.mTribusList = tribusList;
        this.mView = view;

        if (presenter != null){
            mListener = presenter;
        }
    }



    @NonNull
    @Override
    public TribusFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tribus, parent, false);

        return new TribusFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TribusFragmentViewHolder holder, int position) {

        Tribu tribu = mTribusList.get(position);

        holder.initTribusFragmentViewHolder(tribu);

    }

    @Override
    public int getItemCount() {
        return mTribusList.size();
    }






    public class TribusFragmentViewHolder extends RecyclerView.ViewHolder{

        private Context mContext;

        @BindView(R.id.row_tribus)
        CardView mCardView;

        @BindView(R.id.constraint_layout)
        ConstraintLayout mConstraintLayout;

        @BindView(R.id.circle_image_of_tribu)
        SimpleDraweeView mCircleImageTribu;

        @BindView(R.id.tv_name_of_tribu)
        TextView mTvNameTribu;

        @BindView(R.id.tv_unique_name)
        TextView mTvUniqueName;

        @BindView(R.id.tv_description)
        TextView mTvDescription;

        //FIRESTORE INSTANCE
        private FirebaseFirestore mFirestore;

        //FIRESTORE COLLECTIONS REFERENCES
        private CollectionReference mTribusCollection;


        public TribusFragmentViewHolder(View itemView) {
            super(itemView);

            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

            mFirestore = FirebaseFirestore.getInstance();
            mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);


        }


        //SET IMAGE OF TRIBU
        private void setImageOfTribu(String url){

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
                    .setOldController(mCircleImageTribu.getController())
                    .build();
            mCircleImageTribu.setController(dc);

        }


        //SET DESCRIPTION
        private void setUniqueName(String uniqueName){
            mTvUniqueName.setText(uniqueName);
        }

        private void setTribusDescription(String description){
            mTvDescription.setText(description);
        }

        //SET THEMATIC
        private void setNameTribu(String nameTribu){
            mTvNameTribu.setText(nameTribu);
        }


        private void initTribusFragmentViewHolder(Tribu tribuParam){

            mTribusCollection
                    .document(tribuParam.getKey())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        Tribu tribu = documentSnapshot.toObject(Tribu.class);

                        //VIEWS VISIBILITY
                        mCardView.setVisibility(View.VISIBLE);
                        mConstraintLayout.setVisibility(View.VISIBLE);
                        mCircleImageTribu.setVisibility(View.VISIBLE);
                        mCircleImageTribu.bringToFront();
                        mConstraintLayout.invalidate();
                        mTvNameTribu.setVisibility(View.VISIBLE);
                        mTvUniqueName.setVisibility(View.VISIBLE);

                        setNameTribu(tribu.getProfile().getNameTribu());
                        setUniqueName(tribu.getProfile().getUniqueName());
                        setImageOfTribu(tribu.getProfile().getImageUrl());
                        setTribusDescription(tribu.getProfile().getDescription());


                        itemView.setOnClickListener(v -> {
                            mListener.openFeatureChoiceActivity(tribu.getKey(), tribu.getProfile().getUniqueName());
                        });

                        mCircleImageTribu.setOnClickListener(v -> {
                            openImageTribu(tribu);
                        });

                    })
                    .addOnFailureListener(Throwable::printStackTrace);



        }


        private void openImageTribu(Tribu tribu) {

            //CREATE DIALOG TO SHOW NEW IMAGE
            //CONFIGURATION OF DIALOG
            AlertDialog.Builder builder = new AlertDialog.Builder(mView.mContext.getActivity(), R.style.MyDialogTheme);
            LayoutInflater inflater = mView.mContext.getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_image_tribu, null);
            SimpleDraweeView mSdImageTribu = dialogView.findViewById(R.id.sd_image_tribu);
            TextView mTvNameOfTribu = dialogView.findViewById(R.id.tv_name_of_tribu);
            TextView mTvUniqueName = dialogView.findViewById(R.id.tv_unique_name);
            TextView mTvAdminSince = dialogView.findViewById(R.id.tv_created_date);


            mTvNameOfTribu.setText(tribu.getProfile().getNameTribu());
            mTvUniqueName.setText(tribu.getProfile().getUniqueName());
            builder.setView(dialogView);

        /*if (tribu.getTimestampCreated() != null) {
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM", Locale.getDefault());
            String time = sfd.format(new Date(tribu.getTimestampCreatedLong()));

            String appendTime = "Criada em " + time;
            mTvAdminSince.setText(appendTime);

        } else {*/
            String time = GetTimeAgo.getTimeAgo(tribu.getProfile().getCreationDate(), mView.mContext.getActivity());
            String append = "Criada ";
            String appendDate = append + time;
            mTvAdminSince.setText(appendDate);
            //}

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
                    .setUri(Uri.parse(tribu.getProfile().getImageUrl()))
                    .setControllerListener(listener)
                    .setOldController(mSdImageTribu.getController())
                    .build();
            mSdImageTribu.setController(dc);

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

    }


    public interface TribusFragmentAdapterItemClickListener{
        void openFeatureChoiceActivity(String tribuKey, String tribuUniqueName);
    }

}
