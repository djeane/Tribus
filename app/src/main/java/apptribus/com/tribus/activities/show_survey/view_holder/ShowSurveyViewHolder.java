package apptribus.com.tribus.activities.show_survey.view_holder;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.pojo.ChosenOptionSurvey;
import apptribus.com.tribus.pojo.Survey;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.GetTimeAgo;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.SURVEY_VOTE;
import static apptribus.com.tribus.util.Constantes.TRIBUS_SURVEY;

/**
 * Created by User on 1/19/2018.
 */

public class ShowSurveyViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;

    @BindView(R.id.tv_creation_date)
    TextView mTvCreationDate;

    @BindView(R.id.circle_user_image)
    SimpleDraweeView mSdCircleImageUser;

    @BindView(R.id.tv_user_name)
    TextView mTvUserName;

    @BindView(R.id.tv_question)
    TextView mTvQuestion;

    @BindView(R.id.linear_one)
    LinearLayout mLinearOne;

    @BindView(R.id.tv_option_one)
    TextView mTvOptionOne;

    @BindView(R.id.tv_percent_one)
    TextView mTvPercentOne;

    @BindView(R.id.linear_two)
    LinearLayout mLinearTwo;

    @BindView(R.id.tv_option_two)
    TextView mTvOptionTwo;

    @BindView(R.id.tv_percent_two)
    TextView mTvPercentTwo;

    @BindView(R.id.linear_three)
    LinearLayout mLinearThree;

    @BindView(R.id.tv_option_three)
    TextView mTvOptionThree;

    @BindView(R.id.tv_percent_three)
    TextView mTvPercentThree;

    @BindView(R.id.linear_four)
    LinearLayout mLinearFour;

    @BindView(R.id.tv_option_four)
    TextView mTvOptionFour;

    @BindView(R.id.tv_percent_four)
    TextView mTvPercentFour;

    @BindView(R.id.linear_five)
    LinearLayout mLinearFive;

    @BindView(R.id.tv_option_five)
    TextView mTvOptionFive;

    @BindView(R.id.tv_percent_five)
    TextView mTvPercentFive;

    @BindView(R.id.tv_limit_date)
    TextView mTvLimitDate;

    @BindView(R.id.btn_vote)
    Button mBtnVote;

    //FIREBASE
    //INSTANCES - FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUser;
    private DatabaseReference mReferenceSurvey;
    private DatabaseReference mReferenceSurveyVote;

    private ProgressDialog progress;



    public ShowSurveyViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
        mReferenceSurvey = mDatabase.getReference().child(TRIBUS_SURVEY);
        mReferenceSurveyVote = mDatabase.getReference().child(SURVEY_VOTE);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(true);
    }


    public void initShowSurveyViewHolder(Survey survey, Tribu tribu, AppCompatActivity activity) {

        mReferenceUser
                .child(survey.getIdUser())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshotUser) {

                        mReferenceSurveyVote
                                .child(tribu.getProfile().getUniqueName())
                                .child(survey.getKey())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshotSurveyVote) {

                                        User user = dataSnapshotUser.getValue(User.class);

                                        assert user != null;
                                        if (user.getThumb() != null){
                                            setUserImage(user.getImageUrl());
                                        }
                                        else {
                                            setUserImage(user.getThumb());
                                        }

                                        setCreationDate(survey.getDate());

                                        setSurveyQuestion(survey.getQuestion());

                                        setUserName(user.getName(), user.getUsername());

                                        setTvOptionsAnswers(survey);

                                        if (dataSnapshotSurveyVote.hasChild(mAuth.getCurrentUser().getUid())){
                                            mBtnVote.setEnabled(false);
                                            mBtnVote.setText("Você já votou nesta enquete.");
                                        }
                                        else {
                                            mBtnVote.setOnClickListener(v -> {
                                                openSurveyDialog(survey, activity, tribu);
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        databaseError.toException().printStackTrace();
                                    }
                                });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });


    }

    private void openSurveyDialog(Survey survey, AppCompatActivity activity, Tribu tribu) {

        if(survey != null) {
            //CONFIGURATION OF DIALOG
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_survey_vote, null);

            TextView mTvSurveyTitle = dialogView.findViewById(R.id.tv_survey_title);
            String appendSurveyTitle = "ENQUETE de " + tribu.getProfile().getNameTribu();
            mTvSurveyTitle.setText(appendSurveyTitle);

            TextView mTvSurveyQuestion = dialogView.findViewById(R.id.tv_survey_question);
            mTvSurveyQuestion.setText(survey.getQuestion());

            RadioButton mRbOptionOne = dialogView.findViewById(R.id.rb_option_one);
            RadioButton mRbOptionTwo  = dialogView.findViewById(R.id.rb_option_two);
            RadioButton mRbOptionThree  = dialogView.findViewById(R.id.rb_option_three);
            RadioButton mRbOptionFour  = dialogView.findViewById(R.id.rb_option_four);
            RadioButton mRbOptionFive  = dialogView.findViewById(R.id.rb_option_five);
            Button mBtnCancel = dialogView.findViewById(R.id.btn_cancel);
            Button mBtnVote = dialogView.findViewById(R.id.btn_vote);

            mRbOptionOne.setText(survey.getFirstAnswer());
            mRbOptionTwo.setText(survey.getSecondAnswer());

            setRadioButtonsVisibility(survey, mRbOptionThree, mRbOptionFour, mRbOptionFive);

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

            mRbOptionOne.setOnClickListener(v -> {
                mBtnVote.setEnabled(true);
                //CLICK LISTENER TO BUTTONS
                mBtnVote.setOnClickListener(v1 -> {
                    //CHECK INTERNET CONNECTION
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                    } else {
                        updateSurveyVoteToFirebase(survey, tribu, activity, survey.getNumVotesFirstAnswer(), 1);
                    }

                    dialog.dismiss();

                });

            });
            mRbOptionTwo.setOnClickListener(v -> {
                mBtnVote.setEnabled(true);
                //CLICK LISTENER TO BUTTONS
                mBtnVote.setOnClickListener(v1 -> {
                    //CHECK INTERNET CONNECTION
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                    } else {
                        updateSurveyVoteToFirebase(survey, tribu, activity, survey.getNumVotesSecondAnswer(), 2);
                    }

                    dialog.dismiss();

                });

            });
            mRbOptionThree.setOnClickListener(v -> {
                mBtnVote.setEnabled(true);
                //CLICK LISTENER TO BUTTONS
                mBtnVote.setOnClickListener(v1 -> {
                    //CHECK INTERNET CONNECTION
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                    } else {
                        updateSurveyVoteToFirebase(survey, tribu, activity, survey.getNumVotesThirdAnswer(), 3);
                    }

                    dialog.dismiss();

                });

            });
            mRbOptionFour.setOnClickListener(v -> {
                mBtnVote.setEnabled(true);
                //CLICK LISTENER TO BUTTONS
                mBtnVote.setOnClickListener(v1 -> {
                    //CHECK INTERNET CONNECTION
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                    } else {
                        updateSurveyVoteToFirebase(survey, tribu, activity, survey.getNumVotesFourthAnswer(), 4);
                    }

                    dialog.dismiss();

                });

            });
            mRbOptionFive.setOnClickListener(v -> {
                mBtnVote.setEnabled(true);
                //CLICK LISTENER TO BUTTONS
                mBtnVote.setOnClickListener(v1 -> {
                    //CHECK INTERNET CONNECTION
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                    } else {
                        updateSurveyVoteToFirebase(survey, tribu, activity, survey.getNumVotesFifthAnswer(), 5);
                    }

                    dialog.dismiss();

                });

            });


            mBtnCancel.setOnClickListener(v -> {
                dialog.dismiss();
            });

        }
    }

    private void updateSurveyVoteToFirebase(Survey survey, Tribu tribu, AppCompatActivity activity,
                                            long numVotesChosenAnswer, int numberChosenAnswer) {

        progress = new ProgressDialog(activity);
        progress.setCancelable(false);
        progress.setMessage("Aguarde enquanto seu voto é registrado...");


        Map<String, Object> updateSurvey = new HashMap<>();
        long votes = survey.getTotVotes() + 1;
        long numVotes = numVotesChosenAnswer + 1;
        updateSurvey.put("totVotes", votes);

        switch (numberChosenAnswer){
            case 1:
                updateSurvey.put("numVotesFirstAnswer", numVotes);
                break;
            case 2:
                updateSurvey.put("numVotesSecondAnswer", numVotes);
                break;
            case 3:
                updateSurvey.put("numVotesThirdAnswer", numVotes);
                break;
            case 4:
                updateSurvey.put("numVotesFourthAnswer", numVotes);
                break;
            case 5:
                updateSurvey.put("numVotesFifthAnswer", numVotes);
                break;
        }

        showProgressDialog(true);

        ChosenOptionSurvey optionSurvey = new ChosenOptionSurvey();
        Date date = new Date(System.currentTimeMillis());
        optionSurvey.setDate(date);
        optionSurvey.setOption(survey.getFirstAnswer());

        mReferenceSurveyVote
                .child(tribu.getProfile().getUniqueName())
                .child(survey.getKey())
                .child(mAuth.getCurrentUser().getUid())
                .setValue(optionSurvey)
                .addOnSuccessListener(aVoid -> {
                    mReferenceSurvey
                            .child(tribu.getProfile().getUniqueName())
                            .child(survey.getKey())
                            .updateChildren(updateSurvey)
                            .addOnSuccessListener(aVoid2 -> {
                                showProgressDialog(false);
                                Toast.makeText(activity, "Voto registrado com sucesso!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                showProgressDialog(false);
                                e.getLocalizedMessage();
                            });

                })
                .addOnFailureListener(e -> {
                    showProgressDialog(false);
                    e.getLocalizedMessage();
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

    private void setRadioButtonsVisibility(Survey survey, RadioButton mRbOptionThree, RadioButton mRbOptionFour,
                                           RadioButton mRbOptionFive) {

        switch (survey.getTotAnswers()){
            case 3:
                mRbOptionThree.setVisibility(View.VISIBLE);
                mRbOptionThree.setText(survey.getThirdAnswer());
                break;
            case 4:
                mRbOptionThree.setVisibility(View.VISIBLE);
                mRbOptionThree.setText(survey.getThirdAnswer());
                mRbOptionFour.setVisibility(View.VISIBLE);
                mRbOptionFour.setText(survey.getFourthAnswer());
                break;
            case 5:
                mRbOptionThree.setVisibility(View.VISIBLE);
                mRbOptionThree.setText(survey.getThirdAnswer());
                mRbOptionFour.setVisibility(View.VISIBLE);
                mRbOptionFour.setText(survey.getFourthAnswer());
                mRbOptionFive.setVisibility(View.VISIBLE);
                mRbOptionFive.setText(survey.getFifthAnswer());
                break;
        }

    }


    private void setTvOptionsAnswers(Survey survey) {

        switch (survey.getTotAnswers()){
            case 2:
                setTvOptionsTwoAnswers(survey);
                break;
            case 3:
                setTvOptionsThreeAnswers(survey);
                break;
            case 4:
                setTvOptionsFourAnswers(survey);
                break;
            case 5:
                setTvOptionsFiveAnswers(survey);
                break;
        }

    }

    private void setTvOptionsTwoAnswers(Survey survey) {

        String appendTotPercentVotesOne = null;
        String appendTotPercentVotesTwo = null;

        //ONE
        mTvOptionOne.setText(survey.getFirstAnswer());

        if (survey.getNumVotesFirstAnswer() <= 1) {
            appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFirstAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFirstAnswer() + " votos)");
        }
        mTvPercentOne.setText(appendTotPercentVotesOne);

        //TWO
        mTvOptionTwo.setText(survey.getSecondAnswer());

        if (survey.getNumVotesSecondAnswer() <= 1) {
            appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesSecondAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesSecondAnswer() + " votos)");
        }

        mTvPercentTwo.setText(appendTotPercentVotesTwo);


    }

    private void setTvOptionsThreeAnswers(Survey survey) {

        String appendTotPercentVotesOne = null;
        String appendTotPercentVotesTwo = null;
        String appendTotPercentVotesThree = null;

        //ONE
        mTvOptionOne.setText(survey.getFirstAnswer());

        if (survey.getNumVotesFirstAnswer() <= 1) {
            appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFirstAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFirstAnswer() + " votos)");
        }
        mTvPercentOne.setText(appendTotPercentVotesOne);

        //TWO
        mTvOptionTwo.setText(survey.getSecondAnswer());

        if (survey.getNumVotesSecondAnswer() <= 1) {
            appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesSecondAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesSecondAnswer() + " votos)");
        }

        mTvPercentTwo.setText(appendTotPercentVotesTwo);


        //THREE
        mTvOptionThree.setText(survey.getThirdAnswer());

        if (survey.getNumVotesThirdAnswer() <= 1) {
            appendTotPercentVotesThree = String.valueOf(getPercentVote(survey.getNumVotesThirdAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesThirdAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesThree = String.valueOf(getPercentVote(survey.getNumVotesThirdAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesThirdAnswer() + " votos)");
        }

        mTvPercentThree.setText(appendTotPercentVotesThree);

    }

    private void setTvOptionsFourAnswers(Survey survey) {

        String appendTotPercentVotesOne = null;
        String appendTotPercentVotesTwo = null;
        String appendTotPercentVotesThree = null;
        String appendTotPercentVotesFour = null;

        //ONE
        mTvOptionOne.setText(survey.getFirstAnswer());

        if (survey.getNumVotesFirstAnswer() <= 1) {
            appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFirstAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFirstAnswer() + " votos)");
        }
        mTvPercentOne.setText(appendTotPercentVotesOne);

        //TWO
        mTvOptionTwo.setText(survey.getSecondAnswer());

        if (survey.getNumVotesSecondAnswer() <= 1) {
            appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesSecondAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesSecondAnswer() + " votos)");
        }

        mTvPercentTwo.setText(appendTotPercentVotesTwo);


        //THREE
        mTvOptionThree.setText(survey.getThirdAnswer());

        if (survey.getNumVotesThirdAnswer() <= 1) {
            appendTotPercentVotesThree = String.valueOf(getPercentVote(survey.getNumVotesThirdAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesThirdAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesThree = String.valueOf(getPercentVote(survey.getNumVotesThirdAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesThirdAnswer() + " votos)");
        }

        mTvPercentThree.setText(appendTotPercentVotesThree);

        //FOUR
        mTvOptionFour.setText(survey.getFourthAnswer());

        if (survey.getNumVotesFourthAnswer() <= 1) {
            appendTotPercentVotesFour = String.valueOf(getPercentVote(survey.getNumVotesFourthAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFourthAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesFour = String.valueOf(getPercentVote(survey.getNumVotesFourthAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFourthAnswer() + " votos)");
        }

        mTvPercentFour.setText(appendTotPercentVotesFour);

    }

    private void setTvOptionsFiveAnswers(Survey survey) {

        String appendTotPercentVotesOne = null;
        String appendTotPercentVotesTwo = null;
        String appendTotPercentVotesThree = null;
        String appendTotPercentVotesFour = null;
        String appendTotPercentVotesFive = null;

        //ONE
        mTvOptionOne.setText(survey.getFirstAnswer());

        if (survey.getNumVotesFirstAnswer() <= 1) {
            appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFirstAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFirstAnswer() + " votos)");
        }
        mTvPercentOne.setText(appendTotPercentVotesOne);

        //TWO
        mTvOptionTwo.setText(survey.getSecondAnswer());

        if (survey.getNumVotesSecondAnswer() <= 1) {
            appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesSecondAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesSecondAnswer() + " votos)");
        }

        mTvPercentTwo.setText(appendTotPercentVotesTwo);


        //THREE
        mTvOptionThree.setText(survey.getThirdAnswer());

        if (survey.getNumVotesThirdAnswer() <= 1) {
            appendTotPercentVotesThree = String.valueOf(getPercentVote(survey.getNumVotesThirdAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesThirdAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesThree = String.valueOf(getPercentVote(survey.getNumVotesThirdAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesThirdAnswer() + " votos)");
        }

        mTvPercentThree.setText(appendTotPercentVotesThree);

        //FOUR
        mTvOptionFour.setText(survey.getFourthAnswer());

        if (survey.getNumVotesFourthAnswer() <= 1) {
            appendTotPercentVotesFour = String.valueOf(getPercentVote(survey.getNumVotesFourthAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFourthAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesFour = String.valueOf(getPercentVote(survey.getNumVotesFourthAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFourthAnswer() + " votos)");
        }

        mTvPercentFour.setText(appendTotPercentVotesFour);

        //FIVE
        mTvOptionFive.setText(survey.getFifthAnswer());

        if (survey.getNumVotesFifthAnswer() <= 1) {
            appendTotPercentVotesFive = String.valueOf(getPercentVote(survey.getNumVotesFifthAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFifthAnswer() + " voto)");
        }
        else {
            appendTotPercentVotesFive = String.valueOf(getPercentVote(survey.getNumVotesFifthAnswer(), survey.getTotVotes()) + "%" +
                    " (" + survey.getNumVotesFifthAnswer() + " votos)");
        }

        mTvPercentFive.setText(appendTotPercentVotesFive);

    }


    private int getPercentVote(long numVotes, long totVotes){
        if (totVotes != 0) {
            return (int) (numVotes / totVotes * 100);
        }
        else {
            return 0;
        }
    }

    private void setUserName(String name, String username) {
        String[] firstName = name.split(" ");
        String appendNameAndUsername = "por " + firstName[0] + " (" + username + ")";
        mTvUserName.setText(appendNameAndUsername);
    }

    private void setSurveyQuestion(String question) {
        mTvQuestion.setText(question);
    }

    private void setUserImage(String userCircleImage) {

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

            Uri uri = Uri.parse(userCircleImage);
            DraweeController dc = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setControllerListener(listener)
                    .setOldController(mSdCircleImageUser.getController())
                    .build();
            mSdCircleImageUser.setController(dc);

    }

    private void setCreationDate(Date date) {

        String time = GetTimeAgo.getTimeAgo(date, mContext);
        String appendDate = "criada " + time;
        mTvCreationDate.setText(appendDate);

    }
}
