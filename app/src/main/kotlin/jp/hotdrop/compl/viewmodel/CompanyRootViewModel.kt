package jp.hotdrop.compl.viewmodel

import android.content.Context
import jp.hotdrop.compl.repository.company.CompanyRepository
import javax.inject.Inject

class CompanyRootViewModel @Inject constructor(
        private val context: Context,
        private val companyRepository: CompanyRepository
): ViewModel() {
}