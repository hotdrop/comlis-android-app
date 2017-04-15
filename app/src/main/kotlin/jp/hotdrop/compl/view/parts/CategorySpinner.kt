package jp.hotdrop.compl.view.parts

import android.R
import android.app.Activity
import android.widget.ArrayAdapter
import android.widget.Spinner
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.model.Category

class CategorySpinner(private val spinner: Spinner, private val activity: Activity) {

    private val categoryList = CategoryDao.findAll().blockingGet()
    private val adapter by lazy {
        val categoryNames = categoryList.map(Category::name)
        ArrayAdapter(activity, R.layout.simple_dropdown_item_1line, categoryNames)
    }

    init {
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun getSelection(): Int {
        val selectedName = spinner.selectedItem as String
        return categoryList.filter{ it.name == selectedName }
                .first()
                .id
    }

    fun setSelection(name: String?) {
        val position = adapter.getPosition(name)
        spinner.setSelection(position)
    }
}
