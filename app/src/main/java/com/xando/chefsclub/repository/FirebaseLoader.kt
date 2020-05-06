package com.xando.chefsclub.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.xando.chefsclub.dataworkers.BaseRepository
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Simple Rx wrapper
 */
class FirebaseLoader<T>(
    private val sourceReference: DatabaseReference,
    private val type: Class<T>
) {
    private val subject = PublishSubject.create<T>()

    fun loadOnce(): Observable<T> {
        sourceReference.addListenerForSingleValueEvent(EventListener())
        return subject
    }

    fun sync(): Observable<T> {
        sourceReference.addValueEventListener(EventListener())
        return subject
    }

    private inner class EventListener : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            subject.onError(p0.toException())
        }

        override fun onDataChange(p0: DataSnapshot) {
            p0.getValue(type)?.let { subject.onNext(it) }
                ?: subject.onError(BaseRepository.NothingFoundFromServerException())
        }
    }
}