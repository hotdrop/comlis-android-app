package jp.hotdrop.compl.viewmodel

import android.content.Context
import io.reactivex.Single
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import javax.inject.Inject

class SearchViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao

    fun getSearchResults(searchText: String): Single<List<ItemSearchResultViewModel>> {
        return companyDao.findAll().map { companies ->
                companies.filter { company -> isMatch(company, searchText) }
                        .map { ItemSearchResultViewModel(it, context, categoryDao) }
        }
    }

    private fun isMatch(company: Company, searchText: String): Boolean {
        if(company.name.contains(searchText, true)) {
            return true
        }
        val t1 = company.overview ?: return false
        if(t1.contains(searchText, true)) {
            return true
        }
        val t2 = company.note ?: return false
        if(t2.contains(searchText, true)) {
            return true
        }
        return false
    }
}