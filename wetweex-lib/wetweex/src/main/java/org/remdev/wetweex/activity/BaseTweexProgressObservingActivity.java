package org.remdev.wetweex.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.remdev.wetweex.ProgressView;
import org.remdev.wetweex.R;
import org.remdev.wetweex.component.Coordinator;
import org.remdev.wetweex.viewmodel.TweexProgressViewModel;

import java.util.List;

public abstract class BaseTweexProgressObservingActivity<T extends TweexProgressViewModel, C extends Coordinator<T>>
                extends TweexProgressObservingActivity<T, C> {

    private ProgressView progressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_progress_observing);
        progressView = findViewById(R.id.tweex_progress);
    }

    @Override
    public void setContentView(int layoutResID) {
        ViewStub stubContainer = findViewById(R.id.content_container);
        stubContainer.setLayoutResource(layoutResID);
        stubContainer.inflate();
    }

    @Override
    public void setContentView(View view) {
        FrameLayout stubContainer = findViewById(R.id.content_view_container);
        stubContainer.removeAllViews();
        stubContainer.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        FrameLayout stubContainer = findViewById(R.id.content_view_container);
        stubContainer.removeAllViews();
        stubContainer.addView(view, params);
    }

    @NonNull
    protected ProgressView getProgressView() {
        return progressView;
    }

    protected void showProgress(boolean indeterminate, List<String> operationsTags) {
        getProgressView().show(indeterminate, operationsTags);
    }

    protected void hideProgress() {
        getProgressView().hide();
    }
}
