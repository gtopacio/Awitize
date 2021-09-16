package com.mobdeve.awitize.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.awitize.R
import java.lang.RuntimeException

class AccountFragment : Fragment() {

    interface AccountListener{
        fun logout()
        fun deleteAccount()
    }

    private var listener : AccountListener? = null

    private lateinit var logout : Button
    private lateinit var deleteAccount : TextView
    private lateinit var  email : TextView

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
            val id = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseDatabase.getInstance().getReference("users/$id").setValue(null)
            Toast.makeText(context,"Deleted account: " + email.text.toString(),
                Toast.LENGTH_SHORT).show()

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
}