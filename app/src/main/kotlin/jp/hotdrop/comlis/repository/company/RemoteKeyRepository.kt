package jp.hotdrop.comlis.repository.company

import jp.hotdrop.comlis.model.RemoteKey
import jp.hotdrop.comlis.repository.OrmaHolder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteKeyRepository @Inject constructor(
        ormaHolder: OrmaHolder
){
    private val orma = ormaHolder.orma

    fun findLatest() =
            relation().selector().maxByDateEpoch() ?: 0

    fun save(value: Long) {
        val remoteKey = RemoteKey().apply { dateEpoch = value }
        orma.transactionSync {
            relation().inserter().execute(remoteKey)
        }
    }

    private fun relation() =
            orma.relationOfRemoteKey()
}