package org.remdev.wetweex.live;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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

    private Lock observersLock = new ReentrantLock();
    private Set<Observer<? super T>> observersSet = new HashSet<>();
    private DefaultValueLiveData<ValuePacket<T>> source = new DefaultValueLiveData<ValuePacket<T>>(noValue) {
        @Override
        public void removeObserver(@NonNull Observer<? super ValuePacket<T>> observer) {
            super.removeObserver(observer);
            observersSet.clear();
        }
    };

    private AtomicBoolean dataPosted = new AtomicBoolean(false);

    private WrappingObserver innerObserver = new WrappingObserver();

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        withLock(()-> {
            observersSet.add(observer);
            if (observersSet.size() == 1) {
                source.observe(owner, innerObserver);
            }
        });
    }

    @Override
    public void observeForever(@NonNull Observer<? super T> observer) {
        withLock(() -> {
            observersSet.add(observer);
            if (observersSet.size() == 1) {
                source.observeForever(innerObserver);
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
        if (dataPosted.compareAndSet(false, true)) {
            source.postValue(new ValuePacket<>(value));
        } else {
            throw new IllegalStateException("This live data can not consume any new events as it's not reset");
        }
    }

    @Override
    final public void setValue(T value) {
        if (dataPosted.compareAndSet(false, true)) {
            source.setValue(new ValuePacket<>(value));
        } else {
            throw new IllegalStateException("This live data can not consume any new events as it's not reset");
        }
    }

    public void reset() {
        source.setOrPost(noValue);
        dataPosted.set(false);
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

        @Override
        public void onChanged(ValuePacket<T> valuePacket) {
            if (valuePacket == noValue) {
                return;
            }
            try {
                observersLock.lock();
                Iterator<Observer<? super T>> iterator = observersSet.iterator();
                while (iterator.hasNext()) {
                    iterator.next().onChanged(valuePacket.val);
                    iterator.remove();
                }
                source.setOrPost(noValue);
            } finally {
                observersLock.unlock();
            }
        }
    }
}
