package com.coco_sh.ctao.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coco_sh.cocolib.JBaseNoToolbarFragment;
import com.coco_sh.cocolib.utils.SharedPreferenceHelper;
import com.coco_sh.ctao.R;
import com.coco_sh.ctao.utils.Constants;

import butterknife.ButterKnife;

/**
 * Desc:
 * Author:zhjm
 * Date:2015/12/1.
 */
public abstract class BaseNoToolbarFragment extends JBaseNoToolbarFragment {

    protected SharedPreferenceHelper mPreferenceHelper;

    protected abstract void init();

    protected abstract void onClickView(View v);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        init();
        return rootView;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPreferenceHelper = new SharedPreferenceHelper(activity, Constants.PREFERENCE_FILE_NAME);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    protected void showProgress() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(mLoadingLayout != null) {
                TextView loadText = (TextView) mLoadingLayout.findViewById(com.coco_sh.cocolib.R.id.txt_loading);
                loadText.setTextColor(mResources.getColor(R.color.colorAccent));
            }

        }
        showView(mLoadingLayout);
    }

    protected void hideProgress() {

        hideView(mLoadingLayout);

    }



}
