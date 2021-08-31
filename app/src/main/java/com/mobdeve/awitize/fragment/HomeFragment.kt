package com.mobdeve.awitize.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobdeve.awitize.R
import java.lang.RuntimeException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    interface HomeListener{
        fun tapLibrary()
    }

    private var listener : HomeListener? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var attachedContext : Context? = null

    private lateinit var fab : FloatingActionButton

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

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        fab = view.findViewById(R.id.fab_frag_home)
        fab.setOnClickListener{
            listener?.tapLibrary()
        }

//        val categories = resources.getStringArray(R.array.Categories)
//        val spinner = view.findViewById<Spinner>(R.id.sp_frag_home_category)
//        if (spinner != null) {
//            val adapter = attachedContext?.let {
//                ArrayAdapter(
//                    it,
//                    android.R.layout.simple_spinner_item, categories
//                )
//            }
//            spinner.adapter = adapter
//
//            spinner.onItemSelectedListener = object :
//                AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                    Toast.makeText(attachedContext, "Selected ${categories.get(position)}", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>) {
//
//                }
//            }
//        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.attachedContext = context
        if(context is HomeListener)
            listener = context
        else
            throw RuntimeException("$context must implement HomeListener")
    }

    override fun onDetach() {
        super.onDetach()
        attachedContext = null
        listener = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}