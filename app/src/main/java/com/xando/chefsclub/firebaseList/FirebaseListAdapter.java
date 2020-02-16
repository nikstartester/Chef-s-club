package com.xando.chefsclub.firebaseList;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.List;

public abstract class FirebaseListAdapter<T, I extends IItem> extends FastItemAdapter<I>
        implements FirebaseAdapter<T> {

    private static final String TAG = "FirebaseListAdapter";
    private final ObservableSnapshotArray<T> mSnapshots;

    private final SparseArray<String> mIds;

    private final SparseBooleanArray mSyncIndexes = new SparseBooleanArray();


    protected FirebaseListAdapter(@NonNull FirebaseRecyclerOptions<T> options) {
        this(options, null);
    }

    protected FirebaseListAdapter(@NonNull FirebaseRecyclerOptions<T> options, List<T> dataList) {
        mSnapshots = options.getSnapshots();
        mIds = new SparseArray<>();

        if (dataList != null) {
            addSavedData(dataList);
        }

        if (options.getOwner() != null) {
            options.getOwner().getLifecycle().addObserver(this);
        }
    }

    private void addSavedData(List<T> dataList) {
        for (int i = 0; i < dataList.size(); i++)
            addWithId(i, dataList.get(i));
    }

    private void addWithId(int pos, @NonNull T data) {
        String id = getUniqueId(data);

        if (indexOfValueByValue(id) == -1) {
            mIds.put(pos, id);
            add(getNewItemInstance(data, pos));
        }

    }

    //You can get actual data
    //!!!!!!Settings in item can be not actual (see onChildChanged method)!!!!!
    @NonNull
    @Override
    public ObservableSnapshotArray<T> getSnapshots() {
        return mSnapshots;
    }

    @Override
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void startListening() {
        if (!mSnapshots.isListening(this)) {
            mSnapshots.addChangeEventListener(this);
        }
    }

    @Override
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stopListening() {
        mSnapshots.removeChangeEventListener(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void cleanup(LifecycleOwner source) {
        source.getLifecycle().removeObserver(this);
    }

    @Override
    public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
        switch (type) {
            case ADDED:
                onItemAdded(newIndex);
                break;
            case CHANGED:
                onItemChanged(newIndex);
                break;
            case REMOVED:
                onItemRemoved(newIndex);
                break;
            case MOVED:
                onItemMoved(newIndex, oldIndex);
                break;
            default:
                throw new IllegalStateException("Incomplete case statement");
        }
    }

    private void onItemAdded(int pos) {
        onItemAdded(getData(pos), pos);
    }

    private void onItemAdded(T data, int pos) {
        String id = getUniqueId(data);
        int index = indexOfValueByValue(id);

        if (index == -1) {
            add(pos, getNewItemInstance(getData(pos), pos));
            mIds.put(pos, id);
        } else if (index != pos) {
            onItemMoved(pos, index);
        } else {
            onItemChanged(pos);
        }
    }

    private T getData(int pos) {
        return mSnapshots.get(pos);
    }

    public abstract String getUniqueId(@NonNull T data);

    private int indexOfValueByValue(String value) {
        for (int i = 0; i < mIds.size(); i++) {
            if (value == null) {
                if (mIds.valueAt(i) == null) {
                    return i;
                }
            } else {
                if (value.equals(mIds.valueAt(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void removeById(@NonNull String id) {
        int index = indexOfValueByValue(id);

        if (index != -1) {
            onItemRemoved(index);
        }
    }

    @NonNull
    public abstract I getNewItemInstance(@NonNull T data, int pos);

    private void onItemChanged(int pos) {
        mIds.put(pos, getUniqueId(getData(pos)));
        if (onItemChanged(getItem(pos), getData(pos), pos))
            notifyAdapterItemChanged(pos);
    }

    /*
    Return true if need use notifyItemChanged else return false
     */
    public abstract boolean onItemChanged(I item, T data, int pos);

    private void onItemRemoved(int pos) {
        remove(pos);
        mIds.removeAt(pos);
    }

    private void onItemMoved(int newPos, int oldPos) {
        move(oldPos, newPos);

        String idOnOldPos = mIds.get(oldPos);

        //mIds.put(newPos, idOnOldPos);
    }

    @Override
    public void onDataChanged() {

    }

    @Override
    public void onError(@NonNull DatabaseError databaseError) {

    }
}
