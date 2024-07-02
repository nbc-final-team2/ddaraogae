package com.nbcfinalteam2.ddaraogae.presentation.ui.history

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.DialogWalkHistoryMapBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.TextConverter.distanceDoubleToString
import com.nbcfinalteam2.ddaraogae.presentation.util.TextConverter.timeIntToStringForHistory
import java.io.File

class WalkHistoryMapDialog : DialogFragment() {
    private var _binding: DialogWalkHistoryMapBinding? = null
    private val binding get() = _binding!!
    private var walkInfo: WalkingInfo? = null
    private var walkHistoryMap: String? = null
    private var timeTaken: String? = null
    private var distance: String? = null
    private var dogName: String? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("walkHistoryMap", walkHistoryMap)
        outState.putString("timeTaken", timeTaken)
        outState.putString("distance", distance)
        outState.putString("dogName", dogName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogWalkHistoryMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let {
            walkHistoryMap = it.getString("walkHistoryMap")
            timeTaken = it.getString("timeTaken")
            distance = it.getString("distance")
            dogName = it.getString("dogName")
        }

        setupView()
        setupListener()
    }

    private fun setupView() {
        Glide.with(this)
            .load(walkHistoryMap)
            .transform(WalkingImageTransformation(timeTaken ?: "", distance ?: "", dogName ?: ""))
            .error(R.drawable.img_map_default)
            .fallback(R.drawable.img_map_default)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: Target<Drawable>, isFirstResource: Boolean
                ): Boolean {
                    binding.ivShareBtn.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable, model: Any, target: Target<Drawable>?,
                    dataSource: DataSource, isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            })
            .into(binding.ivMap)
    }

    private fun setupListener() {
        binding.ivShareBtn.setOnClickListener {
            shareImage()
        }
    }

    private fun shareImage() {
        val sharedImagesDir = File(requireContext().cacheDir, "sharedImages")
        sharedImagesDir.mkdirs()

        val imageFile = File(sharedImagesDir, "history_image.jpg")


        Glide.with(this)
            .asBitmap()
            .load(walkHistoryMap)
            .transform(WalkingImageTransformation(timeTaken ?: "", distance ?: "", dogName ?: ""))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    try {
                        imageFile.outputStream().use {
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, it)
                        }

                        val imageUri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.nbcfinalteam2.ddaraogae.fileprovider",
                            imageFile
                        )

                        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                            type = "image/jpeg"
                            putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(imageUri))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        startActivity(Intent.createChooser(intent, getString(R.string.home_history_dialog_map_chooser)))
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), R.string.home_history_dialog_map_fail_message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    //Noting to do
                }
            })
    }

    fun setInfo(walk: WalkingInfo, name: String) {
        walkInfo = walk
        walkHistoryMap = walk.walkingImage
        timeTaken = timeIntToStringForHistory(walkInfo?.timeTaken ?: 0)
        distance = distanceDoubleToString(walkInfo?.distance ?: 0.0)
        dogName = name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}