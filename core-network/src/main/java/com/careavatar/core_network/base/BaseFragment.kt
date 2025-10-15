package com.careavatar.core_network.base

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.careavatar.core_ui.R
import com.careavatar.core_utils.ApiResult
import com.careavatar.core_utils.UserPref
import com.careavatar.userapploginmodule.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
open class BaseFragment : Fragment() {

    @Inject
    lateinit var userPref: UserPref   // Will be injected by Hilt

    protected open val shouldHideKeyboardOnTouchOutside: Boolean = true
    private var dialog: Dialog? = null
    private var mDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)) {
            setMIUIStatusBarDarkMode(requireActivity(), darkMode = true)
        }
    }

    // Generic StateFlow collector
    protected fun <T> collectApiResultOnStarted(
        stateFlow: StateFlow<ApiResult<T>>,
        onSuccess: (T) -> Unit
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                stateFlow.collect { result ->
                    when (result) {
                        is ApiResult.Idle -> {}
                        is ApiResult.Loading -> showProgressDialog()
                        is ApiResult.Success -> {
                            hideProgressDialog()
                            onSuccess(result.data)
                        }

                        is ApiResult.Error -> {
                            hideProgressDialog()
                            // showToast(result.message ?: "Something went wrong")
                        }
                    }
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun launchIfInternetAvailable(block: suspend () -> Unit) {
        if (NetworkUtils.isInternetAvailable(requireContext())) {
            lifecycleScope.launch { block() }
        } else {
            showToast("Please check your internet")
        }
    }

    protected fun showProgressDialog() {
        if (mDialog == null) {
            mDialog = Dialog(requireContext()).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
                setContentView(R.layout.pop_up_custom_progress)
                window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                setCanceledOnTouchOutside(false)
            }
        }
        mDialog?.show()
    }

    fun setMIUIStatusBarDarkMode(activity: Activity, darkMode: Boolean) {
        val window: Window = activity.window
        try {
            val clazz = window.javaClass
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            val darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod(
                "setExtraFlags",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            if (darkMode) {

                extraFlagField.invoke(window, darkModeFlag, darkModeFlag)
            } else {

                extraFlagField.invoke(window, 0, darkModeFlag)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun hideProgressDialog() {
        mDialog?.dismiss()
        mDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }


    fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}