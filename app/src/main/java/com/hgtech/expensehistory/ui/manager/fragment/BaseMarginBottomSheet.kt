package com.hgtech.expensehistory.ui.manager.fragment

import android.app.Dialog
import android.graphics.Insets
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.ui.manager.getDp
import com.hgtech.expensehistory.ui.manager.hideKeyboard

abstract class BaseMarginBottomSheet<B : ViewBinding>(
    private val bindingFactory: (LayoutInflater) -> B,
    private val shouldBeFullHeight: Boolean = false,
) : BottomSheetDialogFragment() {

    private var _binding: B? = null
    val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
//            if (shouldBeFullHeight)
            setOnShowListener {
                setup(it as BottomSheetDialog)
            }

        }
    }

    private fun setup(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        bottomSheet.setBackgroundResource(R.drawable.background_round_top)
        if (shouldBeFullHeight) {
            val layoutParams: CoordinatorLayout.LayoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            )

            layoutParams.setMargins(getDp(9f, resources), 0, getDp(9f, resources), 0)
            bottomSheet.layoutParams = layoutParams
        }
    }

    private fun getWindowHeight(): Int {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingFactory(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setListeners()
        observe()
        hideKeyboard(requireActivity())
    }

    abstract fun setupLayout()


    abstract fun setListeners()

    abstract fun observe()

}