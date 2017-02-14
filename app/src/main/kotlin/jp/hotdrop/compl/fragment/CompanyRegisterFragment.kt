package jp.hotdrop.compl.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.hotdrop.compl.databinding.FragmentCompanyRegisterBinding

class CompanyRegisterFragment : BaseFragment() {

    lateinit private var binding: FragmentCompanyRegisterBinding

    companion object {
        fun create(): CompanyRegisterFragment = CompanyRegisterFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyRegisterBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)

        // TODO 登録ボタンのリスナーを実装する
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }
}
