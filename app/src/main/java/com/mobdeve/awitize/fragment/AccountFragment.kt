package com.mobdeve.awitize.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.awitize.R
import java.lang.RuntimeException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {

    interface AccountListener{
        fun logout()
        fun deleteAccount()
    }

    private var listener : AccountListener? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var logout : Button
    private lateinit var deleteAccount : TextView
    private lateinit var  email : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        email = view.findViewById(R.id.tv_frag_acc_email)
        logout = view.findViewById(R.id.btn_frag_acc_logout)
        deleteAccount = view.findViewById(R.id.tv_frag_acc_delete)

        logout.setOnClickListener{
            listener?.logout()
        }

        deleteAccount.setOnClickListener{
            listener?.deleteAccount()
        }

        email.text = FirebaseAuth.getInstance().currentUser?.email.toString()

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is AccountListener){
            listener = context
        }
        else{
            throw RuntimeException("$context must implement AccountListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}