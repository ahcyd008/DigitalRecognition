package com.clientai.recog.digital.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.clientai.recog.digital.R
import com.clientai.recog.digital.UIUtils
import com.clientai.recog.digital.databinding.FragmentDigitalRecognitionBinding
import com.clientai.recog.digital.tflite.DigitalClassifier
import com.clientai.recog.digital.ui.widget.DigitalHandWritingInputView

class DigitalRecognitionFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDigitalRecognitionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var resultArray: ArrayList<Button>? = null
    private var digitClassifier: DigitalClassifier? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDigitalRecognitionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        init()
        return root
    }

    private fun init() {
        binding.digitalInputView.setStrokeWidth(50.0f)
        binding.digitalInputView.setColor(Color.WHITE)
        binding.digitalInputView.setBackgroundColor(Color.BLACK)
        binding.digitalInputView.setActionUpListener(object : DigitalHandWritingInputView.IActionUpListener{
            override fun onActionUp() {
                classifyHandWritingInput()
            }
        })

        // Setup digit classifier
        context?.let {
            val classifier = DigitalClassifier(it)
            classifier.initialize().addOnFailureListener { e -> Log.e(TAG, "Error to setting up digit classifier.", e) }
            digitClassifier = classifier
        }

        binding.clean.setOnClickListener(this)

        val resultArray = arrayListOf(binding.resultTop1, binding.resultTop2, binding.resultTop3, binding.resultTop4)
        this.resultArray = resultArray
        binding.recognitionResult.visibility = View.INVISIBLE
        for (i in 0 until resultArray.size) {
            resultArray[i].visibility = View.INVISIBLE
        }
    }

    private fun classifyHandWritingInput() {
        val classifier = digitClassifier ?: return
        if (classifier.isInitialized) {
            val begin = System.currentTimeMillis()
            val bitmap = binding.digitalInputView.getBitmap()
            Log.d(TAG, "get bitmap cost:${System.currentTimeMillis()-begin}")
            classifier.classifyAsync(bitmap)
                .addOnSuccessListener { result ->
                    binding.recognitionResult.post {
                        val resultArray = resultArray ?: return@post
                        for (i in 0 until resultArray.size) {
                            var label = ""
                            var score = 0f
                            if (i < result.size) {
                                label = result[i].label
                                score = result[i].score
                            }
                            // 展示 top1 和 其他 score > 0.01 的候选数字供用户选择
                            binding.recognitionResult.visibility = View.VISIBLE
                            if (i == 0 || score >= 0.01f) {
                                resultArray[i].visibility = View.VISIBLE
                                resultArray[i].text = "${label}\n${UIUtils.formatDisplayFloat(score)}"
                            } else {
                                resultArray[i].visibility = View.INVISIBLE
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error classifying drawing.", e)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        digitClassifier?.close()
    }

    override fun onClick(view: View?) {
        var id = view?.id ?: return
        when(id) {
            R.id.clean -> {
                binding.digitalInputView.clearCanvas()
                val resultArray = resultArray ?: return
                for (i in 0 until resultArray.size) {
                    resultArray[i].visibility = View.INVISIBLE
                }
                binding.recognitionResult.visibility = View.INVISIBLE
            }
        }
    }

    companion object {
        private const val TAG = "ClientAI#RecogFragment"
    }
}