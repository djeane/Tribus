package apptribus.com.tribus.activities.main_activity.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_talks.TalksFragment;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.TimeLineFragment;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.TribusFragment;

/**
 * Created by User on 5/25/2017.
 */

public class TabsAdapter extends FragmentPagerAdapter{


    private Context mContext;
    private String[] tabsTitle = {"PESQUISAR", "TRIBUS", "AMIGOS"};
    private int[] icons = {R.drawable.ic_tab_timeline, R.drawable.ic_tab_tribus_add, R.drawable.ic_tab_amigos};
    private int mHeigthIcon;

    public TabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        double scale = context.getResources().getDisplayMetrics().density;
        mHeigthIcon = (int)(24 * scale + 0.5f);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        if(position == 0){
            Log.d("Valor: ", "TimeLineFragment.getInstance - Adapter");
            frag = TimeLineFragment.getInstance(position);
        }
        else if(position == 1) {
            Log.d("Valor: ", "TribusFragment.getInstance - Adapter");
            frag = TribusFragment.getInstance(position);
        }
        else if(position == 2) {
            Log.d("Valor: ", "TalksFragment.getInstance - Adapter");
            frag = TalksFragment.getInstance(position);
        }

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        frag.setArguments(bundle);

        return frag;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        Drawable drawable = mContext.getResources().getDrawable(icons[position]);
        drawable.setBounds(0, 0, mHeigthIcon, mHeigthIcon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(mContext.getResources().getColor(R.color.colorPrimaryDark));
        }

        ImageSpan imageSpan = new ImageSpan(drawable);

        SpannableString spannableString = new SpannableString(" ");
        spannableString.setSpan(imageSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }


    @Override
    public int getCount() {
        return tabsTitle.length;
    }
}
