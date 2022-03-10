package com.example.pinterest.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.pinterest.R

class SearchResultFragment : Fragment(R.layout.fragment_search_result) {

    private lateinit var edtSearch: EditText
    private lateinit var tvCancel: TextView
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
    }

    private fun initViews(view: View) {
        edtSearch = view.findViewById(R.id.edtSearch)
        tvCancel = view.findViewById(R.id.tvCancel)
        navController = Navigation.findNavController(view)

        doCancelAction()
    }


    private fun doCancelAction() {
        tvCancel.setOnClickListener {
            navController.navigate(R.id.action_searchResultFragment_to_searchFragment)
        }
    }
}
