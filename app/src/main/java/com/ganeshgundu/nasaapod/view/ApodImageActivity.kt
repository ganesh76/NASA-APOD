package com.ganeshgundu.nasaapod.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ganeshgundu.nasaapod.R
import com.ganeshgundu.nasaapod.core.ApodImageApplication
import com.ganeshgundu.nasaapod.databinding.ActivityApodimageBinding
import com.ganeshgundu.nasaapod.db.ApodImageData
import com.ganeshgundu.nasaapod.repository.ApodImageRepository
import com.ganeshgundu.nasaapod.viewmodel.ApodImageViewModel
import androidx.activity.enableEdgeToEdge
import androidx.core.view.updatePaddingRelative


class ApodImageActivity : AppCompatActivity() {
    private val viewModel: ApodImageViewModel by viewModels {
        ApodImageViewModel.ApodImageViewModelFactory((application as ApodImageApplication).repository)
    }
    private lateinit var context: Context
    private lateinit var binding: ActivityApodimageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApodimageBinding.inflate(layoutInflater)
        // For Android 14 and lower; on 15+ it's enforced but this keeps behavior consistent.
        enableEdgeToEdge()
        setContentView(binding.root)
        val rootScroll = findViewById<ScrollView>(R.id.rootScroll)

        // Pad your content so it stays out from under status/nav bars.
        ViewCompat.setOnApplyWindowInsetsListener(rootScroll) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePaddingRelative(top = bars.top, bottom = bars.bottom)
            insets
        }
        context = this
        viewModel.getApodData()
        viewModel.responseData.observe(this, {
            when (it.responseStatus) {
                ApodImageRepository.ResponseStatus.LAST_AVAILABLE_DATA -> {
                    showAlert(context.getString(R.string.last_available_data_error))
                    populateUi(it.response)
                }
                ApodImageRepository.ResponseStatus.SUCCESS -> populateUi(it.response)
                ApodImageRepository.ResponseStatus.OFFLINE_DATA_NA -> {
                    binding.apodTitleTextView.text =
                        context.getString(R.string.no_available_data_error_title)
                    binding.apodExpTextView.text =
                        context.getString(R.string.no_available_data_error_msg)
                    binding.apodImageView.adjustViewBounds = false
                    binding.apodImageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_connection_error
                        )
                    )
                }
                ApodImageRepository.ResponseStatus.API_ERROR -> {
                    binding.apodTitleTextView.text = context.getString(R.string.api_error_title)
                    binding.apodExpTextView.text = context.getString(R.string.api_error_msg)
                    binding.apodImageView.adjustViewBounds = false
                    binding.apodImageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_broken_image
                        )
                    )
                }
            }
        })

        viewModel.showLoading.observe(this, {
            if (it) {
                binding.uiProgressBar.visibility = View.VISIBLE
            } else {
                binding.uiProgressBar.visibility = View.GONE
            }
        })
    }

    private fun populateUi(apodImageData: ApodImageData) {
        binding.apodTitleTextView.text = apodImageData.title
        binding.apodExpTextView.text = apodImageData.explanation
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(context)
            .load(apodImageData.imageUrl)
            .dontAnimate()
            .error(R.drawable.ic_broken_image)
            .placeholder(R.drawable.loading_animation)
            .apply(requestOptions)
            .into(binding.apodImageView)
        binding.apodImageView.adjustViewBounds = true
    }

    private fun showAlert(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(context.getString(R.string.app_name))
            .setMessage(msg)
            .setCancelable(true)
            .setPositiveButton(context.getString(R.string.alert_ok)) { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}