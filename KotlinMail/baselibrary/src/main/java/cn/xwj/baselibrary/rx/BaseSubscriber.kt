package cn.xwj.baselibrary.rx

import android.util.Log
import cn.xwj.baselibrary.presenter.view.BaseView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Author: xw
 * Date: 2018-05-29 09:58:49
 * Description: BaseSubscriber: .
 */
open class BaseSubscriber<T>(val view: BaseView) : Observer<T> {
    private var disposable: Disposable? = null
    var mData: T? = null

    override fun onComplete() {
        view.hideLoading()
        Log.d("TAG", "disposed: ${disposable?.isDisposed}")
    }

    override fun onSubscribe(d: Disposable) {
        this.disposable = d
    }

    override fun onNext(t: T) {
        mData = t
    }

    override fun onError(e: Throwable) {
        if (e is BaseException) {
            view.showError(e.text)
        }
        view.hideLoading()
        disposable?.dispose()
        Log.d("TAG", "disposed: ${disposable?.isDisposed}")
    }
}

inline fun <T> Observable<T>.subscribe(view: BaseView, crossinline code: (data: T?) -> Unit) {
    this.subscribe(object : BaseSubscriber<T>(view) {
        override fun onComplete() {
            super.onComplete()
            code(mData)
        }
    })
}