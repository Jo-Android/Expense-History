package com.hgtech.expensehistory.ui.manager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

abstract class BaseFragment<B : ViewBinding>(
    private val bindingFactory: (LayoutInflater) -> B,
    private val isCustomBackPress: Boolean = false
) : Fragment() {

    private lateinit var _binding: B
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observe()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::_binding.isInitialized) {
            _binding = bindingFactory(inflater)
            initLayout()
        }
        return _binding.root
    }

    abstract fun observe()

    abstract fun initLayout()

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            this@BaseFragment.handleOnBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(backCallback)
    }

    override fun onStop() {
        super.onStop()
        backCallback.remove()
    }

    fun FloatingActionButton.showHide(list: RecyclerView) {
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && visibility == View.VISIBLE) {
                    hide()
                } else if (dy < 0 && visibility != View.VISIBLE) {
                    show()
                }
            }
        })
    }

    abstract fun onBackPressed()

    fun handleOnBackPressed() {
        if (isCustomBackPress)
            onBackPressed()
        else
            handelBackPressed()
    }

     fun handelBackPressed() {
         if (findNavController().backQueue.size <= 2) {
             requireActivity().finish()
         } else {
             findNavController().popBackStack()
         }
     }
}