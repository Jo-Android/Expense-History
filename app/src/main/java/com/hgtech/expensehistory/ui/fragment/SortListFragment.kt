package com.hgtech.expensehistory.ui.fragment

import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.database.repository.Categories
import com.hgtech.expensehistory.database.repository.Users
import com.hgtech.expensehistory.databinding.FragmentSortListBinding
import com.hgtech.expensehistory.databinding.LayoutItemTitleDescriptionBinding
import com.hgtech.expensehistory.ui.adapter.filter.CategoryAdapter
import com.hgtech.expensehistory.ui.adapter.filter.UserAdapter
import com.hgtech.expensehistory.ui.manager.fragment.BaseMarginBottomSheet
import com.hgtech.expensehistory.ui.manager.getDp
import com.hgtech.expensehistory.ui.manager.runWithLifecycle
import com.hgtech.expensehistory.ui.viewModel.SortListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class SortListFragment :
    BaseMarginBottomSheet<FragmentSortListBinding>(FragmentSortListBinding::inflate, true) {

    companion object {
        const val FILTER_ITEMS = "Filter_Items"
        private const val TAG = "SortListFragment"
    }


    private val viewModel by viewModel<SortListViewModel>()
    private val args by navArgs<SortListFragmentArgs>()
    private var users = ArrayList<Int>()
    private var fromTo: Pair<Long, Long>? = null

    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private val userAdapter: UserAdapter by lazy {
        UserAdapter {
            viewModel.filterCategories(categoryAdapter.list, userAdapter.getSelected())
            categoryAdapter.notifyItemRangeChanged(0, categoryAdapter.itemCount)
            setClearVisibility()
        }
    }

    private val categoryAdapter by lazy {
        CategoryAdapter(getDp(20f, requireActivity().resources)) {
            setClearVisibility()
        }
    }


    override fun setupLayout() {
        binding.datePicker.setup()
        users = ArrayList()
        with(binding.categoryList) {
            adapter = this@SortListFragment.categoryAdapter
            layoutManager = getListLayoutManager(categoryAdapter.addRemove)
        }

        with(binding.userList) {
            adapter = this@SortListFragment.userAdapter
            layoutManager = getListLayoutManager() {
                if (userAdapter.hasSelection())
                    userAdapter.addRemove.invoke()
            }
        }
    }

    private fun getListLayoutManager(addRemove: () -> Unit): RecyclerView.LayoutManager {
        return object : LinearLayoutManager(activity, VERTICAL, false) {
            override fun onLayoutCompleted(state: RecyclerView.State?) {
                addRemove.invoke()
            }
        }
    }

    override fun setListeners() {
        binding.topBar.apply {
            clear.isVisible = args.previousCategories.isNotEmpty() || args.previousUser.isNotEmpty()
            title.text = getString(R.string.filter)
            cancel.setOnClickListener {
                destroy()
            }
            save.setOnClickListener {
                findNavController().previousBackStackEntry?.savedStateHandle?.addStack()
                destroy()
            }
            clear.setOnClickListener {
                categoryAdapter.clearSelection()
                userAdapter.clearSelection()
                clearDate()
            }
        }
    }

    private fun destroy() {
        users = ArrayList()
        findNavController().popBackStack()
    }

    private fun SavedStateHandle.addStack() {
        viewModel.getSelectionList(
            categoryAdapter.getSelected(),
            categoryAdapter.list,
            userAdapter.getSelected(),
            fromTo
        ).also {
            set(FILTER_ITEMS, it)
        }
    }

    override fun observe() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                viewModel.getUser().combine(viewModel.getCategories()) { users, categories ->
                    FilterList(
                        users.map { user ->
                            Users(
                                user,
                                isChecked = args.previousUser.find { it == user.id } != null
                            )
                        },
                        categories.map { cat ->
                            Categories(
                                cat,
                                isChecked = args.previousCategories.find { it == cat.id } != null
                            )
                        })
                }
            }.collectLatest {
                lifecycleScope.runWithLifecycle(lifecycle) {
                    userAdapter.addList(it.users)
                    categoryAdapter.addList(it.categories)
                }
            }
        }
    }

    data class FilterList(
        val users: List<Users>,
        val categories: List<Categories>
    )

    private fun LayoutItemTitleDescriptionBinding.setup() {
        title.text = getString(R.string.date_title)
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.textColor))
        if (args.from != 0L)
            fromTo = Pair(args.from, args.to)

        fromTo?.let {
            value.text = StringBuilder(sdf.format(Date(it.first))).append(" - ")
                .append(sdf.format(Date(it.second)))
        }

        binding.delete.apply {
            isVisible = value.text.isNotEmpty()
            setOnClickListener {
                clearDate()
            }
        }
        root.setOnClickListener {
            if (!binding.delete.isVisible)
                datePickerDialog()
        }
    }

    private fun clearDate() {
        binding.datePicker.value.text = ""
        fromTo = null
        binding.delete.isVisible = false
        setClearVisibility()
    }

    private fun LayoutItemTitleDescriptionBinding.datePickerDialog() {

        val builder: MaterialDatePicker.Builder<Pair<Long, Long>> =
            MaterialDatePicker.Builder.dateRangePicker()

        builder.setCalendarConstraints(
            CalendarConstraints.Builder()
                .setValidator(
                    DateValidatorPointBackward.now()
                ).build()
        )
        builder.setTitleText("Select a date range to Filter")

        // Building the date picker dialog
        val datePicker = builder.build()
        val existingFragment =
            requireActivity().supportFragmentManager.findFragmentByTag("DATE_PICKER")
        if (existingFragment != null)
            requireActivity().supportFragmentManager.beginTransaction().remove(existingFragment)
                .commit()
        datePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener {
            val startDateString: String = sdf.format(Date(it.first))
            val endDateString: String = sdf.format(Date(it.second))

            value.text =
                StringBuilder(startDateString).append(" - ").append(endDateString)
            binding.delete.isVisible = true
            fromTo = Pair(
                (sdf.parse(startDateString)?.time) ?: it.first,
                (sdf.parse(endDateString)?.time) ?: it.second
            )
            setClearVisibility()
        }
    }

    private fun setClearVisibility() {
        binding.topBar.clear.isVisible =
            fromTo != null || userAdapter.hasSelection() || categoryAdapter.hasSelection()
    }
}
