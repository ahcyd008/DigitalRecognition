package com.clientai.recog.digital.ui.track

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.clientai.recog.digital.BitmapUtils
import com.clientai.recog.digital.PermissionUtils
import com.clientai.recog.digital.R
import com.clientai.recog.digital.databinding.FragmentDataTrackBinding
import java.util.*
import kotlin.math.abs

class DataTrackFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDataTrackBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val random = Random()

    init {
        random.setSeed(System.currentTimeMillis())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataTrackBinding.inflate(inflater, container, false)
        val root: View = binding.root
        init()
        return root
    }

    private fun init() {
        binding.digitalInputView.setStrokeWidth(50.0f)
        binding.digitalInputView.setColor(Color.WHITE)
        binding.digitalInputView.setBackgroundColor(Color.BLACK)
        binding.next.setOnClickListener(this)
        binding.skip.setOnClickListener(this)
        binding.clean.setOnClickListener(this)

        randomNextInput()
    }

    private fun randomNextInput() {
        val randomInt = abs(random.nextInt())
        val index = randomInt % digital.size
        Log.d(TAG, "randomNextInput index: $index")
        if (index >=0 && index < digital.size) {
            binding.digital.text = digital[index]
            binding.digitalInputView.clearCanvas()
        }
    }

    private fun saveCurrentInput(): Boolean {
        val activity = activity ?: return false
        val label = binding.digital.text.toString()
        if (TextUtils.isEmpty(label) || !binding.digitalInputView.hasInput()) {
            Toast.makeText(context, "还没有输入", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!PermissionUtils.checkAndRequestPermission(activity)) {
            return false
        }
        val bitmap = binding.digitalInputView.getBitmap()
        if (!BitmapUtils.saveBitmap(bitmap, label, activity)) {
            Toast.makeText(context, "数据保存失败", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onClick(view: View?) {
        var id = view?.id ?: return
        when(id) {
            R.id.clean -> {
                binding.digitalInputView.clearCanvas()
            }
            R.id.skip -> {
                randomNextInput()
            }
            R.id.next -> {
                if (saveCurrentInput()) {
                    randomNextInput()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ClientAI#TrackFragment"
        private val digital = arrayListOf<String>("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    }
}