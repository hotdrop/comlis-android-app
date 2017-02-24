package jp.hotdrop.compl.view

import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor

object PageStateBus {

    val bus = PublishProcessor.create<NavigationPage>().toSerialized()!!

    fun observe(): Flowable<NavigationPage> {
        return bus
    }

}