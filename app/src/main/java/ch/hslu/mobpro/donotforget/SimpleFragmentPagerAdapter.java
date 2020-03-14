package ch.hslu.mobpro.donotforget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {//NOPMD
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(final int position) {
        return position == 0 ? new NotesFragment() : new TodosFragment();
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(final int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return createTabLayoutTitle(R.drawable.ic_note_black_24dp, "Notizen");//NOPMD
            case 1:
                return createTabLayoutTitle(R.drawable.ic_format_list_bulleted_black_24dp, "To-Do's");//NOPMD
            default:
                return null;
        }
    }

    private SpannableString createTabLayoutTitle(final int drawableId, final String name){
        // Generate title based on item position
        final Drawable image = mContext.getDrawable(drawableId);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        // Replace blank spaces with image icon
        SpannableString sb = new SpannableString("   " + " "+name);//NOPMD
        final ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
