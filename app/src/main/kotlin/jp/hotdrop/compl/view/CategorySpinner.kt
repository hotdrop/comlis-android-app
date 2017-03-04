package jp.hotdrop.compl.view

import android.app.Activity
import android.widget.ArrayAdapter
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.model.Category

class CategorySpinner(private val spinner: android.widget.Spinner, private val activity: Activity) {

    private val groupList by lazy {
        CategoryDao.findAll()
    }

    private val adapter by lazy {
        val groupNames = groupList.map(Category::name)
        ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, groupNames)
    }

    init {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun getSelection(): Int {
        val selectedName = spinner.selectedItem as String
        return groupList.filter{ group -> group.name == selectedName }
                .first()
                .id
    }

    fun setSelection(name: String) {
        val position = adapter.getPosition(name)
        spinner.setSelection(position)
    }
}
