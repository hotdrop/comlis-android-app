package jp.hotdrop.compl.viewmodel

import android.content.Context
import io.reactivex.Single
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.repository.category.CategoryRepository
import jp.hotdrop.compl.repository.company.CompanyRepository
import javax.inject.Inject

class SearchViewModel @Inject constructor(
        private val context: Context,
        private val companyRepository: CompanyRepository,
        private val categoryRepository: CategoryRepository
): ViewModel() {

    fun getSearchResults(searchText: String): Single<List<ItemSearchResultViewModel>> =
            companyRepository.findAll()
                    .map { companies ->
                        companies.filter { company -> isMatch(company, searchText) }
                                .map { ItemSearchResultViewModel(it, context, categoryRepository) }
                    }

    private fun isMatch(company: Company, searchText: String): Boolean {

        if(company.name.contains(searchText, true)) {
            return true
        }

        val overview = company.overview ?: return false
        if(overview.contains(searchText, true)) {
            return true
        }

        val note = company.note ?: return false
        if(note.contains(searchText, true)) {
            return true
        }

        return false
    }
}