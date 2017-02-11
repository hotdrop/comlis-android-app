package jp.hotdrop.compl.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.hotdrop.compl.databinding.FragmentCompanyListBinding
import javax.inject.Singleton

@Singleton
class CompanyFragment : BaseFragment() {

    private lateinit var helper: ItemTouchHelper
    private lateinit var binding: FragmentCompanyListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        //getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return null
    }
}
