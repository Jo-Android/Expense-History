package com.hgtech.expensehistory.ui.fragment.settings

import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import com.hgtech.expensehistory.database.backup.workmanager.BackupManager.generateBackup
import com.hgtech.expensehistory.database.backup.workmanager.BackupManager.restore
import com.hgtech.expensehistory.database.repository.FilterItems
import com.hgtech.expensehistory.databinding.FragmentBackupRestoreBinding
import com.hgtech.expensehistory.ui.fragment.SortListFragment
import com.hgtech.expensehistory.ui.manager.fragment.BaseMarginBottomSheet
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets


class BackupRestoreFragment :
    BaseMarginBottomSheet<FragmentBackupRestoreBinding>(
        FragmentBackupRestoreBinding::inflate,
        false
    ) {


    private var filter: FilterItems? = null
    private lateinit var backupLuncher: ActivityResultLauncher<Intent>
    private lateinit var restoreLuncher: ActivityResultLauncher<Intent>

    override fun setListeners() {
        binding.apply {
            close.setOnClickListener {
                findNavController().popBackStack()
            }
            restore.setOnClickListener {
                isButtonEnable(false)
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "application/json"
                restoreLuncher.launch(intent)
            }
            backup.setOnClickListener {
                findNavController().navigate(
                    BackupRestoreFragmentDirections.actionBackupRestoreFragmentToSortListFragment(
                        intArrayOf(), intArrayOf(), 0L, 0L
                    )
                )
            }
        }
    }


    override fun setupLayout() {
        setupBackupLauncher()
        setupRestoreLauncher()
    }

    private fun setupRestoreLauncher() {
        restoreLuncher = requireActivity().activityResultRegistry.register(
            "RESTORE",
            viewLifecycleOwner,
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            result.data?.let { data ->
                data.data?.let { uri ->
                    openFile(uri)
                }.also {
                    if (it == null)
                        Log.e("TAG", "URI Selection Null")
                }
            }.also {
                if (it == null)
                    Log.e("TAG", "result from intent Null")
            }
        }
    }

    private val workInfoObserver = Observer<WorkInfo?> { value ->
        isButtonEnable(value?.state == WorkInfo.State.SUCCEEDED || value?.state == WorkInfo.State.FAILED)
        if (value?.state == WorkInfo.State.SUCCEEDED) {
            Log.d("TAG", "Work Info Suceeded")
            if (context != null) {
                Toast.makeText(requireContext(), "Task Finished Successfully", Toast.LENGTH_SHORT)
                    .show()
            } else
                Log.e("TAG", "Couldn't share backup File. App is minimized")
        }
    }

    private fun isButtonEnable(b: Boolean) {
        binding.apply {
            restore.isClickable = b
            backup.isClickable = b
        }
    }


    private fun setupBackupLauncher() {
        backupLuncher = requireActivity().activityResultRegistry.register(
            "BACKUP",
            viewLifecycleOwner,
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            result.data?.data?.let { uri ->
                isButtonEnable(false)
                generateBackup(
                    requireActivity().application,
                    requireContext(),
                    uri,
                    filter
                ).getState()
            }
        }
    }

    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    private fun openFile(uri: Uri) {
        val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
        val textBuilder = StringBuilder()
        BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
            var c: Int
            while (reader.read().also { c = it } != -1) {
                textBuilder.append(c.toChar())
            }
        }
        restore(requireActivity().application, requireContext(), textBuilder.toString()).getState()
    }

    override fun observe() {
        lifecycleScope.launch {
            findNavController().currentBackStackEntry?.savedStateHandle?.getStateFlow<FilterItems?>(
                SortListFragment.FILTER_ITEMS,
                null
            )?.collectLatest { filterItems ->
                filter = filterItems
                if (filterItems != null)
                    startBackup()
            }
        }
    }

    private fun startBackup() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.type = "application/json"
        intent.putExtra(Intent.EXTRA_TITLE, "backup")
        backupLuncher.launch(intent)
    }


    private fun LiveData<WorkInfo>.getState() {
        removeObserver(workInfoObserver)
        observe(viewLifecycleOwner, workInfoObserver)
    }
}
