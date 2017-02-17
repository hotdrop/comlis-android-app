package jp.hotdrop.compl.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.databinding.FragmentCompanyRegisterBinding
import jp.hotdrop.compl.model.Company
import org.parceler.Parcels

class CompanyRegisterFragment : BaseFragment() {

    lateinit private var company: Company
    lateinit private var binding: FragmentCompanyRegisterBinding

    companion object {
        @JvmStatic val TAG = this.javaClass.simpleName
        fun create(): CompanyRegisterFragment = CompanyRegisterFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        company = Company()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyRegisterBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)

        binding.registerButton.setOnClickListener { v -> onClickRegister() }
        binding.company = company
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    private fun onClickRegister() {
        if(!canRegister()) {
            return
        }
        CompanyDao.insert(company)
        setResult()
        exit()

    }

    private fun canRegister(): Boolean {
        if(company.name.trim() == "") {
            Toast.makeText(this.activity, "会社名を入力してください。", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun setResult() {
        var intent = Intent()
        intent.putExtra(TAG, Parcels.wrap(company))
        activity.setResult(Activity.RESULT_OK, intent)
    }

    private fun exit() {
        if(isResumed) {
            activity.onBackPressed()
        }
    }
}
