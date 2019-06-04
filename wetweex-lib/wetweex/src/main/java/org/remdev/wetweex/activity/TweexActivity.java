package org.remdev.wetweex.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import org.remdev.wetweex.component.Coordinator;
import org.remdev.wetweex.component.CoordinatorsContainer;
import org.remdev.wetweex.fragment.TweexFragment;
import org.remdev.wetweex.viewmodel.TweexViewModel;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class TweexActivity<T extends TweexViewModel, C extends Coordinator<T>> extends AppCompatActivity {

    private static final String KEY_ACTIVITY_ID = "key_activity_id";

    private String activityId = UUID.randomUUID().toString();
    private boolean instanceSaved = false;
    private C activityCoordinator;

    @NonNull
    protected abstract T createViewModel();

    @NonNull
    protected abstract C createCoordinator(@NonNull T viewModel);

    @NonNull
    protected abstract CoordinatorsContainer getCoordinatorContainer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            activityId = savedInstanceState.getString(KEY_ACTIVITY_ID);
        }
        Objects.requireNonNull(activityId);
        //noinspection unchecked
        activityCoordinator = (C) getCoordinatorContainer().restore(activityId);
        if (activityCoordinator == null) {
            activityCoordinator = buildCoordinator();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        instanceSaved = false;
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.isEmpty()) {
            onBackPressedForActivity();
        } else {
            onBackPressedWithFragment(fragments);
        }
    }

    private void onBackPressedForActivity() {
        if (getCoordinator().isTopTaskCrucial() || !getCoordinator().hasTasks()) {
            super.onBackPressed();
            return;
        }
        if (getCoordinator().isTopTaskCancellable()) {
            getCoordinator().cancelTopTask();
        }
    }

    private void onBackPressedWithFragment(List<Fragment> fragments) {
        int backStackSize = getSupportFragmentManager().getBackStackEntryCount();
        Fragment fragment = fragments.get(fragments.size() - 1);
        if (fragment instanceof TweexFragment) {
            boolean canGoBack = ((TweexFragment) fragment).onBackPressed();
            if (canGoBack && backStackSize == 0) {
                onBackPressedForActivity();
            } else if (canGoBack) {
                super.onBackPressed();
            }
        } else {
            if (backStackSize > 0) {
                super.onBackPressed();
            } else {
                onBackPressedForActivity();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_ACTIVITY_ID, activityId);
        getCoordinatorContainer().save(activityId, getCoordinator());
        instanceSaved = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!instanceSaved) {
            getCoordinator().tryCancelAll();
            getCoordinatorContainer().remove(activityId);
        }
    }

    @NonNull
    protected C getCoordinator() {
        return activityCoordinator;
    }

    @NonNull
    public T getViewModel() {
        return activityCoordinator.getViewModel();
    }

    protected void clearState() {
        getCoordinatorContainer().remove(activityId);
        C oldCoordinator = activityCoordinator;
        activityCoordinator = buildCoordinator();
        oldCoordinator.tryCancelAll();
    }

    private C buildCoordinator() {
        return createCoordinator(createViewModel());
    }

}
