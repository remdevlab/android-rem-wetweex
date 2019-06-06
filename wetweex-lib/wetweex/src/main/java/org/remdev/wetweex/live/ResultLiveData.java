package org.remdev.wetweex.live;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple LiveData extension, which allows to react only after
 * {@link androidx.lifecycle.LiveData#setValue(Object)} or
 * {@link androidx.lifecycle.LiveData#postValue(Object)} value was invoked
 * and will not invoked after observer is connected again after on start
 */
public class ResultLiveData<T> extends AnyThreadMutableLiveData<T> {

    private static final ValuePacket noValue = new ValuePacket<>(null);
    private final AtomicLong dataVersion = new AtomicLong(0);

    private Lock observersLock = new ReentrantLock();
    private Set<Observer<? super T>> observersSet = new HashSet<>();
    private DefaultValueLiveData<ValuePacket<T>> source = new DefaultValueLiveData<ValuePacket<T>>(noValue) {
        @Override
        public void removeObserver(@NonNull Observer<? super ValuePacket<T>> observer) {
            super.removeObserver(observer);
            observersSet.clear();
        }
    };

    private WrappingObserver innerObserver = new WrappingObserver();

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        withLock(()-> {
            observersSet.add(observer);
            if (observersSet.size() == 1) {
                source.observe(owner, innerObserver);
            } else if (owner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                innerObserver.onChanged(source.getValue());
            }
        });
    }

    @Override
    public void observeForever(@NonNull Observer<? super T> observer) {
        withLock(() -> {
            observersSet.add(observer);
            if (observersSet.size() == 1) {
                source.observeForever(innerObserver);
            } else {
                innerObserver.onChanged(source.getValue());
            }
        });
    }

    @Override
    public void removeObserver(@NonNull Observer<? super T> observer) {
        withLock(() -> {
            observersSet.remove(observer);
            if (observersSet.size() == 0) {
                source.removeObserver(innerObserver);
            }
        });
    }

    @Override
    final public void postValue(T value) {
        source.postValue(new ValuePacket<>(value));
    }

    @Override
    final public void setValue(T value) {
        withLock(() -> {
            dataVersion.incrementAndGet();
            source.setValue(new ValuePacket<>(value));
        });
    }

    public void reset() {
        source.setOrPost(noValue);
        dataVersion.incrementAndGet();
    }

    public void removeListeners() {
        withLock(() -> {
            observersSet.clear();
            source.removeObserver(innerObserver);
        });
    }

    private void withLock(Runnable action) {
        try {
            observersLock.lock();
            action.run();
        } finally {
            observersLock.unlock();
        }
    }

    private static class ValuePacket<T> {
        private final T val;

        private ValuePacket(T val) {
            this.val = val;
        }
    }

    private final class WrappingObserver implements Observer<ValuePacket<T>>  {

        private long lastObservedVersion = 0;

        @Override
        public void onChanged(ValuePacket<T> valuePacket) {
            if (valuePacket == noValue || lastObservedVersion == dataVersion.get()) {
                return;
            }
            try {
                observersLock.lock();
                for (Observer<? super T> observer : observersSet) {
                    observer.onChanged(valuePacket.val);
                }
                lastObservedVersion = dataVersion.get();
            } finally {
                observersLock.unlock();
            }
        }
    }
}
