package jp.hotdrop.compl.view.parts

import android.R
import android.app.Activity
import android.widget.ArrayAdapter
import android.widget.Spinner
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.repository.category.CategoryRepository

class CategorySpinner(
        private val spinner: Spinner,
        private val activity: Activity,
        categoryRepository: CategoryRepository
) {

    private val categoryList = categoryRepository.findAll()
    private val adapter by lazy {
        val categoryNames = categoryList.map(Category::name)
        ArrayAdapter(activity, R.layout.simple_dropdown_item_1line, categoryNames).apply {
            setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        }
    }

    init {
        spinner.adapter = adapter
    }

    fun getSelection(): Int =
            categoryList.first { it.name == spinner.selectedItem as String }.id

    fun setSelection(name: String?) {
        val position = adapter.getPosition(name)
        spinner.setSelection(position)
    }

    fun setSelection(id: Int) {
        val name = categoryList.first { category -> category.id == id }.name
        setSelection(name)
    }
}
