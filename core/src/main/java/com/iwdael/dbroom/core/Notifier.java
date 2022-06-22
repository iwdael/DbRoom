package com.iwdael.dbroom.core;

import androidx.databinding.Observable;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/DbRoom
 */
public interface Notifier extends Observable {
    @Override
    default void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
    }

    @Override
    default void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
    }

    default Notifier to() {
        return this;
    }
}
