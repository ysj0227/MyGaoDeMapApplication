package com.coco_sh.ctao.base;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.coco_sh.cocolib.JBaseActivity;
import com.coco_sh.cocolib.utils.SharedPreferenceHelper;
import com.coco_sh.ctao.R;
import com.coco_sh.ctao.utils.Constants;

import butterknife.ButterKnife;

/**
 * Desc:
 * Author:zhjm
 * Date:2015/12/1.
 */
public abstract class BaseActivity extends JBaseActivity {

    protected SharedPreferenceHelper mPreferenceHelper;

    protected abstract void init();

    protected abstract void onClickView(View v);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mPreferenceHelper = new SharedPreferenceHelper(mContext, Constants.PREFERENCE_FILE_NAME);

        init();

    }

    protected void showProgress() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mLoadingLayout != null) {
                TextView loadText = (TextView) mLoadingLayout.findViewById(com.coco_sh.cocolib.R.id.txt_loading);
                loadText.setTextColor(mResources.getColor(R.color.colorAccent));
            }

        }
        showView(mLoadingLayout);

//        if(!mLoadingFragment.isVisible()) {
//            mLoadingFragment.show(getSupportFragmentManager(), "loading");
//        }

    }

    protected void hideProgress() {
        hideView(mLoadingLayout);
    }



}
