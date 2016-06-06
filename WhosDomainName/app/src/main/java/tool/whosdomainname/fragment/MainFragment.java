package tool.whosdomainname.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.sdk.services.gists.PublicGistsClient;
import com.alorma.github.sdk.services.repos.GithubReposClient;
import com.alorma.github.sdk.services.repos.UserReposClient;
import com.alorma.gitskarios.core.client.LogProvider;
import com.alorma.gitskarios.core.client.LogProviderInterface;
import com.alorma.gitskarios.core.client.TokenProvider;
import com.alorma.gitskarios.core.client.TokenProviderInterface;
import com.alorma.gitskarios.core.client.UrlProvider;
import com.alorma.gitskarios.core.client.UrlProviderInterface;
import com.alorma.gitskarios.core.client.UsernameProvider;
import com.alorma.gitskarios.core.client.UsernameProviderInterface;

import rx.schedulers.Schedulers;
import tool.whosdomainname.activity.CustomWaitDialog;
import tool.whosdomainname.android.R;

/**
 * @author liu_chonghui
 */
public class MainFragment extends BaseFragment {

    protected int getPageLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTransaction();

        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void startTransaction() {
        getActivity().overridePendingTransition(R.anim.push_left_in,
                R.anim.push_still);
    }

    protected void initData() {
        if (getActivity().getIntent() != null) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (!isViewNull()) {
            return mView;
        }
        mView = inflater.inflate(getPageLayout(), container, false);
        intView(mView);
        return mView;
    }

    CustomWaitDialog mUpdateDialog;

    @SuppressLint("InflateParams")
    protected void intView(View view) {
        mUpdateDialog = new CustomWaitDialog(getActivity());
        mUpdateDialog.setCanceledOnTouchOutside(false);

        TokenProvider.setTokenProviderInstance(new TokenProviderInterface() {
            @Override
            public String getToken() {
                return null;
            }
        });

        UrlProvider.setUrlProviderInstance(new UrlProviderInterface() {
            @Override
            public String getUrl() {
                return null;
            }
        });

        UsernameProvider.setUsernameProviderInterface(new UsernameProviderInterface() {
            @Override
            public String getUsername() {
                return null;
            }
        });

        LogProvider.setTokenProviderInstance(new LogProviderInterface() {
            @Override
            public void log(String message) {
            }
        });

        PublicGistsClient client = new PublicGistsClient();

//        GithubReposClient client = new UserReposClient(getActivity(), username);
//        client.observable()
//                .observeOn(AndroidSchedulers.mainThread()
//                        .subscribeOn(Schedulers.io())
//                        .subscribe(null);
    }

    protected void flushPage() {
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    boolean firstResume = true;

    @Override
    public void onResume() {
        super.onResume();
        if (firstResume) {
            firstResume = false;
        }
        if (!firstResume) {
        }
    }

    public boolean holdGoBack() {
        // if (myOneKeyShare != null && myOneKeyShare.isShow()) {
        // return true;
        // }
        // if (popupAttacher != null && popupAttacher.isShowing()) {
        // return true;
        // }
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean flag = false;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (holdGoBack()) {
                // if (myOneKeyShare != null && myOneKeyShare.isShow()) {
                // myOneKeyShare.close();
                // }
                // if (popupAttacher != null && popupAttacher.isShowing()) {
                // popupAttacher.closePop();
                // }
                flag = true;
            }
        }
        return flag;
    }

    public void leaveCurrentPage() {
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.push_still,
                R.anim.push_right_out);
    }

}
