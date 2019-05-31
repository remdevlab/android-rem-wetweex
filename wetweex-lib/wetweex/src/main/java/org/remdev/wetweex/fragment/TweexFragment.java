package org.remdev.wetweex.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import org.remdev.wetweex.activity.TweexActivity;
import org.remdev.wetweex.component.Coordinator;
import org.remdev.wetweex.component.CoordinatorsContainer;
import org.remdev.wetweex.utils.Objects;
import org.remdev.wetweex.viewmodel.TweexViewModel;

import java.util.UUID;

public abstract class TweexFragment<T extends TweexViewModel, C extends Coordinator<T>> extends Fragment {

    private static final String KEY_FRAGMENT_ID = "key_fragment_id";

    private String fragmentId = UUID.randomUUID().toString();
    private boolean instanceSaved = false;
    private C fragmentCoordinator;

    @NonNull
    protected abstract T createViewModel();

    @NonNull
    protected abstract C createCoordinator(@NonNull T viewModel);

    @NonNull
    protected abstract CoordinatorsContainer getCoordinatorContainer();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            fragmentId = savedInstanceState.getString(KEY_FRAGMENT_ID);
        }
        Objects.requireNonNull(fragmentId);
        //noinspection unchecked
        fragmentCoordinator = (C) getCoordinatorContainer().restore(fragmentId);
        if (fragmentCoordinator == null) {
            fragmentCoordinator = buildCoordinator();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        instanceSaved = false;
    }

    public boolean onBackPressed() {
        if (getCoordinator().isTopTaskCrucial() || !getCoordinator().hasTasks()) {
            return true;
        }
        if (getCoordinator().isTopTaskCancellable()) {
            getCoordinator().cancelTopTask();
            return false;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FRAGMENT_ID, fragmentId);
        getCoordinatorContainer().save(fragmentId, getCoordinator());
        instanceSaved = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!instanceSaved) {
            getCoordinator().tryCancelAll();
            getCoordinatorContainer().remove(fragmentId);
        }
    }

    @NonNull
    protected C getCoordinator() {
        return fragmentCoordinator;
    }

    @NonNull
    protected T getViewModel() {
        return fragmentCoordinator.getViewModel();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    protected <VM extends TweexViewModel> VM getActivityTweexViewModel() {
        FragmentActivity activity = getActivity();
        if (activity instanceof TweexActivity) {
            return (VM) ((TweexActivity) activity).getViewModel();
        }
        return null;
    }

    protected void clearState() {
        getCoordinatorContainer().remove(fragmentId);
        C oldCoordinator = fragmentCoordinator;
        fragmentCoordinator = buildCoordinator();
        oldCoordinator.tryCancelAll();
    }

    private C buildCoordinator() {
        return createCoordinator(createViewModel());
    }

}
