package com.mobdeve.awitize.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.mobdeve.awitize.R
import java.lang.RuntimeException

class NavFragment : Fragment() {

    interface NavListener{
        fun tapSearch()
        fun tapAccount()
        fun tapBack()
    }

    private var listener : NavListener ? = null
    private lateinit var back : ImageButton
    private lateinit var search : ImageButton
    private lateinit var account : ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nav, container, false)
        back = view.findViewById(R.id.ib_home)
        search = view.findViewById(R.id.ib_search)
        account = view.findViewById(R.id.ib_account)
        back.setOnClickListener{
            listener?.tapBack()
        }
        search.setOnClickListener{
            listener?.tapSearch()
        }
        account.setOnClickListener{
            listener?.tapAccount()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is NavListener){
            listener = context
        }
        else{
            throw RuntimeException("$context must implement NavListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}