package apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.mvp;

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
import apptribus.com.tribus.activities.sharing.view_holder.SharingTribusViewHolder;
import apptribus.com.tribus.pojo.Tribu;
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

public class SharingTribusFragmentPresenter {

    private final SharingTribusFragmentView view;
    private final SharingTribusFragmentModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private FirebaseRecyclerAdapter<Tribu, SharingTribusViewHolder> mAdapterTribus;

    //REFERENCES - FIREBASE (KEEP SYNCED)
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private DatabaseReference mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);
    private DatabaseReference mRefTalkers = mDatabase.getReference().child(USERS_TALKS);
    private DatabaseReference mRefFollowers = mDatabase.getReference().child(FOLLOWERS);

    private String mLink;

    public SharingTribusFragmentPresenter(SharingTribusFragmentView view, SharingTribusFragmentModel model) {
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
        subscription.add(hasChildrenTribus());
        if (view.mRvTribus != null && mAdapterTribus != null) {
            mAdapterTribus.notifyDataSetChanged();
        }

    }


    public void onStop(){

    }


    public void onPause(){
        if (mAdapterTribus != null) {
            mAdapterTribus.notifyDataSetChanged();
        }
    }


    public void onDetach(){

    }

    private Subscription observeUser() {
        return model.getUser()
                .subscribe(this::setAdapterTribus,
                        Throwable::printStackTrace
                );
    }

    private void setAdapterTribus(User user) {
        mAdapterTribus = model.setRecyclerViewTribus(mLink);

        if (view.mRvTribus != null && mAdapterTribus != null) {
            view.mRvTribus.setAdapter(model.setRecyclerViewTribus(mLink));
        }
    }


    private Subscription hasChildrenTribus() {
        return model.hasChildrenTribus()
                .subscribe(hasChildren -> {
                            if (!hasChildren) {
                                view.mRvTribus.setVisibility(View.GONE);
                                TextView tv = new TextView(view.getContext());
                                tv.setText(R.string.hasChildrenTribus);
                                tv.setId(R.id.text);
                                tv.setTextSize(16);
                                tv.setTextColor(view.getResources().getColor(R.color.colorPrimaryDark));

                                tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                        FrameLayout.LayoutParams.WRAP_CONTENT));
                                tv.setGravity(Gravity.CENTER);

                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                                        view.mRelativeTribus.getLayoutParams();

                                setViewMargins(view.getContext(), params, 0, 0, 0, 0, tv);

                                view.mRelativeTribus.addView(tv);
                            } else {
                                if (view.mRelativeTribus.findViewById(R.id.text) != null) {
                                    view.mRvTribus.setVisibility(View.VISIBLE);
                                    view.mRelativeTribus.removeView(view.mRelativeTribus.findViewById(R.id.text));
                                } else {
                                    view.mRvTribus.setVisibility(View.VISIBLE);
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

    public void onDestroyView(){

        if (mAdapterTribus != null) {
            mAdapterTribus.cleanup();
            subscription.clear();
        }
    }
}
