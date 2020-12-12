package apptribus.com.tribus.activities.blocked_talkers.mvp;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.blocked_talkers.view_holder.BlockedTalkersViewHolder;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static apptribus.com.tribus.util.Constantes.BLOCKED_TALKERS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;

public class BlockedTalkersPresenter {

    private final BlockedTalkersView view;
    private final BlockedTalkersModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private FirebaseRecyclerAdapter<Talk, BlockedTalkersViewHolder> mAdapter;
    public static boolean isOpen;

    //REFERENCES - FIREBASE
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private DatabaseReference mRefBlockedTalkers = mDatabase.getReference().child(BLOCKED_TALKERS);
    private DatabaseReference mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);


    public BlockedTalkersPresenter(BlockedTalkersView view, BlockedTalkersModel model) {
        this.view = view;
        this.model = model;
    }


    public void onStart() {
        //KEEP SYNCED
        mReferenceUser.keepSynced(true);
        mRefBlockedTalkers.keepSynced(true);
        mReferenceTribus.keepSynced(true);

        PresenceSystemAndLastSeen.presenceSystem();

        subscription.add(observeUser());
        subscription.add(observeBtnArrowBack());
        subscription.add(hasChildren());

        if (view.mRvBlockedTalkers != null && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

        isOpen = true;
    }

    public void onPause() {
        PresenceSystemAndLastSeen.lastSeen();
    }

    public void onRestart() {
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onResume() {
        PresenceSystemAndLastSeen.presenceSystem();
    }

    private Subscription hasChildren() {
        return model.hasChildren()
                .subscribe(hasChildren -> {
                            if (!hasChildren) {
                                view.mRvBlockedTalkers.setVisibility(View.GONE);
                                TextView tv = new TextView(view.getContext());
                                tv.setText("Não há contato contato bloqueado.");
                                tv.setId(R.id.text);
                                tv.setTextSize(16);
                                tv.setTextColor(view.getResources().getColor(R.color.colorPrimaryDark));

                                tv.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT,
                                        CoordinatorLayout.LayoutParams.WRAP_CONTENT));
                                tv.setGravity(Gravity.CENTER);

                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.mRelativeBlockedUser.getLayoutParams();

                                setViewMargins(view.getContext(), params, 0, 0, 0, 0, tv);

                                view.mCoordinatorRecycler.addView(tv);
                                Log.d("Valor: ", "passou por hasChildren" + hasChildren);
                            } else {
                                if (view.mCoordinatorRecycler.findViewById(R.id.text) != null) {
                                    view.mRvBlockedTalkers.setVisibility(View.VISIBLE);
                                    view.mCoordinatorRecycler.removeView(view.mCoordinatorRecycler.findViewById(R.id.text));
                                } else {
                                    view.mRvBlockedTalkers.setVisibility(View.VISIBLE);
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

    public void onStop() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

        isOpen = false;
    }

    private Subscription observeUser() {
        return model.getUser()
                //.debounce(5000, TimeUnit.MILLISECONDS) // mudei isso para rodar no tablet
                //.observeOn(AndroidSchedulers.mainThread()) // mudei isso para rodar no tablet
                .subscribe(
                        this::setAdapter,
                        Throwable::printStackTrace
                );
    }

    private void setAdapter(User user) {
        mAdapter = model.setRecyclerViewBlockedTalkers();
        if (view.mRvBlockedTalkers != null && mAdapter != null) {
            view.mRvBlockedTalkers.setAdapter(model.setRecyclerViewBlockedTalkers());
        }
    }


    private Subscription observeBtnArrowBack() {
        return view.observableBtnArrowBack()
                .subscribe(
                        __ -> model.backToMainActivity(),
                        Throwable::printStackTrace
                );
    }

    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.cleanup();
            subscription.clear();
        }
    }
}
