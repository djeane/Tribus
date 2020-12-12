package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
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
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.mvp.FragmentContainerView;
import apptribus.com.tribus.pojo.ChosenOptionSurvey;
import apptribus.com.tribus.pojo.Survey;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.GetTimeAgo;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.NUM_VOTES_FIFTH_ANSWER;
import static apptribus.com.tribus.util.Constantes.NUM_VOTES_FIRST_ANSWER;
import static apptribus.com.tribus.util.Constantes.NUM_VOTES_FOURTH_ANSWER;
import static apptribus.com.tribus.util.Constantes.NUM_VOTES_SECOND_ANSWER;
import static apptribus.com.tribus.util.Constantes.NUM_VOTES_THIRD_ANSWER;
import static apptribus.com.tribus.util.Constantes.SURVEY;
import static apptribus.com.tribus.util.Constantes.SURVEYS;
import static apptribus.com.tribus.util.Constantes.SURVEY_VOTES;
import static apptribus.com.tribus.util.Constantes.TOT_VOTES;

public class FragmentContainerSurveysAdapter extends RecyclerView.Adapter<FragmentContainerSurveysAdapter.FragmentContainerSurveysViewHolder> {

    private Context mContext;
    private List<Survey> mSurveysList;
    private FragmentContainerView mView;
    private String mTribusKey;
    private String mSurveyKey;

    public FragmentContainerSurveysAdapter(Context context, List<Survey> surveysList, FragmentContainerView view, String tribusKey) {
        this.mContext = context;
        this.mSurveysList = surveysList;
        this.mView = view;
        this.mTribusKey = tribusKey;
    }


    @NonNull
    @Override
    public FragmentContainerSurveysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_show_survey, parent, false);

        return new FragmentContainerSurveysViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentContainerSurveysViewHolder holder, int position) {

        Survey survey = mSurveysList.get(position);

        holder.initShowSurveyViewHolder(survey);
    }

    @Override
    public int getItemCount() {
        return mSurveysList.size();
    }


    public class FragmentContainerSurveysViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;

        @BindView(R.id.tv_creation_date)
        TextView mTvCreationDate;

        @BindView(R.id.circle_user_image)
        SimpleDraweeView mSdCircleImageUser;

        @BindView(R.id.tv_user_name)
        TextView mTvUserName;

        @BindView(R.id.relative_content)
        RelativeLayout mRelativeContent;

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

        @BindView(R.id.btn_cancel_vote)
        Button mBtnCancelVote;

        @BindView(R.id.tv_info_vote)
        TextView mTvInfoVote;

        @BindView(R.id.textViewOptions)
        TextView mTvOptions;


        //FIRESTORE INSTANCE
        private FirebaseFirestore mFirestore;

        //FIRESTORE COLLECTIONS REFERENCES
        private CollectionReference mUsersCollection;
        private CollectionReference mTribusCollection;

        //INSTANCES - FIREBASE
        private FirebaseAuth mAuth;
        private ProgressDialog progress;


        public FragmentContainerSurveysViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

            mSdCircleImageUser.bringToFront();
            mRelativeContent.invalidate();

            mAuth = FirebaseAuth.getInstance();
            mFirestore = FirebaseFirestore.getInstance();
            mUsersCollection = mFirestore.collection(GENERAL_USERS);
            mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);

            progress = new ProgressDialog(mContext);
            progress.setCancelable(true);

        }


        public void initShowSurveyViewHolder(Survey survey) {

            mSurveyKey = survey.getKey();

            mUsersCollection
                    .document(survey.getIdUser())
                    .get()
                    .addOnSuccessListener(documentSnapshotUser -> {

                        mTribusCollection
                                .document(mTribusKey)
                                .collection(SURVEY)
                                .document(survey.getKey())
                                .collection(SURVEY_VOTES)
                                .document(mAuth.getCurrentUser().getUid())
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {

                                    User user = documentSnapshotUser.toObject(User.class);

                                    assert user != null;

                                    if (user.getThumb() != null) {
                                        setUserImage(user.getThumb());
                                    } else {
                                        setUserImage(user.getImageUrl());
                                    }

                                    setCreationDate(survey.getDate());

                                    setSurveyQuestion(survey.getQuestion());

                                    setUserName(user.getName(), user.getUsername());

                                    setTvOptionsAnswers(survey);


                                    if (documentSnapshot != null && documentSnapshot.exists()) {

                                        mTvInfoVote.setVisibility(VISIBLE);
                                        mBtnCancelVote.setVisibility(View.GONE);
                                        mBtnVote.setVisibility(View.INVISIBLE);

                                        mBtnCancelVote.setOnClickListener(view1 -> {

                                            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                                            } else {
                                                //updateSurveyVoteToFirebase(survey, mTribu, view, survey.getNumVotesFirstAnswer(), 1);
                                            }

                                        });
                                    } else {
                                        mTvInfoVote.setVisibility(View.INVISIBLE);
                                        mBtnCancelVote.setVisibility(View.GONE);
                                        mBtnVote.setVisibility(VISIBLE);
                                        mBtnVote.setOnClickListener(v -> {

                                            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                                ShowSnackBarInfoInternet.showToastInfoInternet(mView.getContext());
                                            } else {
                                                openSurveyDialog(survey, mView, mView.mFragment);
                                            }

                                        });
                                    }

                                    if (mAuth.getCurrentUser().getUid().equals(survey.getIdUser())) {
                                        mTvOptions.setVisibility(VISIBLE);

                                        mTvOptions.setOnClickListener(v -> {

                                            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                                ShowSnackBarInfoInternet.showToastInfoInternet(mView.mContext);
                                            } else {
                                                setTvOptionsMenu(survey, mView, mView.mFragment);
                                            }

                                        });
                                    }


                                }).addOnFailureListener(Throwable::printStackTrace);

                    })
                    .addOnFailureListener(Throwable::printStackTrace);


        }

        private void setTvOptionsMenu(Survey survey, FragmentContainerView view, Fragment fragment) {

            PopupMenu popup = new PopupMenu(fragment.getActivity(), mTvOptions);
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_poput_survey_options);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()) {
                    case R.id.action_close_survey:

                        showDialogToCloseSurvey(survey, view, fragment);

                        return true;

                    default:
                        return false;
                }
            });
            //displaying the popup
            popup.show();

        }

        //REMOVE TALKER IF PROFILE PUBLIC
        private void showDialogToCloseSurvey(Survey survey, FragmentContainerView view, Fragment fragment) {
            //INSTANCE OF PROGRESS DIALOG
            progress.setMessage("Aguarde enquanto sua enquete é excluída...");

            //CONFIGURATION OF DIALOG
            AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
            builder.setTitle("Excluir enquete");
            builder.setMessage("Deseja excluir a enquete " + "'" + survey.getQuestion() + "'" + "?");

            String positiveText = "SIM";
            builder.setPositiveButton(positiveText, (dialog, which) -> {
                dialog.dismiss();
                showProgressDialog(true);
                removeSurvey(survey, fragment);

            });
            String negativeText = "NÃO";
            builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();


        }

        private void removeSurvey(Survey survey, Fragment fragment) {

            /*mReferenceSurveyVote
                    .child(tribu.getProfile().getUniqueName())
                    .child(survey.getKey())
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        mReferenceSurvey
                                .child(tribu.getProfile().getUniqueName())
                                .child(survey.getKey())
                                .removeValue()
                                .addOnSuccessListener(aVoid2 -> {
                                    showProgressDialog(false);
                                    Toast.makeText(fragment.getActivity(), "Enquete excluida!", Toast.LENGTH_SHORT).show();
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
                    */


        }


        private void openSurveyDialog(Survey survey, FragmentContainerView view, Fragment fragment) {

            mTribusCollection
                    .document(mTribusKey)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        Tribu tribu = documentSnapshot.toObject(Tribu.class);

                        if (survey != null) {
                            //CONFIGURATION OF DIALOG
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
                            LayoutInflater inflater = fragment.getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.dialog_survey_vote, null);

                            TextView mTvSurveyTitle = dialogView.findViewById(R.id.tv_survey_title);
                            String appendSurveyTitle = fragment.getResources().getString(R.string.survey_from)
                                    + " " + tribu.getProfile().getNameTribu();
                            mTvSurveyTitle.setText(appendSurveyTitle);

                            TextView mTvSurveyQuestion = dialogView.findViewById(R.id.tv_survey_question);
                            mTvSurveyQuestion.setText(survey.getQuestion());

                            RadioButton mRbOptionOne = dialogView.findViewById(R.id.rb_option_one);
                            RadioButton mRbOptionTwo = dialogView.findViewById(R.id.rb_option_two);
                            RadioButton mRbOptionThree = dialogView.findViewById(R.id.rb_option_three);
                            RadioButton mRbOptionFour = dialogView.findViewById(R.id.rb_option_four);
                            RadioButton mRbOptionFive = dialogView.findViewById(R.id.rb_option_five);
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
                                        updateSurveyVoteToFirebase(survey, tribu, survey.getNumVotesFirstAnswer(),
                                                1);
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
                                        updateSurveyVoteToFirebase(survey, tribu, survey.getNumVotesSecondAnswer(),
                                                2);
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
                                        updateSurveyVoteToFirebase(survey, tribu, survey.getNumVotesThirdAnswer(),
                                                3);
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
                                        updateSurveyVoteToFirebase(survey, tribu, survey.getNumVotesFourthAnswer(),
                                                4);
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
                                        updateSurveyVoteToFirebase(survey, tribu, survey.getNumVotesFifthAnswer(),
                                                5);
                                    }

                                    dialog.dismiss();

                                });

                            });


                            mBtnCancel.setOnClickListener(v -> {
                                dialog.dismiss();
                            });

                        }
                    })
                    .addOnFailureListener(Throwable::printStackTrace);


        }

        private void updateSurveyVoteToFirebase(Survey survey, Tribu tribu, long numVotesChosenAnswer, int numberChosenAnswer) {

            progress.setMessage(mView.mFragment.getResources().getString(R.string.progress_vote_survey));


            Map<String, Object> updateSurvey = new HashMap<>();
            long votes = survey.getTotVotes() + 1;
            long numVotes = numVotesChosenAnswer + 1;
            updateSurvey.put(TOT_VOTES, votes);

            switch (numberChosenAnswer) {
                case 1:
                    updateSurvey.put(NUM_VOTES_FIRST_ANSWER, numVotes);
                    break;
                case 2:
                    updateSurvey.put(NUM_VOTES_SECOND_ANSWER, numVotes);
                    break;
                case 3:
                    updateSurvey.put(NUM_VOTES_THIRD_ANSWER, numVotes);
                    break;
                case 4:
                    updateSurvey.put(NUM_VOTES_FOURTH_ANSWER, numVotes);
                    break;
                case 5:
                    updateSurvey.put(NUM_VOTES_FIFTH_ANSWER, numVotes);
                    break;
            }

            showProgressDialog(true);

            ChosenOptionSurvey optionSurvey = new ChosenOptionSurvey();
            Date date = new Date(System.currentTimeMillis());
            optionSurvey.setDate(date);
            optionSurvey.setOption(survey.getFirstAnswer());

            mTribusCollection
                    .document(mTribusKey)
                    .collection(SURVEYS)
                    .document(survey.getKey())
                    .collection(SURVEY_VOTES)
                    .document(mAuth.getCurrentUser().getUid())
                    .set(optionSurvey)
                    .addOnSuccessListener(aVoid -> {
                        showProgressDialog(false);
                        Toast.makeText(mView.getContext(),
                                mView.mFragment.getResources().getString(R.string.toast_vote_registered_successfully),
                                Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        e.getLocalizedMessage();
                        showProgressDialog(false);
                        Toast.makeText(mView.getContext(),
                                mView.mFragment.getResources().getString(R.string.toast_error_registering_vote),
                                Toast.LENGTH_SHORT).show();
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

            switch (survey.getTotAnswers()) {
                case 3:
                    mRbOptionThree.setVisibility(VISIBLE);
                    mRbOptionThree.setText(survey.getThirdAnswer());
                    break;
                case 4:
                    mRbOptionThree.setVisibility(VISIBLE);
                    mRbOptionThree.setText(survey.getThirdAnswer());
                    mRbOptionFour.setVisibility(VISIBLE);
                    mRbOptionFour.setText(survey.getFourthAnswer());
                    break;
                case 5:
                    mRbOptionThree.setVisibility(VISIBLE);
                    mRbOptionThree.setText(survey.getThirdAnswer());
                    mRbOptionFour.setVisibility(VISIBLE);
                    mRbOptionFour.setText(survey.getFourthAnswer());
                    mRbOptionFive.setVisibility(VISIBLE);
                    mRbOptionFive.setText(survey.getFifthAnswer());
                    break;
            }

        }


        private void setTvOptionsAnswers(Survey survey) {

            switch (survey.getTotAnswers()) {
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
                appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFirstAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFirstAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
            }
            mTvPercentOne.setText(appendTotPercentVotesOne);

            //TWO
            mTvOptionTwo.setText(survey.getSecondAnswer());

            if (survey.getNumVotesSecondAnswer() <= 1) {
                appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesSecondAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesSecondAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
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
                appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFirstAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFirstAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
            }
            mTvPercentOne.setText(appendTotPercentVotesOne);

            //TWO
            mTvOptionTwo.setText(survey.getSecondAnswer());

            if (survey.getNumVotesSecondAnswer() <= 1) {
                appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesSecondAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesSecondAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
            }

            mTvPercentTwo.setText(appendTotPercentVotesTwo);


            //THREE
            mTvOptionThree.setText(survey.getThirdAnswer());

            if (survey.getNumVotesThirdAnswer() <= 1) {
                appendTotPercentVotesThree = String.valueOf(getPercentVote(survey.getNumVotesThirdAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesThirdAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesThree = String.valueOf(getPercentVote(survey.getNumVotesThirdAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesThirdAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
            }

            mTvPercentThree.setText(appendTotPercentVotesThree);
            mLinearThree.setVisibility(VISIBLE);

        }

        private void setTvOptionsFourAnswers(Survey survey) {

            String appendTotPercentVotesOne = null;
            String appendTotPercentVotesTwo = null;
            String appendTotPercentVotesThree = null;
            String appendTotPercentVotesFour = null;

            //ONE
            mTvOptionOne.setText(survey.getFirstAnswer());

            if (survey.getNumVotesFirstAnswer() <= 1) {
                appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFirstAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFirstAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
            }
            mTvPercentOne.setText(appendTotPercentVotesOne);

            //TWO
            mTvOptionTwo.setText(survey.getSecondAnswer());

            if (survey.getNumVotesSecondAnswer() <= 1) {
                appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesSecondAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesSecondAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
            }

            mTvPercentTwo.setText(appendTotPercentVotesTwo);


            //THREE
            mTvOptionThree.setText(survey.getThirdAnswer());

            if (survey.getNumVotesThirdAnswer() <= 1) {
                appendTotPercentVotesThree = String.valueOf(getPercentVote(survey.getNumVotesThirdAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesThirdAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesThree = String.valueOf(getPercentVote(survey.getNumVotesThirdAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesThirdAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
            }

            mTvPercentThree.setText(appendTotPercentVotesThree);
            mLinearThree.setVisibility(VISIBLE);

            //FOUR
            mTvOptionFour.setText(survey.getFourthAnswer());

            if (survey.getNumVotesFourthAnswer() <= 1) {
                appendTotPercentVotesFour = String.valueOf(getPercentVote(survey.getNumVotesFourthAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFourthAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesFour = String.valueOf(getPercentVote(survey.getNumVotesFourthAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFourthAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
            }

            mTvPercentFour.setText(appendTotPercentVotesFour);
            mLinearFour.setVisibility(VISIBLE);
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
                appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFirstAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesOne = String.valueOf(getPercentVote(survey.getNumVotesFirstAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFirstAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
            }
            mTvPercentOne.setText(appendTotPercentVotesOne);

            //TWO
            mTvOptionTwo.setText(survey.getSecondAnswer());

            if (survey.getNumVotesSecondAnswer() <= 1) {
                appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesSecondAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesTwo = String.valueOf(getPercentVote(survey.getNumVotesSecondAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesSecondAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
            }

            mTvPercentTwo.setText(appendTotPercentVotesTwo);


            //THREE
            mTvOptionThree.setText(survey.getThirdAnswer());

            if (survey.getNumVotesThirdAnswer() <= 1) {
                appendTotPercentVotesThree = String.valueOf(getPercentVote(survey.getNumVotesThirdAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesThirdAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesThree = String.valueOf(getPercentVote(survey.getNumVotesThirdAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesThirdAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
            }

            mTvPercentThree.setText(appendTotPercentVotesThree);
            mLinearThree.setVisibility(VISIBLE);

            //FOUR
            mTvOptionFour.setText(survey.getFourthAnswer());

            if (survey.getNumVotesFourthAnswer() <= 1) {
                appendTotPercentVotesFour = String.valueOf(getPercentVote(survey.getNumVotesFourthAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFourthAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesFour = String.valueOf(getPercentVote(survey.getNumVotesFourthAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFourthAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
            }

            mTvPercentFour.setText(appendTotPercentVotesFour);
            mLinearFour.setVisibility(VISIBLE);

            //FIVE
            mTvOptionFive.setText(survey.getFifthAnswer());

            if (survey.getNumVotesFifthAnswer() <= 1) {
                appendTotPercentVotesFive = String.valueOf(getPercentVote(survey.getNumVotesFifthAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFifthAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.one_vote));
            } else {
                appendTotPercentVotesFive = String.valueOf(getPercentVote(survey.getNumVotesFifthAnswer(), survey.getTotVotes())
                        + mView.mFragment.getResources().getString(R.string.percent)
                        + " (" + survey.getNumVotesFifthAnswer() + " "
                        + mView.mFragment.getResources().getString(R.string.more_than_one_vote));
            }

            mTvPercentFive.setText(appendTotPercentVotesFive);
            mLinearFive.setVisibility(VISIBLE);
        }


        private int getPercentVote(long numVotes, long totVotes) {
            if (totVotes != 0) {
                return (int) (numVotes / totVotes * 100);
            } else {
                return 0;
            }
        }

        private void setUserName(String name, String username) {
            String[] firstName = name.split(" ");
            String appendNameAndUsername = mView.mFragment.getResources().getString(R.string.by)
                    + " " + firstName[0] + " (" + username + ")";
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
            //String appendDate = "criada " + time;
            mTvCreationDate.setText(time);

        }
    }
}
