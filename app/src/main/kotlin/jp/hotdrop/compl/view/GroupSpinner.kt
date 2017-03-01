package jp.hotdrop.compl.view

import android.app.Activity
import android.widget.ArrayAdapter
import jp.hotdrop.compl.dao.GroupDao

class GroupSpinner(private val binding: android.databinding.ViewDataBinding, private val activity: Activity) {

    fun setSpinner(spinner: android.widget.Spinner) {
        val groups = GroupDao.findAll()
        val adapter = ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, groups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter
        // TODO
        //spinner.setSelection()
    }

}