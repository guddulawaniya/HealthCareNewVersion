package com.asyscraft.alzimer_module


import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.audiofx.Visualizer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.adapter.AdapterAddedimage
import com.asyscraft.alzimer_module.adapter.AdapterAlbum
import com.asyscraft.alzimer_module.adapter.AdapterImageTask
import com.asyscraft.alzimer_module.adapter.AdapterMemory
import com.asyscraft.alzimer_module.adapter.AdapterPlannerDate
import com.asyscraft.alzimer_module.adapter.AdapterPlannerMessage
import com.asyscraft.alzimer_module.adapter.AdapterTestcard
import com.asyscraft.alzimer_module.adapter.AdapterVoice
import com.asyscraft.alzimer_module.adapter.Adapterline
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AlbumData
import com.careavatar.core_model.alzimer.MemoryItem
import com.careavatar.core_model.alzimer.MessageData
import com.careavatar.core_model.alzimer.TaskItem
import com.careavatar.core_model.alzimer.VoiceItem
import com.careavatar.core_model.alzimer.game_response
import com.asyscraft.alzimer_module.databinding.ActivityHomePageBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.asyscraft.alzimer_module.utils.TestCardItem
import com.asyscraft.alzimer_module.utils.WaveformView
import com.asyscraft.alzimer_module.utils.androidExtension
import com.asyscraft.alzimer_module.utils.showDatePicker
import com.asyscraft.alzimer_module.adapter.AdapterGame
import com.careavatar.core_model.alzimer.date_response_task
import com.careavatar.core_model.alzimer.line_respone
import com.asyscraft.alzimer_module.utils.GridSpacing
import com.careavatar.core_network.base.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import kotlin.toString

@AndroidEntryPoint
class HomePage : BaseActivity() {

    private lateinit var binding: ActivityHomePageBinding


    private lateinit var adapter: AdapterGame

    private lateinit var  type : String


    private lateinit var adapterdate: AdapterPlannerDate
    private var selectedDateFormatted: String? = null

    private lateinit var dialog: AlertDialog

    private lateinit var dialogbottom: BottomSheetDialog


    private lateinit var adapterstatusline: Adapterline



    private lateinit var adaptertestcard: AdapterTestcard
    private  var datalisttask: ArrayList<TestCardItem> = ArrayList()


    private  var taskDataList: List<TaskItem> = emptyList()



    private var datalistplannermessage: ArrayList<MessageData> = ArrayList()
    private lateinit var adaptermessage: AdapterPlannerMessage


    private lateinit var adaptervoice: AdapterVoice
    private  var datalistforVoicelist: ArrayList<VoiceItem> = ArrayList()


    private lateinit var adapterMemory: AdapterMemory
    private  var datalistformemorylist: ArrayList<MemoryItem> = ArrayList()


    private lateinit var adapterAlbum: AdapterAlbum
    private var datalistforalbum: ArrayList<AlbumData> = ArrayList()



    private lateinit var adapterimagetask: AdapterImageTask
    private  var datalistforimagetask = ArrayList<String>()




    private var mediaRecorder: MediaRecorder? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isRecording = false
    private lateinit var waveformView: WaveformView
    private var recordedFile: File? = null



    private var startTime: Long = 0L
    private var elapsedTime: Long = 0L // Keeps track of total time before each pause
    private val timeHandler = Handler(Looper.getMainLooper())
    private lateinit var updateTimeRunnable: Runnable
    private lateinit var adapterAddedimage: AdapterAddedimage

    private val viewModel: YourViewModel by viewModels()

    private lateinit var caregiverid: String
    private lateinit var patientid: String




    //    image add in create album
    private val PICK_IMAGE_REQUEST = 1
    private val PICK_IMAGE_REQUEST_SINGLE = 0
    private val selectedImages = mutableListOf<Uri>()


    //  memory create
    private lateinit var selectedImageButton: ImageButton
    private lateinit var Texthint: TextView
    private var selectedThumbnailUri: Uri? = null


    private var mediaPlayer: MediaPlayer? = null
    private var visualizer: Visualizer? = null
    private var isPlayingVoice  = false  // ✅ Make sure this is on its own line!


    var moodecompleted: Boolean = false

    private lateinit var userid : String


    private val updateVisualizer = object : Runnable {
        override fun run() {
            mediaRecorder?.maxAmplitude?.let { amplitude ->
                waveformView.addAmplitude(amplitude)
            }
            if (isRecording) {
                handler.postDelayed(this, 100)
            }
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getStringExtra("type").toString()

        val patientimage = intent.getStringExtra("patientimage").toString()
        val isAssesment = intent.getBooleanExtra("isassesment", false)

        patientid = SavedPrefManager.getStringPreferences(this, SavedPrefManager.PATIENT_ID).toString()
        caregiverid = SavedPrefManager.getStringPreferences(this, SavedPrefManager.CAREGIVER_ID).toString()
        userid = SavedPrefManager.getStringPreferences(this, SavedPrefManager.USERID).toString()


        Log.d("patttiiiend", patientid)
        Log.d("caregiverid", caregiverid)
        Log.d("tyyppee", type)

        // Initialize RecyclerView
        setupRecyclerView()

        // Bottom Navigation Click Listeners
        binding.home.setOnClickListener {
            setActiveTab("home")
        }

        binding.planner.setOnClickListener {
            setActiveTab("planner")
        }

        binding.memory.setOnClickListener {
            setActiveTab("memory")
            viewModel.GetAlbumlist()
            viewModel.GetMemorylist()
            viewModel.GetVoicelist()

        }

        binding.bookappointment.setOnClickListener{
            val intent = Intent(this, BookAppointment::class.java)
            startActivity(intent)
        }

        binding.meditation.setOnClickListener{
            val intent = Intent(this, Meditation::class.java)
            startActivity(intent)
        }

        binding.sosBtn.setOnClickListener{
            val intent = Intent(this, Sos::class.java)
            intent.putExtra("type", type)
            startActivity(intent)
        }

        binding.sessions.setOnClickListener{
            val intent = Intent(this, Sessions::class.java)
            startActivity(intent)
        }

        binding.whoiam.setOnClickListener{
            val intent = Intent(this, WhoIAm::class.java)
            intent.putExtra("type", type)
            startActivity(intent)
        }

        binding.todolist.setOnClickListener{
            val intent = Intent(this, TodoList::class.java)
            startActivity(intent)
        }

        binding.seeallgame.setOnClickListener{
            val intent = Intent(this, SuggestedGames::class.java)
            startActivity(intent)
        }

//        binding.Geoloaction.setOnClickListener{
//            val intent = Intent(this, Geolocation::class.java)
//            startActivity(intent)
//        }


        val today = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.date.text = sdf.format(today)

        binding.date.setOnClickListener {
            showDatePicker(this, binding.date)
        }

        binding.dailyplanner.visibility = View.VISIBLE
        binding.activateplanner.visibility = View.GONE


        Log.d("tyyyyppe", type)

        if(type == "Caregiver"){
            binding.activateplanner.visibility = View.VISIBLE
            binding.dailyplanner.visibility = View.GONE
            binding.geolocationtext.setText("Geolocation")
            binding.sosBtn.visibility = View.GONE

            binding.todayactivity.visibility = View.VISIBLE


            binding.plannerfragment.visibility = View.GONE
            binding.Plannercaregiverscreen.visibility = View.VISIBLE


            binding.Geoloaction.setOnClickListener{
                val intent = Intent(this, Geolocation::class.java)

                intent.putExtra("patientimage", patientimage)
                startActivity(intent)
            }

            if(isAssesment){
                binding.activateplanner.visibility = View.GONE
            }

        }

        if(type == "Patient"){
            binding.geolocationtext.setText("Where Am I?")

            binding.Geoloaction.setOnClickListener{
                val intent = Intent(this, WhereIAm::class.java)
                startActivity(intent)
            }

            binding.sosBtn.visibility = View.VISIBLE
            binding.todayactivity.visibility = View.GONE

            binding.plannerfragment.visibility = View.VISIBLE
            binding.Plannercaregiverscreen.visibility = View.GONE


        }


        binding.voicenoteaddBtn.setOnClickListener{
            addvoicenote()
        }

        binding.memoryaddBtn.setOnClickListener{
            addmemory()
        }

        binding.albumaddBtn.setOnClickListener{
            addAlbum()
        }



        setupRecyclerViewdate()
        setupRecyclerViewplannermessage()
        setupRecyclerViewline()
        setupRecyclerViewtestcard(emptyList())
        setupRecyclerViewvoicenote()
        setupRecyclerViewmemory()
        setupRecyclerViewalbum()

        setupRecyclerViewActivityImagetask()




        observeCreateAlbumResponse()
        observeResponsegetAlbumlist()
        observeCreateMemoryResponse()
        observeResponsegetMemorylist()
        observeCreateVoiceResponse()
        observeResponsegetVoicelist()
        observeResponsecreatemood()

        observeResponsecreatemessage()
        observeResponsegetMessage()

        observeResponsegetactivityresult()

        viewModel.GetMood(userid)
        observeResponsegetMood()
    }


    override fun onResume() {
        super.onResume()

        // 👇 Call your API here
        viewModel.GetAlbumlist()
    }

    private fun setupRecyclerView() {
        val datalist = listOf(
            game_response(R.drawable.game1_imag, "Ninja war", "Rating 1.3k"),
            game_response(R.drawable.game2_img, "Space Farm", "Rating 1.3k"),
            game_response(R.drawable.game3_img, "Space Farm", "Rating 1.3k"),
        )

        adapter = AdapterGame(this, datalist, { game ->
            Toast.makeText(this, "Clicked on: ${game.gameName}", Toast.LENGTH_SHORT).show()
        })

        binding.recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerview.adapter = adapter
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun setActiveTab(tab: String) {
        binding.homeFragment.visibility = if (tab == "home") View.VISIBLE else View.GONE
        binding.homeFill.visibility = if (tab == "home") View.VISIBLE else View.GONE
        binding.sosBtn.visibility = if (tab == "home") View.VISIBLE else View.GONE
        binding.home.visibility = if (tab == "home") View.GONE else View.VISIBLE

        binding.plannerFill.visibility = if (tab == "planner") View.VISIBLE else View.GONE
        binding.planner.visibility = if (tab == "planner") View.GONE else View.VISIBLE

        binding.memoryFill.visibility = if (tab == "memory") View.VISIBLE else View.GONE
        binding.memory.visibility = if (tab == "memory") View.GONE else View.VISIBLE

        if(tab == "memory" && type == "Caregiver"){

            viewModel.GetActivityResult(userid)
        }

        // Only show plannerfragment if it's Patient and planner is selected
        if (tab == "planner" && type == "Patient") {
            binding.plannerfragment.visibility = View.VISIBLE
            binding.Plannercaregiverscreen.visibility = View.GONE


//            viewModel.Getmessage(patientid)
            viewModel.Getmessage()
            viewModel.GetTasklist()

            observeResponsegetTaskList()

            if(!moodecompleted){

                Handler(Looper.getMainLooper()).postDelayed({
                    if (binding.plannerfragment.visibility == View.VISIBLE) {
                        showSuccessDialogofmood()
                    }
                }, 10000)
            }

        } else if (tab == "planner" && type == "Caregiver") {
            binding.plannerfragment.visibility = View.GONE
            binding.Plannercaregiverscreen.visibility = View.VISIBLE


//            viewModel.GetMood()
//            observeResponsegetMood()


            binding.save.setOnClickListener{
                binding.date.text.toString()
                viewModel.CreateMessage(
//                    patientid, caregiverid,
                    binding.writemessage.text.toString(),binding.date.text.toString())
            }






        } else {
            binding.plannerfragment.visibility = View.GONE
            binding.Plannercaregiverscreen.visibility = View.GONE
        }

        // Memory fragment visible only when memory tab is selected
        binding.memoryLanefragment.visibility = if(tab == "memory") View.VISIBLE else View.GONE
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupRecyclerViewdate() {
        val today = LocalDate.now()

        // Generate date list for next 15 days
        val datalist = (0..14).map { offset ->
            val date = today.plusDays(offset.toLong())
            date_response_task(
                Date = date.dayOfMonth,
                Day = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                fullDate = date // ✅ full date used here
            )
        }

        val selectedPosition = 0
        val selectedDate = datalist[selectedPosition].fullDate
        selectedDateFormatted = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        // Show message since today's date is initially selected
        binding.recyclerviewmessage.visibility = View.VISIBLE

        adapterdate = AdapterPlannerDate(this, datalist, selectedPosition) { dateItem, position ->

            if (position == datalist.lastIndex) {
                // ✅ Handle last item (assessment start)
                binding.statusline.visibility = View.GONE
                binding.taskcard.visibility = View.GONE
                binding.todayalltask.visibility = View.GONE
                binding.assesmentstart.visibility = View.VISIBLE
                binding.startassesmentbtn.visibility = View.VISIBLE

                binding.startassesmentbtn.setOnClickListener {
                    val intent = Intent(this, MatchingPairsGame::class.java)
                    intent.putExtra("type", type)
                    startActivity(intent)
                }
            } else {
                // ✅ Show task UI
                binding.statusline.visibility = View.VISIBLE
                binding.taskcard.visibility = View.VISIBLE
                binding.todayalltask.visibility = View.VISIBLE
                binding.assesmentstart.visibility = View.GONE
                binding.startassesmentbtn.visibility = View.GONE
            }

            // ✅ Update selected date and check if it's today
            selectedDateFormatted = dateItem.fullDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            binding.recyclerviewmessage.visibility =
                if (dateItem.fullDate.isEqual(today)) View.VISIBLE else View.GONE

            // ✅ Update tasks based on selected date
            val filteredItem = taskDataList.find { it.date == selectedDateFormatted }
            val combinedList = mutableListOf<TestCardItem>()
            filteredItem?.tasks?.forEach { combinedList.add(TestCardItem.Task(it)) }
            filteredItem?.activities?.forEach { combinedList.add(TestCardItem.Activity(it)) }

            setupRecyclerViewtestcard(combinedList)
        }

        // Setup RecyclerView for dates
        binding.recyclerviewdate.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewdate.adapter = adapterdate

        // ✅ Load initial tasks for today's date
        val filteredItem = taskDataList.find { it.date == selectedDateFormatted }
        val combinedList = mutableListOf<TestCardItem>()
        filteredItem?.tasks?.forEach { combinedList.add(TestCardItem.Task(it)) }
        filteredItem?.activities?.forEach { combinedList.add(TestCardItem.Activity(it)) }
        setupRecyclerViewtestcard(combinedList)
    }


    private fun setupRecyclerViewplannermessage() {
        adaptermessage = AdapterPlannerMessage(this, datalistplannermessage)

        binding.recyclerviewmessage.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerviewmessage.adapter = adaptermessage
    }


    private fun setupRecyclerViewline() {
        val datalist = listOf(
            line_respone(R.drawable.unselected_circle, R.drawable.unselected_line),
            line_respone(R.drawable.unselected_circle, R.drawable.unselected_line),
            line_respone(R.drawable.unselected_circle, R.drawable.unselected_line),
            line_respone(R.drawable.unselected_circle, R.drawable.unselected_line),
            line_respone(R.drawable.unselected_circle, R.drawable.unselected_line),
            line_respone(R.drawable.unselected_circle, R.drawable.unselected_line),
            line_respone(R.drawable.unselected_circle, R.drawable.unselected_line),
        )

        adapterstatusline = Adapterline(this, datalist)

        binding.statusline.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.statusline.adapter = adapterstatusline
    }




    private fun setupRecyclerViewtestcard(list: List<TestCardItem>) {
        val sortedList = list.sortedBy { it is TestCardItem.Activity }

        adaptertestcard = AdapterTestcard(this, sortedList) { item ->
            when (item) {



                is TestCardItem.Task -> {
                    val title = item.task.title
                    val userId = patientid
                    val taskId = item.task._id // or replace with correct ID field
                    val url = "https://client-demo.in/games/firefly/?userId=$userId&taskId=$taskId"

                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(url)
                    }

                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(this, "No browser found to open the link", Toast.LENGTH_SHORT).show()
                    }
                }


                is TestCardItem.Activity -> {
                    val title = item.activity.title
                    Toast.makeText(this, "Clicked on Activity: $title", Toast.LENGTH_SHORT).show()

                    if(item.activity.key == "image"){

                        val intent = Intent(this, ImageTask::class.java)
                        intent.putExtra("title", item.activity.title)
                        intent.putExtra("activityid", item.activity._id)
                        startActivity(intent)

                    }else{

                        val intent = Intent(this, VoiceTask::class.java)
                        intent.putExtra("title", item.activity.title)
                        intent.putExtra("activityid", item.activity._id)
                        startActivity(intent)
                    }

                }


            }



        }

        binding.taskcard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.taskcard.adapter = adaptertestcard
    }



    private fun setupRecyclerViewvoicenote() {


        adaptervoice = AdapterVoice(this, datalistforVoicelist)

        binding.recyclerviewvoice.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewvoice.adapter = adaptervoice
    }



    private fun setupRecyclerViewmemory() {


        adapterMemory = AdapterMemory(this, datalistformemorylist)

        binding.recyclerviewmemory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewmemory.adapter = adapterMemory
    }




    private fun setupRecyclerViewActivityImagetask() {


        adapterimagetask = AdapterImageTask(this, datalistforimagetask)

        binding.imagetask.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.imagetask.adapter = adapterimagetask
    }



    private fun setupRecyclerViewalbum() {




        val gridLayoutManager = GridLayoutManager(this, 2)
        binding.recyclerviewalbum.layoutManager = gridLayoutManager


        val spacingInPixels = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
        binding.recyclerviewalbum.addItemDecoration(GridSpacing(2, spacingInPixels, true))
        adapterAlbum = AdapterAlbum(this, datalistforalbum) { doctor ->
            Toast.makeText(this, "Clicked on: ${doctor.title}", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, Album_view::class.java)
            intent.putExtra("id",doctor._id)
            startActivity(intent)

        }
        binding.recyclerviewalbum.adapter = adapterAlbum

    }



    private fun setupRecyclerViewaddedimage(recyclerView: RecyclerView) {

        adapterAddedimage = AdapterAddedimage(this, selectedImages)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapterAddedimage
    }








    private fun showSuccessDialogofmood() {

        val dialogView = layoutInflater.inflate(R.layout.mood_dialouge, null)



        val builder = AlertDialog.Builder(this,R.style.CustomAlertDialogTheme)
        builder.setView(dialogView)


        // Show the dialog
        dialog = builder.create()



//        val continueButton = dialogView.findViewById<TextView>(R.id.delete)
        val cancel = dialogView.findViewById<ImageButton>(R.id.cross)
        val submit = dialogView.findViewById<TextView>(R.id.save)
        val description = dialogView.findViewById<EditText>(R.id.writemessage)


        val emojiMap = mapOf(
            dialogView.findViewById<ImageButton>(R.id.sademoji) to "Sad",
            dialogView.findViewById<ImageButton>(R.id.verygoodemoji) to "Very Good",
            dialogView.findViewById<ImageButton>(R.id.goodemoji) to "Good",
            dialogView.findViewById<ImageButton>(R.id.angryemoji) to "Angry"
        )

        var selectedEmojiText: String? = null

        // 2. Set click listeners and handle selection
        emojiMap.forEach { (button, text) ->
            button.setOnClickListener {
                // Deselect all buttons
                emojiMap.keys.forEach { it.isSelected = false }

                // Select the clicked one
                button.isSelected = true

                // Save selected text to be sent to API
                selectedEmojiText = text
            }
        }



        submit.setOnClickListener {
            selectedEmojiText?.let { emojiText ->
                // Call your ViewModel or Retrofit function and pass emojiText
                viewModel.CreateMood(emojiText, description.text.toString())
            } ?: run {
                Toast.makeText(this, "Please select an emoji", Toast.LENGTH_SHORT).show()
            }


        }

        cancel.setOnClickListener{
            Log.d("canceled", "clicked")
            dialog.dismiss()


        }

        dialog.show()
    }




    private fun showSuccessDialogofstreak() {

        val dialogView = layoutInflater.inflate(R.layout.steak_dialouge, null)



        val builder = AlertDialog.Builder(this,R.style.CustomAlertDialogTheme)
        builder.setView(dialogView)

//        // Find and set up the Continue button

        // Show the dialog
        dialog = builder.create()



        val continueButton = dialogView.findViewById<TextView>(R.id.save)



        continueButton.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun addvoicenote() {
        dialogbottom = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.add_voice_drawer_layout, null)
        dialogbottom.setContentView(view)

        val bottomSheet = dialogbottom.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        val micButton = view.findViewById<ImageButton>(R.id.mic)
        val reset = view.findViewById<ImageButton>(R.id.retry)
        val voicesave = view.findViewById<ImageButton>(R.id.voicesave)

        waveformView = view.findViewById(R.id.waveformView)
        val time = view.findViewById<TextView>(R.id.time)

        view.findViewById<TextView>(R.id.cross).setOnClickListener {
            dialogbottom.dismiss()

            // ❗ Delete the previously recorded file
            recordedFile?.let {
                if (it.exists()) {
                    it.delete()
                }
            }
            recordedFile = null // clear reference
        }
        micButton.setImageResource(R.drawable.mic_icon)

        micButton.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this@HomePage, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 101)
                Toast.makeText(this, "Please allow microphone permission", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isRecording) {
                micButton.setImageResource(R.drawable.pause_icon)
                startRecording()
                startTime = System.currentTimeMillis()
                startTimer(time)
            } else {
                stopRecording()
                pauseTimer()
                micButton.setImageResource(R.drawable.play_icon)
            }

            Log.d("mic click", "$isRecording")
        }



        reset.setOnClickListener {
            if (isRecording) {
                try {
                    mediaRecorder?.apply {
                        stop()
                        release()
                    }
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
                isRecording = false
            }

            handler.removeCallbacks(updateVisualizer)
            mediaRecorder = null
            waveformView.clear()
            resetTimer()
            time.text = "00:00:00"



            // ❗ Delete the previously recorded file
            recordedFile?.let {
                if (it.exists()) {
                    it.delete()
                }
            }
            recordedFile = null // clear reference
            micButton.setImageResource(R.drawable.mic_icon)
        }


        voicesave.setOnClickListener{
            if(isRecording){
                stopRecording()
                pauseTimer()
                micButton.setImageResource(R.drawable.play_icon)
            }


            recordedFile?.let { it1 ->
                viewModel.CreateVoice(userType =type,
//                    userId = if (type == "Patient") patientid else caregiverid,
                    music = it1
                )
            }
        }


        dialogbottom.show()
    }




    private fun startRecording() {
        Log.d("first", "se")
        val outputFile = "${externalCacheDir?.absolutePath}/recorded_${System.currentTimeMillis()}.3gp"
        Log.d("second", "se")
        recordedFile = File(outputFile)
        Log.d("third", "se")

        mediaRecorder = MediaRecorder().apply {
            Log.d("fourth", "se")
            setAudioSource(MediaRecorder.AudioSource.MIC)
            Log.d("fifth", "se")
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            Log.d("sixth", "se")
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            Log.d("seventh", "se")
            setOutputFile(outputFile)
            Log.d("eighth", "se")
            prepare()
            Log.d("ninth", "se")
            start()
            Log.d("ten", "se")
        }

        isRecording = true
        Log.d("eleven", "se")
        handler.post(updateVisualizer)
        Log.d("twelve", "se")
    }

    private fun stopRecording() {
        isRecording = false
        handler.removeCallbacks(updateVisualizer)
        mediaRecorder?.apply {
            stop()
        }

    }



    private fun startTimer(timeTextView: TextView) {
        startTime = System.currentTimeMillis() - elapsedTime // Adjust start time

        updateTimeRunnable = object : Runnable {
            override fun run() {
                val currentTime = System.currentTimeMillis()
                val totalElapsed = currentTime - startTime

                elapsedTime = totalElapsed // Keep updating elapsed time

                val minutes = (totalElapsed / 1000) / 60
                val seconds = (totalElapsed / 1000) % 60
                val milliseconds = (totalElapsed % 1000) / 10

                val formattedTime = String.format("%02d:%02d:%02d", minutes, seconds, milliseconds)
                timeTextView.text = formattedTime

                timeHandler.postDelayed(this, 50)
            }
        }
        timeHandler.post(updateTimeRunnable)
    }


    private fun pauseTimer() {
        timeHandler.removeCallbacks(updateTimeRunnable)
    }


    private fun resetTimer() {
        pauseTimer()
        elapsedTime = 0L

    }



    private fun addmemory() {
        dialogbottom = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.add_memory_drawer_layout, null)
        dialogbottom.setContentView(view)

        val bottomSheet = dialogbottom.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        selectedImageButton = view.findViewById(R.id.image) // ✅ store reference
        Texthint = view.findViewById(R.id.time_hint)


        var selectedColor: String? = null

        val colorMap = mapOf(
            R.id.voilet to Pair(R.color.DEB5F7, "violet"),
            R.id.green to Pair(R.color.B7F8B0, "green"),
            R.id.red to Pair(R.color.FD7373, "red"),
            R.id.orange to Pair(R.color.FFB561, "orange")
        )

        colorMap.forEach { (buttonId, colorData) ->
            val (colorRes, colorName) = colorData
            view.findViewById<ImageButton>(buttonId)?.setOnClickListener {
                // Set button background color
                it.setBackgroundColor(ContextCompat.getColor(this, colorRes))

                // Update selected color name
                selectedColor = colorName
            }
        }


        val creatememory = view.findViewById<TextView>(R.id.creatememory)

        creatememory.setOnClickListener {
            val title = view.findViewById<EditText>(R.id.title).text.toString()
            val color = selectedColor  // fallback color if none selected


            val thumbnailFile = selectedThumbnailUri?.let { uriToFile(this, it) }
            if (thumbnailFile != null) {
                if (color != null) {
                    viewModel.CreateMemory(title = title, thumbnail = thumbnailFile, color = color)
                }
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }

        Texthint.visibility = View.VISIBLE

        selectedImageButton.setOnClickListener {
            openGallerysingle()
        }


        view.findViewById<TextView>(R.id.cross).setOnClickListener {
            dialogbottom.dismiss()
        }


        dialogbottom.show()
    }




    private fun addAlbum() {
        dialogbottom = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.add_album_drawer_layout, null)
        dialogbottom.setContentView(view)

        val bottomSheet = dialogbottom.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED


        // ✅ Setup RecyclerView inside bottom sheet
        val recyclerView = view.findViewById<RecyclerView>(R.id.addedimage)
        setupRecyclerViewaddedimage(recyclerView)

        view.findViewById<TextView>(R.id.cross).setOnClickListener {

            selectedImages.clear()
            adapterAddedimage.notifyDataSetChanged()

            dialogbottom.dismiss()
        }

        view.findViewById<ImageButton>(R.id.opengalarry).setOnClickListener{
            openGallery()
        }

        val create = view.findViewById<TextView>(R.id.create)

        create.setOnClickListener{

            val title = view.findViewById<EditText>(R.id.title).text.toString()
            val description = view.findViewById<EditText>(R.id.description).text.toString()

            // ✅ Convert your selected URIs to File list
            val imageFiles = selectedImages.mapNotNull { uri -> uriToFile(this, uri) }


            viewModel.CreateAlbum(title, description, imageFiles)
        }


        dialogbottom.show()
    }



    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_REQUEST)
    }


    private fun openGallerysingle() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        // Remove multiple selection
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_SINGLE)
    }

    // Add this function to get file name from Uri
    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
        return name
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            // If multiple images are selected
            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    selectedImages.add(uri)
                    adapterAddedimage.notifyItemInserted(selectedImages.size - 1)

                    val fileName = getFileName(uri)
                    Log.d("MultiFileName", "$fileName")
                }
            }

            // If a single image is selected


            data?.data?.let { uri ->
                selectedImages.add(uri)
                adapterAddedimage.notifyItemInserted(selectedImages.size - 1)

                val fileName = getFileName(uri)
                Log.d("SingleFileName", "$fileName")

            }

            adapterAddedimage.notifyDataSetChanged() // ✅ force UI update


            // ✅ Force layout update
            dialogbottom.findViewById<RecyclerView>(R.id.addedimage)?.apply {
                post {
                    requestLayout()
                    invalidate()
                }
            }

            // ✅ Re-expand sheet to adjust new height
            dialogbottom.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let {
                BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
            }

        }
        if (requestCode == PICK_IMAGE_REQUEST_SINGLE) {
            data?.data?.let { uri ->
                selectedThumbnailUri = uri
                selectedImageButton.setImageURI(uri)
                Texthint.visibility = View.GONE
                Log.d("SingleSelectedImage", "URI: $uri")
                Log.d("SingleSelectedImage", "FileName: ${getFileName(uri)}")

                // Optionally convert to File
                val file = uriToFile(this, uri)
                Log.d("SingleSelectedImage", "Temp File Path: ${file?.absolutePath}")
            }
        }
    }


    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { output -> inputStream.copyTo(output) }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    private fun observeCreateAlbumResponse() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createalbum.observe(this@HomePage) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@HomePage)
                            Log.d("AlbumCreation", "⏳ Creating album...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            val data = response.data
                            if (data?.success == true) {
                                Toast.makeText(this@HomePage, "Album Created Successfully!", Toast.LENGTH_SHORT).show()
                                dialogbottom.dismiss()

                                selectedImages.clear()
                                adapterAddedimage.notifyDataSetChanged()

                                // Refresh album list
                                viewModel.GetAlbumlist()
                            } else {
                                androidExtension.alertBox(data?.msg ?: "Album creation failed", this@HomePage)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("AlbumCreation", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@HomePage)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@HomePage)
                        }
                    }
                }
            }
        }
    }


    private fun observeResponsegetAlbumlist() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.GetAlbumlist.observe(this@HomePage) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@HomePage)
                            Log.d("AlbumList", "⏳ Loading album list...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            val data = response.data

                            if (data?.success == true) {
                                datalistforalbum.clear()
                                datalistforalbum.addAll(data.data)
                                adapterAlbum.notifyDataSetChanged()

                                binding.albumempty.visibility =
                                    if (datalistforalbum.isEmpty()) View.VISIBLE else View.GONE
                            } else {
                                androidExtension.alertBox(data?.msg ?: "Failed to load albums", this@HomePage)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("AlbumList", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@HomePage)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@HomePage)
                        }
                    }
                }
            }
        }
    }



    private fun observeCreateMemoryResponse() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.creatememory.observe(this@HomePage) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@HomePage)
                            Log.d("CreateMemory", "⏳ Creating memory...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            val data = response.data
                            if (data?.success == true) {
                                Toast.makeText(this@HomePage, "Memory Created Successfully!", Toast.LENGTH_SHORT).show()
                                dialogbottom.dismiss()

                                viewModel.GetMemorylist()
                            } else {
                                androidExtension.alertBox(data?.msg ?: "Failed to create memory", this@HomePage)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("CreateMemory", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@HomePage)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@HomePage)
                        }
                    }
                }

                viewModel.error.observe(this@HomePage) { errorMsg ->
                    Progresss.stop()
                    Toast.makeText(this@HomePage, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun observeResponsegetMemorylist() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.GetMemorylist.observe(this@HomePage) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@HomePage)
                            Log.d("MemoryList", "⏳ Loading memory list...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            val data = response.data
                            if (data?.success == true) {
                                datalistformemorylist.clear()
                                datalistformemorylist.addAll(data.data)
                                adapterMemory.notifyDataSetChanged()

                                binding.memoryempty.visibility = if (datalistformemorylist.isEmpty()) View.VISIBLE else View.GONE
                            } else {
                                androidExtension.alertBox(response.message ?: "Failed to fetch memory list", this@HomePage)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("MemoryList", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@HomePage)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@HomePage)
                        }
                    }
                }
            }
        }
    }


    private fun observeCreateVoiceResponse() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createvoice.observe(this@HomePage) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@HomePage)
                            Log.d("VoiceCreate", "⏳ Creating voice...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            val data = response.data
                            if (data?.success == true) {
                                Toast.makeText(this@HomePage, "Voice created successfully!", Toast.LENGTH_SHORT).show()
                                dialogbottom.dismiss()

                                // Optionally refresh voice list
                                viewModel.GetVoicelist()
                            } else {
                                androidExtension.alertBox(data?.msg ?: "Failed to create voice", this@HomePage)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("VoiceCreate", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@HomePage)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@HomePage)
                        }
                    }
                }

                viewModel.error.observe(this@HomePage) { errorMsg ->
                    Progresss.stop()
                    Toast.makeText(this@HomePage, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun observeResponsegetVoicelist() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.GetVoicelist.observe(this@HomePage) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@HomePage)
                            Log.d("VoiceList", "⏳ Fetching voice list...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            val data = response.data

                            if (data?.success == true) {
                                datalistforVoicelist.clear()
                                datalistforVoicelist.addAll(data.data)  // Newest first
                                adaptervoice.notifyDataSetChanged()

                                binding.voiceempty.visibility =
                                    if (datalistforVoicelist.isEmpty()) View.VISIBLE else View.GONE
                            } else {
                                androidExtension.alertBox(data?.msg ?: "Failed to fetch voice list", this@HomePage)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("VoiceList", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@HomePage)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@HomePage)
                        }
                    }
                }


            }
        }
    }


    private fun observeResponsecreatemood() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createmood.observe(this@HomePage) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@HomePage)
                            Log.d("CreateMood", "⏳ Creating mood entry...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            if (response.data?.success == true) {
                                dialog.dismiss()
//                                showSuccessDialogofstreak()
                            } else {
                                androidExtension.alertBox(response.data?.msg ?: "Mood creation failed", this@HomePage)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("CreateMood", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@HomePage)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@HomePage)
                        }
                    }
                }


            }
        }
    }


    private fun observeResponsegetMood() {
        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.GetMood.observe(this@HomePage) { response ->

                    if(response.success){

                        moodecompleted = response.data.isComplete
                        val emojiDrawableMap = mapOf(
                            "Sad" to R.drawable.sad_emoji_color,
                            "Very Good" to R.drawable.verygood_emoji_color,
                            "Good" to R.drawable.happy_emoji_color,
                            "Angry" to R.drawable.angry_emoji_color
                        )

                        val moodTitle = response.data.title

                        // Set emoji image based on title
                        emojiDrawableMap[moodTitle]?.let { emojiRes ->
                            binding.emoji.setImageResource(emojiRes)
                        }


                        binding.moodtype.setText(response.data.title)
                        binding.message.setText(response.data.description)


                    }else{

                    }

                }
            }
        }
    }


    private fun observeResponsecreatemessage() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createmessage.observe(this@HomePage) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@HomePage)
                            Log.d("CreateMessage", "⏳ Sending message...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let {
                                binding.writemessage.text?.clear()
                                Toast.makeText(this@HomePage, "Message sent successfully", Toast.LENGTH_SHORT).show()
                            } ?: run {
                                androidExtension.alertBox("Message sent, but no response data", this@HomePage)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("CreateMessage", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Failed to send message", this@HomePage)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@HomePage)
                        }
                    }
                }

                viewModel.error.observe(this@HomePage) { errorMsg ->
                    Progresss.stop()
                    Toast.makeText(this@HomePage, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun observeResponsegetMessage() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getmessage.observe(this@HomePage) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@HomePage)
                            Log.d("GetMessage", "⏳ Loading messages...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                datalistplannermessage.clear()
                                datalistplannermessage.addAll(data.messages)
                                adaptermessage.notifyDataSetChanged()
                            } ?: run {
                                androidExtension.alertBox("No messages found", this@HomePage)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("GetMessage", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Failed to fetch messages", this@HomePage)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@HomePage)
                        }
                    }
                }

                viewModel.error.observe(this@HomePage) { errorMsg ->
                    Progresss.stop()
                    Toast.makeText(this@HomePage, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeResponsegetTaskList() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gettasklist.observe(this@HomePage) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@HomePage)
                            Log.d("TaskList", "⏳ Loading tasks...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                taskDataList = data.data // Store globally
                                setupRecyclerViewdate()
                                Log.d("TaskList", "✅ Tasks loaded: $data")
                            } ?: run {
                                androidExtension.alertBox("No tasks found", this@HomePage)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("TaskList", "❌ Error: ${response.message}")
//                            androidExtension.alertBox(response.data?.msg ?: "Failed to load tasks", this@HomePage)


                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@HomePage)
                        }
                    }
                }


            }
        }
    }


    private fun observeResponsegetactivityresult() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getactivityresult.observe(this@HomePage) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@HomePage)
                            Log.d("ActivityResult", "⏳ Fetching activity result...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            val resultData = response.data
                            if (resultData != null && resultData.data.isNotEmpty()) {
                                Log.d("ActivityResult", "✅ Success: $resultData")
                                binding.textnoactivity.visibility = View.GONE

                                val firstItem = resultData.data[0]

                                binding.activitytitle.setText(firstItem.title.toString())

                                if (!firstItem.thumbnail.isNullOrEmpty()) {
                                    binding.imagetask.visibility = View.VISIBLE
                                    binding.playActivity.visibility = View.GONE
                                    binding.wave.visibility = View.GONE

                                    datalistforimagetask.clear()
                                    resultData.data.forEach { task ->
                                        datalistforimagetask.addAll(task.thumbnail)
                                    }

                                    adapterimagetask.notifyDataSetChanged()
                                } else if (!firstItem.music.isNullOrEmpty()) {
                                    binding.imagetask.visibility = View.GONE
                                    binding.playActivity.visibility = View.VISIBLE
                                    binding.wave.visibility = View.VISIBLE
                                    setupMusicButton(firstItem.music)
                                } else {
                                    binding.imagetask.visibility = View.GONE
                                    binding.playActivity.visibility = View.GONE
                                    binding.wave.visibility = View.GONE
                                    binding.textnoactivity.visibility = View.VISIBLE
                                    Log.d("ActivityResult", "No image or music found.")
                                }

                            } else {
                                // Handle empty list
                                binding.textnoactivity.visibility = View.VISIBLE
                                binding.imagetask.visibility = View.GONE
                                binding.playActivity.visibility = View.GONE
                                binding.wave.visibility = View.GONE
                                Log.d("ActivityResult", "Empty or null activity data.")
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("ActivityResult", "❌ Error: ${response.message}")
                            androidExtension.alertBox(
                                response.message ?: "Failed to fetch activity result",
                                this@HomePage
                            )
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@HomePage)
                        }
                    }
                }

                viewModel.error.observe(this@HomePage) { errorMsg ->
                    Progresss.stop()
                    Toast.makeText(this@HomePage, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setupMusicButton(musicPath: String) {
        val fullMusicUrl = "http://172.104.206.4:5000/uploads/$musicPath"

        binding.playActivity.setImageResource(R.drawable.play_btn_for_activity)
        binding.wave.visibility = View.VISIBLE
        binding.wave.pauseVisualizer() // show idle wave initially

        binding.playActivity.setOnClickListener {
            if (isPlayingVoice) {
                mediaPlayer?.pause()
                binding.playActivity.setImageResource(R.drawable.play_btn_for_activity)
                isPlayingVoice = false
                visualizer?.enabled = false
                binding.wave.pauseVisualizer() // go back to idle animation
            } else {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(fullMusicUrl)
                        prepareAsync()
                        setOnPreparedListener {
                            start()
                            isPlayingVoice = true
                            binding.playActivity.setImageResource(R.drawable.pause_btn_for_activity)
                            binding.wave.resumeVisualizer()

                            val audioSessionId = audioSessionId
                            if (audioSessionId != -1) {
                                visualizer = Visualizer(audioSessionId).apply {
                                    captureSize = Visualizer.getCaptureSizeRange()[1]
                                    setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                                        override fun onWaveFormDataCapture(
                                            visualizer: Visualizer?, waveform: ByteArray?, samplingRate: Int
                                        ) {
                                            waveform?.let { binding.wave.updateVisualizer(it) }
                                        }

                                        override fun onFftDataCapture(
                                            visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int
                                        ) {
                                            // No-op
                                        }
                                    }, Visualizer.getMaxCaptureRate() / 2, true, false)

                                    enabled = true
                                }
                            }
                        }

                        setOnCompletionListener {
                            binding.playActivity.setImageResource(R.drawable.play_btn_for_activity)
                            isPlayingVoice = false
                            visualizer?.enabled = false
                            binding.wave.pauseVisualizer()
                            seekTo(0)
                        }
                    }
                } else {
                    mediaPlayer?.start()
                    isPlayingVoice = true
                    binding.playActivity.setImageResource(R.drawable.pause_btn_for_activity)
                    visualizer?.enabled = true
                    binding.wave.resumeVisualizer()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}