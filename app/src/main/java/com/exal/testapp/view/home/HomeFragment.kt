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
import com.exal.testapp.view.sharelist.ShareListActivity
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

        binding.sharedListBtn.setOnClickListener{
            val intent = Intent(requireContext(), ShareListActivity::class.java)
            startActivity(intent)
        }

        pagingAdapter = ExpensesAdapter{id, title, date ->
            navigateToDetail(id = id, title = title, date = date)
        }
        binding.rvExpense.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpense.adapter = pagingAdapter

        lifecycleScope.launch {
            homeViewModel.getLists("Track")
            homeViewModel.expenses.observe(viewLifecycleOwner) { pagingData ->
                Log.d("HomeFragment", "Paging Data: $pagingData")

                pagingAdapter.submitData(lifecycle, pagingData)
                val list = pagingAdapter.snapshot().items
                homeViewModel.setLists(list)
            }
        }

        homeViewModel.totalExpenses.observe(viewLifecycleOwner) { totalPrice ->
            binding.total.text = formatRupiah(totalPrice)
        }

        homeViewModel.totalItems.observe(viewLifecycleOwner) { totalItems ->
            binding.items.text = "${totalItems} items"
        }
    }

    private fun navigateToDetail(id: Int, title: String, date: String) {
        val intent = Intent(requireContext(), DetailExpenseActivity::class.java)
        val type = "Track"
        intent.putExtra(DetailExpenseActivity.EXTRA_EXPENSE_ID, id)
        intent.putExtra(DetailExpenseActivity.EXTRA_EXPENSE_TITLE, title)
        intent.putExtra(DetailExpenseActivity.EXTRA_EXPENSE_DATE, date)
        intent.putExtra(DetailExpenseActivity.EXTRA_EXPENSE_TYPE, type)
        Log.d("HomeFragment", "Expense ID: $id, Title: $title, Date: $date, Type: $type")
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
