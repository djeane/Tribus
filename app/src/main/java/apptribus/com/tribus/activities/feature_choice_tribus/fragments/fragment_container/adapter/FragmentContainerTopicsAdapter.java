package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.mvp.FragmentContainerPresenter;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.mvp.FragmentContainerView;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.GetTimeAgo;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.EDITED;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TOPIC;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_MESSAGES;

public class FragmentContainerTopicsAdapter extends RecyclerView.Adapter<FragmentContainerTopicsAdapter.FragmentContainerTopicsViewHolder> {

    private Context mContext;
    private List<ConversationTopic> mTopicsList;
    private FragmentContainerView mView;
    private String mTribuKey;
    private String mTopicKey;
    private String mTribuName;
    private String mTopicName;
    private String mTribuUniqueName;

    private OnShowTopicsViewHolderListener mListener;

    public FragmentContainerTopicsAdapter(Context context, List<ConversationTopic> topicsList, FragmentContainerView view,
                                          FragmentContainerPresenter presenter, String tribuKey) {
        this.mContext = context;
        this.mTopicsList = topicsList;
        this.mView = view;
        this.mTribuKey = tribuKey;

        if (presenter != null) {
            mListener = presenter;
        }
    }


    @NonNull
    @Override
    public FragmentContainerTopicsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_topic_conversation, parent, false);

        return new FragmentContainerTopicsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentContainerTopicsViewHolder holder, int position) {

        ConversationTopic topic = mTopicsList.get(position);

        holder.initShowTopicsViewHolder(topic);

    }

    @Override
    public int getItemCount() {
        return mTopicsList.size();
    }


    public class FragmentContainerTopicsViewHolder extends RecyclerView.ViewHolder{

        private Context mContext;

        private View mItemView;

        @BindView(R.id.relative_content)
        RelativeLayout mRelativeContent;

        @BindView(R.id.circle_image_of_participant)
        SimpleDraweeView mCircleImagePartipant;

        @BindView(R.id.textViewOptions)
        TextView mTvOptions;

        @BindView(R.id.view1)
        View mView1;

        @BindView(R.id.tv_name)
        TextView mTvName;

        @BindView(R.id.tv_topic)
        TextView mTvTopic;

        @BindView(R.id.topic_time)
        TextView mTvTopicTime;

        private long numMessages = 0;

        private ProgressDialog progress;

        //INSTANCES - FIREBASE
        private FirebaseAuth mAuth;

        //FIRESTORE INSTANCE
        private FirebaseFirestore mFirestore;

        //FIRESTORE COLLECTION
        private CollectionReference mTribusCollection;
        private CollectionReference mUsersCollection;


        public FragmentContainerTopicsViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

            mItemView = itemView;

            mAuth = FirebaseAuth.getInstance();

            mCircleImagePartipant.bringToFront();
            mRelativeContent.invalidate();

            mFirestore = FirebaseFirestore.getInstance();
            mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
            mUsersCollection = mFirestore.collection(GENERAL_USERS);

            progress = new ProgressDialog(itemView.getContext());

        }

        private void setTvTopic(String topic, long numTopics) {

            int index = topic.length();

            SpannableString styledTopic = new SpannableString(topic);
            styledTopic.setSpan(
                    new UnderlineSpan(),
                    0,
                    index,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            String appendTopic = styledTopic + " (" + numTopics + ")";
            mTvTopic.setText(appendTopic);

        }

        private void setName(String username, String name) {
            if (username != null || name != null) {
                String[] firstName = name.split(" ");
                String appendNameAndUsername = "por " + firstName[0] + " (" + username + ")";
                mTvName.setText(appendNameAndUsername);
            }
        }

        private void setTvTopicTime(Date date) {
            if (date != null) {
                String time = GetTimeAgo.getTimeAgo(date, mContext);
                mTvTopicTime.setText(time);
            }
        }


        private void setImageParticipant(String url) {

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
                    .setOldController(mCircleImagePartipant.getController())
                    .build();
            mCircleImagePartipant.setController(dc);

        }


        private void initShowTopicsViewHolder(ConversationTopic topic) {

            //GET TRIBU
            mTribusCollection
                    .document(mTribuKey)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        Tribu tribu = documentSnapshot.toObject(Tribu.class);

                        mTopicKey = topic.getKey();
                        mTopicName = topic.getTopic();
                        mTribuName = tribu.getProfile().getNameTribu();
                        mTribuUniqueName = tribu.getProfile().getUniqueName();

                        //GET NUMBER OF MESSAGES FROM TOPICS
                        mTribusCollection
                                .document(mTribuKey)
                                .collection(TOPICS)
                                .document(topic.getKey())
                                .collection(TOPIC_MESSAGES)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {

                                    if (queryDocumentSnapshots != null || !queryDocumentSnapshots.isEmpty()) {
                                        numMessages = queryDocumentSnapshots.getDocuments().size();
                                    }

                                    mUsersCollection
                                            .document(topic.getIdParticipant())
                                            .get()
                                            .addOnSuccessListener(documentSnapshotUser -> {

                                                User user = documentSnapshotUser.toObject(User.class);


                                                mView1.setVisibility(VISIBLE);
                                                setName(user.getUsername(), user.getName());
                                                setTvTopic(topic.getTopic(), numMessages);
                                                setTvTopicTime(topic.getDate());
                                                setImageParticipant(user.getThumb());

                                                mItemView.setOnClickListener(v -> {
                                                    if (mListener != null) {
                                                        mListener.itemTopicClickListener(tribu.getProfile().getUniqueName(),
                                                                topic.getKey(), tribu.getKey(), tribu.getProfile().getNameTribu(),
                                                                topic.getTopic());
                                                    }

                                                });

                                                if (mAuth.getCurrentUser().getUid().equals(topic.getIdParticipant())) {

                                                    mTvOptions.setVisibility(VISIBLE);

                                                    mTvOptions.setOnClickListener(v -> {

                                                                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                                                    ShowSnackBarInfoInternet.showToastInfoInternet(mView.getContext());
                                                                } else {
                                                                    setTvOptionsMenu(topic, tribu, mView);

                                                                }
                                                            }
                                                    );
                                                }

                                            })
                                            .addOnFailureListener(Throwable::printStackTrace);


                                })
                                .addOnFailureListener(Throwable::printStackTrace);


                    })
                    .addOnFailureListener(Throwable::printStackTrace);


        }

        private void setTvOptionsMenu(ConversationTopic topic, Tribu tribu, FragmentContainerView view) {

            PopupMenu popup = new PopupMenu(mContext, mTvOptions);
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_poput_topic_options);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()) {
                    case R.id.action_topic_edit:

                        openDialogTopic(tribu, topic, view);

                        return true;
                    case R.id.action_close_topic:
                        //handle menu2 click
                        return true;
                    default:
                        return false;
                }
            });
            //displaying the popup
            popup.show();

        }

        private void openDialogTopic(Tribu tribu, ConversationTopic topic, FragmentContainerView view) {

            String oldTopic = topic.getTopic();

            if (tribu != null) {
                //CONFIGURATION OF DIALOG
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.MyDialogTheme);
                LayoutInflater inflater = view.mFragment.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_edit_topic, null);

                EditText mEtTopic = dialogView.findViewById(R.id.et_topic);
                mEtTopic.setText(topic.getTopic().trim());
                mEtTopic.setSelection(mEtTopic.getText().length());
                TextView mTvCharacterCount = dialogView.findViewById(R.id.tv_character_count);
                Button mBtnCancel = dialogView.findViewById(R.id.btn_cancel);
                Button mBtnEditTopic = dialogView.findViewById(R.id.btn_edit_topic);

                builder.setView(dialogView);

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

                mEtTopic.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        String appendCount = String.valueOf(s.length())
                                + view.mContext.getResources().getString(R.string.character_count);
                        mTvCharacterCount.setText(appendCount);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                //CLICK LISTENER TO BUTTONS
                mBtnEditTopic.setOnClickListener(v -> {
                    //CHECK INTERNET CONNECTION
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                    } else {
                        ShowSnackBarInfoInternet.showSnack(true, view);

                        String newTopic = mEtTopic.getText().toString().trim();

                        if (newTopic.equals("")) {
                            Toast.makeText(view.mContext, view.mContext.getResources().getString(R.string.toast_alert_empty_subject),
                                    Toast.LENGTH_LONG).show();
                        } else if (oldTopic.equals(newTopic)) {
                            Toast.makeText(view.mContext, view.mContext.getResources().getString(R.string.toast_no_topic_update),
                                    Toast.LENGTH_LONG).show();
                        } else {

                            sendTopicToFirebase(tribu, newTopic, view.mContext, topic);
                        }

                        dialog.dismiss();

                    }
                });

                mBtnCancel.setOnClickListener(v -> {
                    dialog.dismiss();
                });

            }
        }

        private void sendTopicToFirebase(Tribu tribu, String newTopic, Context fragment, ConversationTopic oldTopic) {

            progress = new ProgressDialog(fragment);
            progress.setCancelable(false);
            progress.setMessage(fragment.getResources().getString(R.string.progress_updating_topic));

            showProgressDialog(true);

            Map<String, Object> updateTopic = new HashMap<>();
            updateTopic.put(TOPIC, newTopic);
            updateTopic.put(EDITED, true);

            mTribusCollection
                    .document(tribu.getKey())
                    .collection(TOPICS)
                    .document(oldTopic.getKey())
                    .update(updateTopic)
                    .addOnSuccessListener(aVoid -> {
                        showProgressDialog(false);
                        Toast.makeText(fragment,
                                fragment.getResources().getString(R.string.toast_update_topic_successfully),
                                Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        showProgressDialog(false);
                        e.printStackTrace();
                        Toast.makeText(fragment, fragment.getResources().getString(R.string.toast_error_update_topic),
                                Toast.LENGTH_LONG).show();
                    });

        }

        //SHOW PROGRESS
        private void showProgressDialog(boolean load) {

            if (load) {
                progress.show();
            } else {
                progress.dismiss();
            }
        }


    }

    public interface OnShowTopicsViewHolderListener {
        void itemTopicClickListener(String tribuUniqueName, String topicKey, String tribuKey, String tribuName, String topicName);
    }
}
