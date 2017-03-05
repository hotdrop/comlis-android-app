package jp.hotdrop.compl.view

import android.app.Activity
import android.widget.ArrayAdapter
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.model.Category

class CategorySpinner(private val spinner: android.widget.Spinner, private val activity: Activity) {

    private val categoryList: MutableList<Category> = CategoryDao.findAll()

    private val adapter by lazy {
        val categoryNames = categoryList.map(Category::name)
        ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, categoryNames)
    }

    init {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun getSelection(): Int {
        val selectedName = spinner.selectedItem as String
        return categoryList.filter{ it.name == selectedName }
                .first()
                .id
    }

    fun setSelection(name: String) {
        val position = adapter.getPosition(name)
        spinner.setSelection(position)
    }
}
