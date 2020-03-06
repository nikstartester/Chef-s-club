package com.xando.chefsclub.firebaseList

import android.util.SparseArray
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.mikepenz.fastadapter.IItem
import com.xando.chefsclub.list.GroupAdapter

abstract class FirebaseGroupListAdapter<T, ITEM : IItem<out Any, out RecyclerView.ViewHolder>>
(options: FirebaseRecyclerOptions<T>,
 dataList: List<T>?,
 private val groupAdapter: GroupAdapter<ITEM>) : FirebaseAdapter<T> {

    private val snapshots = options.snapshots
    private val ids = SparseArray<String>()

    init {
        if (dataList != null) addSavedData(dataList)

        if (options.owner != null) options.owner!!.lifecycle.addObserver(this)
    }

    //You can get actual data
    //!!!!!!Settings in item can be not actual (see onChildChanged method)!!!!!
    override fun getSnapshots() = snapshots

    private fun addSavedData(dataList: List<T>) {
        for (i in dataList.indices)
            addWithId(i, dataList[i])
    }

    private fun addWithId(pos: Int, data: T) {
        val id = getUniqueId(data)

        if (indexOfValueByValue(id) == -1) {
            ids.put(pos, id)
            groupAdapter.addItem(getNewItemInstance(data, pos))
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun startListening() {
        if (!snapshots.isListening(this)) snapshots.addChangeEventListener(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun stopListening() {
        snapshots.removeChangeEventListener(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun cleanup(source: LifecycleOwner) {
        source.lifecycle.removeObserver(this)
    }

    override fun onChildChanged(type: ChangeEventType, snapshot: DataSnapshot, newIndex: Int, oldIndex: Int) {
        when (type) {
            ChangeEventType.ADDED -> onItemAdded(newIndex)
            ChangeEventType.CHANGED -> onItemChanged(newIndex)
            ChangeEventType.REMOVED -> onItemRemoved(newIndex)
            ChangeEventType.MOVED -> onItemMoved(newIndex, oldIndex)
        }
    }

    private fun onItemAdded(pos: Int) {
        onItemAdded(getData(pos), pos)
    }

    protected fun onItemAdded(data: T, pos: Int) {
        val id = getUniqueId(data)
        val index = indexOfValueByValue(id)

        when {
            index == -1 -> {
                groupAdapter.addItem(pos, getNewItemInstance(getData(pos), pos))
                ids.put(pos, id)
            }
            index != pos -> onItemMoved(pos, index)
            else -> onItemChanged(pos)
        }
    }

    private fun getData(pos: Int): T {
        return snapshots.get(pos)
    }

    abstract fun getUniqueId(data: T): String

    protected fun indexOfValueByValue(value: String?): Int {
        for (i in 0 until ids.size()) {
            if (value == null) {
                if (ids.valueAt(i) == null) return i
            } else if (value == ids.valueAt(i)) return i
        }
        return -1
    }

    fun removeById(id: String) {
        val index = indexOfValueByValue(id)

        if (index != -1) onItemRemoved(index)
    }

    abstract fun getNewItemInstance(data: T, pos: Int): ITEM

    protected fun onItemChanged(pos: Int) {
        ids.put(pos, getUniqueId(getData(pos)))
        if (onItemChanged(groupAdapter.getItem(pos), getData(pos), pos))
            groupAdapter.notifyItemChanged(pos)
    }

    /**
     * @return true if need use notifyItemChanged else return false
     **/
    abstract fun onItemChanged(item: ITEM, data: T, pos: Int): Boolean

    protected fun onItemRemoved(pos: Int) {
        groupAdapter.remove(pos)
        ids.removeAt(pos)
    }

    protected fun onItemMoved(newPos: Int, oldPos: Int) {
        groupAdapter.move(oldPos, newPos)

        val idOnOldPos = ids.get(oldPos)

        //mIds.put(newPos, idOnOldPos);
    }

    override fun onDataChanged() {
        //do nothing
    }

    override fun onError(databaseError: DatabaseError) {
        //do nothing
    }
}