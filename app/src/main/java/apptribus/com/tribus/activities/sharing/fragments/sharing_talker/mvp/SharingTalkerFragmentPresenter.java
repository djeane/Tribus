package apptribus.com.tribus.activities.sharing.fragments.sharing_talker.mvp;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.sharing.view_holder.SharingTalkersViewHolder;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static apptribus.com.tribus.util.Constantes.FOLLOWERS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;

/**
 * Created by User on 1/15/2018.
 */

public class SharingTalkerFragmentPresenter {

    private final SharingTalkerFragmentView view;
    private final SharingTalkerFragmentModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private FirebaseRecyclerAdapter<Talk, SharingTalkersViewHolder> mAdapterTalkers;

    //REFERENCES - FIREBASE (KEEP SYNCED)
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private DatabaseReference mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);
    private DatabaseReference mRefTalkers = mDatabase.getReference().child(USERS_TALKS);
    private DatabaseReference mRefFollowers = mDatabase.getReference().child(FOLLOWERS);

    private String mLink;

    public SharingTalkerFragmentPresenter(SharingTalkerFragmentView view, SharingTalkerFragmentModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart(String link){

        mLink = link;

        //KEEP SYNCED
        mReferenceUser.keepSynced(true);
        mReferenceTribus.keepSynced(true);
        mRefTalkers.keepSynced(true);
        mRefFollowers.keepSynced(true);

        subscription.add(observeUser());
        subscription.add(hasChildrenTalkers());

        if (view.mRvTalkers != null && mAdapterTalkers != null) {
            mAdapterTalkers.notifyDataSetChanged();
        }
    }


    public void onStop(){

        if (mAdapterTalkers != null) {
            mAdapterTalkers.notifyDataSetChanged();
        }

    }


    public void onPause(){

    }

    private Subscription observeUser() {
        return model.getUser()
                .subscribe(this::setAdapterTalkers,
                        Throwable::printStackTrace
                );
    }

    private void setAdapterTalkers(User user) {
        mAdapterTalkers = model.setRecyclerViewTalkers(mLink);

        if (view.mRvTalkers != null && mAdapterTalkers != null) {
            view.mRvTalkers.setAdapter(model.setRecyclerViewTalkers(mLink));
        }
    }

    private Subscription hasChildrenTalkers() {
        return model.hasChildrenTalkes()
                .subscribe(hasChildren -> {
                            if (!hasChildren) {
                                view.mRvTalkers.setVisibility(View.GONE);
                                TextView tv = new TextView(view.getContext());
                                tv.setText(R.string.hasChildrenTalkers);
                                tv.setId(R.id.text);
                                tv.setTextSize(16);
                                tv.setTextColor(view.getResources().getColor(R.color.colorPrimaryDark));

                                tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                        FrameLayout.LayoutParams.WRAP_CONTENT));
                                tv.setGravity(Gravity.CENTER);

                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                                        view.mRelativeTalkers.getLayoutParams();

                                setViewMargins(view.getContext(), params, 0, 0, 0, 0, tv);

                                view.mRelativeTalkers.addView(tv);
                            } else {
                                if (view.mRelativeTalkers.findViewById(R.id.text) != null) {
                                    view.mTvTalkers.setVisibility(View.VISIBLE);
                                    view.mRelativeTalkers.removeView(view.mRelativeTalkers.findViewById(R.id.text));
                                } else {
                                    view.mTvTalkers.setVisibility(View.VISIBLE);
                                }

                            }
                        },

                        Throwable::printStackTrace
                );
    }

    private void setViewMargins(Context con, ViewGroup.LayoutParams params,
                                int left, int top, int right, int bottom, View view) {

        final float scale = con.getResources().getDisplayMetrics().density;
        // convert the DP into pixel
        int pixel_left = (int) (left * scale + 0.5f);
        int pixel_top = (int) (top * scale + 0.5f);
        int pixel_right = (int) (right * scale + 0.5f);
        int pixel_bottom = (int) (bottom * scale + 0.5f);

        ViewGroup.MarginLayoutParams s = (ViewGroup.MarginLayoutParams) params;
        s.setMargins(pixel_left, pixel_top, pixel_right, pixel_bottom);

        view.setLayoutParams(params);
    }

    public void onDetach(){

    }


    public void onDestroyView(){
        if (mAdapterTalkers != null) {
            mAdapterTalkers.cleanup();
            subscription.clear();
        }
    }
}
