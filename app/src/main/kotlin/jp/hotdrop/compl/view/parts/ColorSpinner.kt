package jp.hotdrop.compl.view.parts

import android.R
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import jp.hotdrop.compl.util.ColorDataUtil

class ColorSpinner(private val spinner: Spinner, private val activity: Activity) {

    private val adapter by lazy {
        Adapter(activity, R.layout.simple_dropdown_item_1line, ColorDataUtil.getNames())
    }

    init {
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun getSelection(): String {
        return spinner.selectedItem as String
    }

    fun setSelection(name: String) {
        val position = adapter.getPosition(name)
        spinner.setSelection(position)
    }

    private inner class Adapter(context: Context?, textViewResourceId: Int, var colorNames: List<String>)
        : ArrayAdapter<String>(context, textViewResourceId, colorNames) {

        private val itemTextSize = 20.toFloat()

        @Suppress("DEPRECATION")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val v = convertView ?: View.inflate(context, android.R.layout.simple_dropdown_item_1line, null)
            val textView = ((v as TextView).findViewById(android.R.id.text1) as TextView)
            textView.setTextColor(context.resources.getColor(ColorDataUtil.getColorNormal(colorNames[position])))
            textView.text = colorNames[position]
            textView.textSize = itemTextSize
            return textView
        }

        @Suppress("DEPRECATION")
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val v = convertView ?: View.inflate(context, android.R.layout.simple_dropdown_item_1line, null)
            val textView = (v.findViewById(android.R.id.text1) as TextView)
            textView.setTextColor(context.resources.getColor(ColorDataUtil.getColorNormal(colorNames[position])))
            textView.text = colorNames[position]
            textView.textSize = itemTextSize
            return textView
        }
    }
}