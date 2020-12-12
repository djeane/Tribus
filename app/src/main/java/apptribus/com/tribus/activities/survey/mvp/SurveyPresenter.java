package apptribus.com.tribus.activities.survey.mvp;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.Toast;

import apptribus.com.tribus.R;
import apptribus.com.tribus.pojo.Survey;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by User on 1/18/2018.
 */

public class SurveyPresenter {


    private final SurveyView view;
    private final SurveyModel model;
    private CompositeSubscription subscription = new CompositeSubscription();

    private Tribu mTribu;
    private Survey mSurvey;
    public static boolean isOpen;

    //private int mYear, mMonth, mDay;

    ////CHANGE AGE
    //private DatePickerDialog.OnDateSetListener mDateSetListenter;


    public SurveyPresenter(SurveyView view, SurveyModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart(){


        PresenceSystemAndLastSeen.presenceSystem();

        //model.createConversationTopic(view.mTribuUniqueName, mTopicKey);

        subscription.add(observeTribu());
        subscription.add(observeBtnArrowBack());
        subscription.add(observeTvAddOptions());
        subscription.add(observeBtnCreateSurvey());
        //subscription.add(observeTvLimitDate());

        isOpen = true;

    }

    public void onResume(){
        PresenceSystemAndLastSeen.presenceSystem();

    }

    public void onPause() {

        PresenceSystemAndLastSeen.lastSeen();

    }

    public void onStop() {

        isOpen = false;
    }

    public void onRestart() {
        PresenceSystemAndLastSeen.presenceSystem();
    }


    private Subscription observeTribu() {
        return model.getTribu(view.mTribuKey)
                .subscribe(tribu -> {
                            mTribu = tribu;
                            setToolbarFields(tribu);
                        },
                        Throwable::printStackTrace
                );
    }

    private void setToolbarFields(Tribu tribu) {

        view.mTvQuestion.setText(view.mSurveyQuestion);

        String appendTvSurvey = "Nova enquete em " + tribu.getProfile().getNameTribu();

        view.mTvSurvey.setText(appendTvSurvey);
    }

    private Subscription observeBtnArrowBack() {
        return view.observeBtnArrowBack()
                .subscribe(__ -> {
                            view.mContext.finish();
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeTvAddOptions(){
        return view.observeTvAddOptions()
                .subscribe(__ -> {

                    if (view.mEtQuestionThree.getVisibility() != View.VISIBLE){
                        view.mEtQuestionThree.setVisibility(View.VISIBLE);
                    }
                    else if (view.mEtQuestionFour.getVisibility() != View.VISIBLE){
                        view.mEtQuestionFour.setVisibility(View.VISIBLE);
                    }
                    else if(view.mEtQuestionFive.getVisibility() != View.VISIBLE){
                        view.mEtQuestionFive.setVisibility(View.VISIBLE);
                        String noOptionsText = "Sem mais opções de resposta.";
                        view.mTvAddOptions.setText(noOptionsText);
                        view.mTvAddOptions.setEnabled(false);
                    }

                });
    }

    private Subscription observeTvLimitDate(){
        return view.observeTvLimitDate()
                .subscribe();
    }

    private Subscription observeBtnCreateSurvey(){
        return view.observeBtnCreateSurvey()
                .filter(__ -> {
                    if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    }
                    else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .map(__ -> getAnswersOptions())
                .filter(__ -> validateFields())
                .doOnNext(__ -> model.sendSurveyToFirebase(mTribu, mSurvey))
                .subscribe();
    }

    private Survey getAnswersOptions() {

        mSurvey = new Survey();
        mSurvey.setQuestion(view.mSurveyQuestion);

        String firstAnswer = view.mEtQuestionOne.getText().toString().trim();
        String secondAnswer = view.mEtQuestionTwo.getText().toString().trim();
        String thirdAnswer = view.mEtQuestionThree.getText().toString().trim();
        String fourthAnswer = view.mEtQuestionFour.getText().toString().trim();
        String fifthAnswer = view.mEtQuestionFive.getText().toString().trim();

        mSurvey.setFirstAnswer(firstAnswer);
        mSurvey.setSecondAnswer(secondAnswer);
        mSurvey.setTotAnswers(2);

        if (!thirdAnswer.equals("")){
            mSurvey.setTotAnswers(3);
            mSurvey.setThirdAnswer(thirdAnswer);
        }

        if (!fourthAnswer.equals("")){
            mSurvey.setTotAnswers(4);
            mSurvey.setFourthAnswer(fourthAnswer);
        }

        if (!fifthAnswer.equals("")){
            mSurvey.setTotAnswers(5);
            mSurvey.setFifthAnswer(fifthAnswer);
        }

        return mSurvey;
    }

    private boolean validateFields(){

        if (!mSurvey.getFirstAnswer().equals("") && !mSurvey.getSecondAnswer().equals("") ){

            return true;
        }
        else {
            Toast.makeText(view.mContext, "Por favor, informe, no mínimo, duas opções de resposta para criar uma enquete.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void onDestroy(){
        subscription.clear();
    }
}
