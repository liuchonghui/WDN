package tool.whosdomainname.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import tool.whosdomainname.android.R;
import tool.whosdomainname.fragment.MainFragment;

public class MainActivity extends BaseFragmentActivity {
    protected MainFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_frame);
        initFragment();
    }

    public void initFragment() {
        if (mFragment == null) {
            mFragment = new MainFragment();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fm.beginTransaction();
        fragTransaction.add(R.id.content_frame, mFragment);
        fragTransaction.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mFragment != null) {
            mFragment.onNewIntent(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mFragment != null && mFragment.holdGoBack()) {
                return mFragment.onKeyDown(keyCode, event);
            } else {
                if (mFragment != null) {
                    mFragment.leaveCurrentPage();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}