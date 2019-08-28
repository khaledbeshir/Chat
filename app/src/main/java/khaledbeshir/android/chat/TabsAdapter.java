package khaledbeshir.android.chat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Mohamed Amr on 6/12/2019.
 */

public class TabsAdapter extends FragmentPagerAdapter {
    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 : ChatFragment chatFragment =
                    new ChatFragment();
            return chatFragment;

            case 1:
                return new ContactFragment();

            case 2:
                return new GroupFragment();

            case 3:
                return new RequestsFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Chats";
            case 1:
                return "Contacts";
            case 2:
                return "Groups";
            case 3:
                return "Requests";

            default: return null;
        }
    }
}
