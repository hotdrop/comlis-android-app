package jp.hotdrop.compl.view.parts

import android.R
import android.app.Activity
import android.widget.ArrayAdapter
import android.widget.Spinner
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.model.Category

class CategorySpinner(private val spinner: Spinner, private val activity: Activity, categoryDao: CategoryDao) {

    // TODO blockingGet使っているのは良くない。以下のいずれかにするか決めかねている
    //  カテゴリーのリストを引数にとる。（ViewModel経由でスピナーを生成するようにして、ViewModelでリストを作る）
    //  カテゴリーでDB取得のObservableを扱う。ただ、カテゴリー数が大したことないので必要性を感じない
    private val categoryList = categoryDao.findAll().blockingGet()
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
            categoryList.filter{ it.name == spinner.selectedItem as String }.first().id

    fun setSelection(name: String?) {
        val position = adapter.getPosition(name)
        spinner.setSelection(position)
    }

    fun setSelection(id: Int) {
        val name = categoryList.filter { category -> category.id == id }.first().name
        setSelection(name)
    }
}
