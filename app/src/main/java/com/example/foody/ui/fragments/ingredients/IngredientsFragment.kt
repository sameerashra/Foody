package com.example.foody.ui.fragments.ingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foody.R
import com.example.foody.adapters.IngredientsAdapter
import com.example.foody.databinding.FragmentIngredientsBinding
import com.example.foody.models.Result
import com.example.foody.util.Constants.Companion.RECIPE_RESULT_KEY

class IngredientsFragment : Fragment() {

    private var _binding: FragmentIngredientsBinding? = null
    private val binding get() = _binding!!

    private val mAdapter: IngredientsAdapter by lazy {
        IngredientsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        setUpRecyclerView()

        myBundle?.extendedIngredients?.let {
            mAdapter.setData(it)
        }

        return binding.root
    }

    private fun setUpRecyclerView(){
        binding.ingredientsRecyclerView.adapter = mAdapter
        binding.ingredientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}