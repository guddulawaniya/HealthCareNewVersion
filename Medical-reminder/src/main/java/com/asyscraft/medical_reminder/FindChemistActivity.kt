package com.asyscraft.medical_reminder

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asyscraft.medical_reminder.databinding.ActivityFindChemistBinding
import com.careavatar.core_network.base.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindChemistActivity : BaseActivity() {
    private lateinit var binding: ActivityFindChemistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindChemistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addChemistBtn.setOnClickListener {
            showAddChemistBottomSheet()
        }
    }

    private fun showAddChemistBottomSheet() {
        val dialog = BottomSheetDialog(this, com.careavatar.core_ui.R.style.CustomBottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.add_chemist_manually_layout, null)
        dialog.setContentView(view)

        val cancelBtn = view.findViewById<TextView>(R.id.cancelBtn)
        val addChemistBtn = view.findViewById<TextView>(R.id.addChemistBtn)

        val etMobileNumber = view.findViewById<EditText>(R.id.etMobileNumber)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etAddress = view.findViewById<EditText>(R.id.etAddress)


        addChemistBtn.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etMobileNumber.text.toString().trim()
            val etAddress = etAddress.text.toString().trim()

            if (name.isEmpty()) {
                showToast("Please fill all fields")
            } else if(etAddress.isEmpty()){
                showToast("Please enter the address")
            }
            else if (phone.length != 10) {
                showToast("Please enter valid phone number")
            } else {
                showToast("Saved")

                dialog.dismiss()
            }
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}