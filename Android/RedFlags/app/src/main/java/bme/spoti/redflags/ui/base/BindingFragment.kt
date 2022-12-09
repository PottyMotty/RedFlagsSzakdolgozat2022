package bme.spoti.redflags.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import bme.spoti.redflags.databinding.FragmentSingleRevealBinding
import bme.spoti.redflags.ui.ingame.screens.single_reveal.SingleRevealViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

abstract class BaseBindingFragment<V: ViewBinding>() : Fragment() {
	private var _binding: V? = null
	protected val binding get() = _binding!!


	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_binding = createBinding(inflater, container)
		return binding.root

	}
	abstract fun createBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): V

	override fun onDestroy() {
		super.onDestroy()
		_binding = null
	}
}