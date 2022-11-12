package com.iwdael.dbroom.core;

import androidx.room.Ignore;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public abstract class RoomObservable extends BaseObservable {
  @Ignore
  private final BaseObservable dbObservable = Utils.createObserver(this);

  @Override
  public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
    dbObservable.addOnPropertyChangedCallback(callback);
  }

  @Override
  public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
    dbObservable.removeOnPropertyChangedCallback(callback);
  }

  @Override
  public void notifyPropertyChanged(int fieldId) {
    dbObservable.notifyPropertyChanged(fieldId);
  }

  public BaseObservable getDbObservable() {
    return dbObservable;
  }
}
