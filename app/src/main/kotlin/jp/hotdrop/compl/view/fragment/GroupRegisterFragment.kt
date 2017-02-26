package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import jp.hotdrop.compl.dao.GroupDao
import jp.hotdrop.compl.databinding.FragmentGroupRegisterBinding
import jp.hotdrop.compl.model.Group
import org.parceler.Parcels

class GroupRegisterFragment: BaseFragment() {

    lateinit private var group: Group
    lateinit private var binding: FragmentGroupRegisterBinding

    companion object {
        @JvmStatic val TAG = GroupRegisterFragment::class.java.simpleName!!
        fun create() = GroupRegisterFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        group = Group()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGroupRegisterBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)

        //binding.registerButton.setOnClickListener { onClickRegister() }
        binding.group = group

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
        GroupDao.insert(group)
        setResult()
        exit()
    }

    private fun canRegister(): Boolean {
        if(group.name.trim() == "") {
            Toast.makeText(this.activity, "名称を入力してください。", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun setResult() {
        val intent = Intent()
        intent.putExtra(TAG, Parcels.wrap(group))
        activity.setResult(Activity.RESULT_OK, intent)
    }

    private fun exit() {
        if(isResumed) {
            activity.onBackPressed()
        }
    }
}