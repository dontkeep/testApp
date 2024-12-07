package com.exal.testapp.view.home

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.data.Resource
import com.exal.testapp.databinding.FragmentHomeBinding
import com.exal.testapp.helper.formatRupiah
import com.exal.testapp.view.adapter.ExpensesAdapter
import com.exal.testapp.view.createlist.CreateListActivity
import com.exal.testapp.view.detailexpense.DetailExpenseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var pagingAdapter: ExpensesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagingAdapter = ExpensesAdapter{id, title ->
            navigateToDetail(id = id, title = title)
        }
        binding.rvExpense.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpense.adapter = pagingAdapter

        lifecycleScope.launch {
            homeViewModel.getLists("Track")
            homeViewModel.expenses.observe(viewLifecycleOwner) { pagingData ->
                Log.d("HomeFragment", "Paging Data: $pagingData")
                // calculate item.amount & item.total

                pagingAdapter.submitData(lifecycle, pagingData)
            }
        }

        homeViewModel.totalExpenses.observe(viewLifecycleOwner) { totalPrice ->
            binding.total.text = formatRupiah(totalPrice)
        }

        homeViewModel.totalItems.observe(viewLifecycleOwner) { totalItems ->
            binding.items.text = "${totalItems} items"
        }

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), CreateListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToDetail(id: Int, title: String) {
        val intent = Intent(requireContext(), DetailExpenseActivity::class.java)
        intent.putExtra(DetailExpenseActivity.EXTRA_EXPENSE_ID, id)
        intent.putExtra(DetailExpenseActivity.EXTRA_EXPENSE_TITLE, title)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
