package apptribus.com.tribus.activities.show_survey.mvp;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_survey.view_holder.ShowSurveyViewHolder;
import apptribus.com.tribus.pojo.Survey;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by User on 1/19/2018.
 */

public class ShowSurveyPresenter {

    private final ShowSurveyView view;
    private final ShowSurveyModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private FirebaseRecyclerAdapter<Survey, ShowSurveyViewHolder> mAdapter;

    public static boolean isOpen;
    private Tribu mTribu;



    public ShowSurveyPresenter(ShowSurveyView view, ShowSurveyModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        PresenceSystemAndLastSeen.presenceSystem();

        //subscription.add(observeUser());

        subscription.add(observeTribu());
        subscription.add(observeBtnArrowBack());
        //subscription.add(hasChildren());

        if (view.mRvShowSurvey != null && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

        isOpen = true;

    }

    public void onResume() {

        PresenceSystemAndLastSeen.presenceSystem();

        if (view.mRvShowSurvey != null && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

    }

    public void onPause() {

        PresenceSystemAndLastSeen.lastSeen();

    }


    public void onStop() {

        //model.removeListeners();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        isOpen = false;
    }

    private Subscription observeTribu() {
        return model.getTribu(view.mTribuKey)
                .subscribe(tribu -> {
                            mTribu = tribu;
                            setToolbarTitleAndSubtitle(tribu);
                            setAdapter(tribu);
                        },
                        Throwable::printStackTrace
                );
    }

    private void setAdapter(Tribu tribu) {
        mAdapter = model.setRecyclerViewSurveys(tribu);

        if (view.mRvShowSurvey != null && mAdapter != null) {
            view.mRvShowSurvey.setAdapter(model.setRecyclerViewSurveys(tribu));
        }
    }


    private void setToolbarTitleAndSubtitle(Tribu tribu) {

        String appendToolbarTitle = "Enquetes de " + tribu.getProfile().getNameTribu();
        view.mTvNameTribu.setText(appendToolbarTitle);

    }


    private Subscription observeBtnArrowBack() {
        return view.observableBtnArrowBack()
                .subscribe(
                        __ -> model.backToMainActivity(),
                        Throwable::printStackTrace
                );
    }

    public void onRestart() {
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.cleanup();
            subscription.clear();
            //model.removeListeners();
        }

    }
}
