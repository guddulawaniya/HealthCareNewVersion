package com.careavatar.core_network.base

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.core_navigation.AppNavigator
import com.asyscraft.core_navigation.Navigator
import com.careavatar.core_utils.ApiResult
import com.careavatar.core_ui.R
import com.careavatar.core_utils.CurrentActivityHolder
import com.careavatar.core_utils.UserPref
import com.careavatar.userapploginmodule.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var userPref: UserPref   // Will be injected by Hilt

    protected open val shouldHideKeyboardOnTouchOutside: Boolean = true
    private var dialog: Dialog? = null
    private var mDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)


        ViewCompat.setOnApplyWindowInsetsListener(window.decorView.rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
        }

        // Setup AppNavigator globally
        AppNavigator.setNavigator(object : Navigator {
            override fun navigateToLogin() {
                val activity = CurrentActivityHolder.currentActivity ?: return
                try {
                    // Dynamically load login screen (no hard dependency)
                    val clazz = Class.forName("com.asyscraft.login_module.ui.LoginActivity")
                    val intent = Intent(activity, clazz)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity.startActivity(intent)
                    activity.finish()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
        })
        if (Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)) {
            setMIUIStatusBarDarkMode(this, darkMode = true)
        }
    }
    inline fun <reified T : Activity> launchActivity() {
        startActivity(Intent(this, T::class.java))
    }


    protected fun showInputError(editText: View, errorMessage: String) {
        // Show toast message
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()

        // Apply shake animation
        editText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))

        // Set red border background
        editText.setBackgroundResource(R.drawable.edittext_error_border)

        // Optional: Clear text


        if (editText is EditText) {
            editText.setBackgroundResource(R.drawable.edit_text_bg)
            editText.setText("")
            // Revert to normal border on text change
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    editText.setBackgroundResource(R.drawable.edit_text_bg)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
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
        if (NetworkUtils.isInternetAvailable(this)) {
            lifecycleScope.launch { block() }
        } else {
            showToast("Please check your internet")
        }
    }

    protected fun showProgressDialog() {
        if (mDialog == null) {
            mDialog = Dialog(this).apply {
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (shouldHideKeyboardOnTouchOutside && ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
