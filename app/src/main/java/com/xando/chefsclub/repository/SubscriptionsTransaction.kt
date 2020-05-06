package com.xando.chefsclub.repository

import com.xando.chefsclub.FirebaseReferences
import com.xando.chefsclub.helper.FirebaseHelper
import com.xando.chefsclub.profiles.data.ProfileData
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

// TODO: replace to ProfileRepository
object SubscriptionsTransaction {

    /**
     * Switch the subscription for [FirebaseHelper.getUid] to the user [toSubscribeUserId]
     */
    fun switchSubscription(toSubscribeUserId: String): Disposable {
        val currUserId = FirebaseHelper.getUid() ?: throw IllegalArgumentException()
        val databaseReference = FirebaseReferences.getDataBaseReference()

        val currUserRef = databaseReference
            .child("users")
            .child(currUserId)
        val toSubscribeUserRef = databaseReference
            .child("users")
            .child(toSubscribeUserId)

        return Observable.zip<ProfileData, ProfileData, Map<String, Any>>(
            FirebaseLoader(currUserRef, ProfileData::class.java)
                .loadOnce(),
            FirebaseLoader(
                toSubscribeUserRef,
                ProfileData::class.java
            )
                .loadOnce(),
            BiFunction { current, toSubscribe ->
                val currUserResult =
                    current.switchSubscription(toSubscribeUserId).getSubscriptionChildren("users/$currUserId/")
                val toSubscribeUserResult =
                    toSubscribe.switchSubscribe(currUserId).getSubscribeChildren("users/$toSubscribeUserId/")

                return@BiFunction hashMapOf<String, Any>()
                    .apply {
                        putAll(currUserResult)
                        putAll(toSubscribeUserResult)
                    }
            }
        )
            .subscribeOn(Schedulers.io())
            .subscribe { databaseReference.updateChildren(it) }
    }

    //region Actions
    private fun ProfileData.switchSubscription(userId: String) = apply {
        if (subscriptions.containsKey(userId)) {
            subscriptionsCount--
            subscriptions.remove(userId)
        } else {
            subscriptionsCount++
            subscriptions[userId] = true
        }
    }

    private fun ProfileData.switchSubscribe(userId: String) = apply {
        if (subscribers.containsKey(userId)) {
            subscribersCount--
            subscribers.remove(userId)
        } else {
            subscribersCount++
            subscribers[userId] = true
        }
    }
    //endregion

    /**
     * @param childPath - prefix for keys
     * @return map of subscriptions data to update child
     */
    private fun ProfileData.getSubscriptionChildren(childPath: String) =
        mapOf<String, Any>(
            "${childPath}subscriptions" to subscriptions,
            "${childPath}subscriptionsCount" to subscriptionsCount
        )

    /**
     * @param childPath - prefix for keys
     * @return map of subscribers data to update child
     */
    private fun ProfileData.getSubscribeChildren(childPath: String) =
        mapOf<String, Any>(
            "${childPath}subscribers" to subscribers,
            "${childPath}subscribersCount" to subscribersCount
        )
}