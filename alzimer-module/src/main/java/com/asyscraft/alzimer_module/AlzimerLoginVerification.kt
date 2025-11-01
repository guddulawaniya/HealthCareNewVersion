package com.asyscraft.alzimer_module

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityAlzimerLoginVerificationBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlzimerLoginVerification : BaseActivity() {

    private lateinit var binding: ActivityAlzimerLoginVerificationBinding
    private val viewModel: YourViewModel by viewModels()

    private lateinit var usertype:String

    var otp = ""
    var phoneno = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlzimerLoginVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        otp = intent.getStringExtra("otp").toString()
        phoneno = intent.getStringExtra("phoneno").toString()
        usertype = intent.getStringExtra("type").toString() //Default to Patient

        binding.phone.setText(otp)


        binding.save.setOnClickListener{

            otp = binding.phone.text.toString().trim()

            Log.d("button clicl", "otp")
            if (phoneno.isNotEmpty() && otp.isNotEmpty()) {
//                viewModel.Verifyotp(
//                    phonenumber = phoneno,
//                    otp = otp
//
//                )
            }else {
                Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show()
            }
        }


//        observeResponseverifyotp()
    }





//    private fun observeResponseverifyotp() {
//        lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.verifyotp.observe(this@AlzimerLoginVerification) { response ->
//
//                    if(response.success){
//
//                        SavedPrefManager.saveStringPreferences(this@AlzimerLoginVerification, SavedPrefManager.TOKEN, response.token)
//
//                        Toast.makeText(this@AlzimerLoginVerification, "Login successfully", Toast.LENGTH_SHORT).show()
//
//
//                        val intent = Intent(this@AlzimerLoginVerification, BrainHealthCheck::class.java)
////                        val intent = Intent(this@AlzimerLoginVerification, HomePage::class.java)
//                        intent.putExtra("type",usertype)
//                        startActivity(intent)
////                        if(response.isFirstTime){
//////                          val intent = Intent(this@Login, MainActivity::class.java)
////                            val intent = Intent(this@AlzimerLoginVerification, CreateAccount::class.java)
////                            startActivity(intent)
////                            finish()
////                        }else{
////                            val intent = Intent(this@AlzimerLoginVerification, MainActivity::class.java)
////                            startActivity(intent)
////                            finish()
////                        }
//
//
//                    }else{
//
//                    }
//
//                }
//            }
//        }
//    }

}