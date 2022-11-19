package com.iwdael.dbroom.example

import androidx.room.TypeConverter
import com.iwdael.dbroom.example.entity.Music


@TypeConverter
fun musicConvertString(user: Music): String {
    return ""
}

@TypeConverter
fun stringConvertMusic(user: String): Music? {
    return null
}
/**
 *
 *     private void notifyNameChanged() {
synchronized (nameLock) {
if (callbacks != null) {
callbacks.notifyCallbacks(this, BR.name, null);
}
if (this.getId() == null) {
nameEntityVersion = 0;
return;
}
if (nameEntityVersion == -1) {
nameEntityVersion = 0;
return;
}
if (nameEntityVersion == 0) {
int maxVersion = 1;
List<WeakReference<MusicNotifier>> musicOfInit = musicOfAll();
for (WeakReference<MusicNotifier> reference : musicOfInit) {
MusicNotifier entity = reference.get();
if (entity == null || getId() != entity.getId()) continue;
maxVersion = Math.max(entity.nameEntityVersion, maxVersion);
}
nameEntityVersion = maxVersion;
}
nameEntityVersion++;
List<WeakReference<MusicNotifier>> musicOfAll = musicOfAll();
for (WeakReference<MusicNotifier> reference : musicOfAll) {
MusicNotifier entity = reference.get();
if (entity == null || getId() != entity.getId()) continue;
if (nameEntityVersion > entity.nameEntityVersion) {
entity.nameEntityVersion = nameEntityVersion - 1;
entity.nameRoomVersion = nameEntityVersion;
entity.setName(this.getName());
}
}
if (nameRoomVersion >= nameEntityVersion) return;
nameRoomVersion = nameEntityVersion - 1;
RoomNotifier.notifyRoom(new Notifier() {
@Override
public void notifier() {
if (nameEntityVersion - nameRoomVersion == 1) {
nameRoomVersion = nameEntityVersion;
DbRoom.music().updateName(MusicNotifier.this.getId(), MusicNotifier.this.getName());
List<WeakReference<MusicNotifier>> musicOfRoom = musicOfAll();
for (WeakReference<MusicNotifier> reference : musicOfRoom) {
MusicNotifier entity = reference.get();
if (entity == null || MusicNotifier.this.getId() != entity.getId()) continue;
if (nameEntityVersion > entity.nameEntityVersion) {
entity.nameEntityVersion = nameEntityVersion - 1;
entity.nameRoomVersion = nameEntityVersion;
entity.setName(MusicNotifier.this.getName());
}
}
}
}
});
}
}
 *
 */