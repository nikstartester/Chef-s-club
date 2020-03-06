package com.xando.chefsclub.search.core

import androidx.appcompat.widget.SearchView
import com.algolia.instantsearch.ui.helpers.InstantSearch
import com.algolia.instantsearch.ui.utils.SearchViewFacade
import com.algolia.instantsearch.ui.viewmodels.SearchBoxViewModel
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class DelayedSearchBoxViewModel(searchView: SearchView) : SearchBoxViewModel(searchView) {

    companion object {
        private const val DEBOUNCE_TIMEOUT = 350L
        private const val MIN_SYMBOL_TO_SEARCH = 3
    }

    private val searchViewFacade: SearchViewFacade = SearchViewFacade(searchView)
    private val listeners = mutableListOf<InstantSearch>()

    private val disposer = CompositeDisposable()

    private val subject: Subject<String> = BehaviorSubject.create()

    override fun getSearchView() = searchViewFacade

    override fun addListener(instantSearch: InstantSearch) {
        listeners.add(instantSearch)

        disposer.add(Observable.create(ObservableOnSubscribe<String> { subscriber ->
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                        override fun onQueryTextChange(newText: String?): Boolean {
                            subscriber.onNext(newText ?: "")
                            subject.onNext(newText ?: "")
                            return true
                        }

                        override fun onQueryTextSubmit(query: String?): Boolean {
                            searchViewFacade.clearFocus()
                            return true
                        }
                    })
                })
                .map { text -> text.trim().replace(" +".toRegex(), " ") }
                .debounce(DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .filter { text -> text.length >= MIN_SYMBOL_TO_SEARCH || text.isEmpty() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { text ->
                    listeners.forEach {
                        if (text.isNotEmpty() || it.hasSearchOnEmptyString()) {
                            it.search(text)
                        }
                    }
                })
    }

    fun addTextListener(listener: (text: String) -> Unit): Disposable =
            subject.subscribe(listener)

    fun clear() {
        disposer.dispose()
        listeners.clear()
    }
}