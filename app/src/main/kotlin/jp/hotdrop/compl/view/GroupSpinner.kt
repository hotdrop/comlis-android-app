package jp.hotdrop.compl.view

import android.app.Activity
import android.widget.ArrayAdapter
import jp.hotdrop.compl.dao.GroupDao
import jp.hotdrop.compl.model.Group

class GroupSpinner(private val spinner: android.widget.Spinner, private val activity: Activity) {

    private val adapter by lazy {
        val groupNames = GroupDao.findAll().map(Group::name)
        ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, groupNames)
    }

    init {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun setSelection(name: String) {
        val position = adapter.getPosition(name)
        spinner.setSelection(position)
    }

}