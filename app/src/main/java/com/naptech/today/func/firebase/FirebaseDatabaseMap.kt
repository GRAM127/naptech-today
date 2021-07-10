package com.naptech.today.func.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference


class FirebaseDatabaseMap(private val reference: DatabaseReference): MutableMap<String, Any?> {

    private val map = mutableMapOf<String, Any?>()
    private var onUpdateListener: OnUpdateListener? = null

    private val databaseListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val key = snapshot.key.toString()
            map[key] = snapshot.value
            onUpdateListener?.onAdded(key, snapshot.value)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val key = snapshot.key.toString()
            val oldData = map[key]
            map[key] = snapshot.value
            onUpdateListener?.onUpdated(key, oldData, snapshot.value)
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            onUpdateListener?.onRemoved(snapshot.key.toString(), snapshot.value)
            map.remove(snapshot.key)
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }

    fun setOnUpdateListener(listenerOn: OnUpdateListener) {
        onUpdateListener = listenerOn
    }

//    ------------

    fun start(): FirebaseDatabaseMap {
        reference.addChildEventListener(databaseListener)
        return this
    }
    fun stop(): FirebaseDatabaseMap {
        reference.removeEventListener(databaseListener)
        return this
    }

    interface OnUpdateListener {
        fun onAdded(key: String, newData: Any?) { }
        fun onUpdated(key: String, oldData: Any?, newData: Any?) { }
        fun onRemoved(key: String, removedData: Any?) { }
    }

//    ------------

    override val size: Int
        get() = map.size

    override fun containsKey(key: String): Boolean = map.containsKey(key)
    override fun containsValue(value: Any?): Boolean = map.containsValue(value)

    override fun get(key: String): Any? = map[key]

    override fun isEmpty(): Boolean = map.isEmpty()

    override val entries: MutableSet<MutableMap.MutableEntry<String, Any?>>
        get() = map.entries
    override val keys: MutableSet<String>
        get() = map.keys
    override val values: MutableCollection<Any?>
        get() = map.values

    override fun clear() {
        map.clear()
    }

    override fun put(key: String, value: Any?): Any? {
        reference.child(key).setValue(value)
        return value
    }

    override fun putAll(from: Map<out String, Any?>) {
        from.forEach {
            reference.child(it.key).setValue(it.value)
        }
    }

    override fun remove(key: String): Any? {
        val data = map[key]
        reference.child(key).removeValue()
        return data
    }

}