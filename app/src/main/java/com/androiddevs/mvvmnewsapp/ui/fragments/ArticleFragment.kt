package com.androiddevs.mvvmnewsapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*
import java.util.*

class ArticleFragment : Fragment(R.layout.fragment_article), TextToSpeech.OnInitListener{

    lateinit var viewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this.context,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this.context,R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this.context,R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this.context,R.anim.to_bottom_anim) }

    private var clicked = false
    private var tts: TextToSpeech?= null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as NewsActivity).viewModel



        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        fab.setOnClickListener {
            onAddButtonClicked()
        }
        save_news.setOnClickListener{
            viewModel.saveArticle(article)
            Snackbar.make(view,"Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }

        share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type= "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT," Hey, Checkout this Breaking news ${article.url}")
            val chooser= Intent.createChooser( intent, "Share this News using .. ")
            startActivity(chooser)
        }

        textToSpeech.isEnabled = false
        tts = TextToSpeech(this.context,this)
        textToSpeech.setOnClickListener {
            var text= article.description.toString()
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH,null,"1")

        }



    }




    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if(!clicked){
            save_news.startAnimation(fromBottom)

            share.startAnimation(fromBottom)
            textToSpeech.startAnimation(fromBottom)
            fab.startAnimation(rotateOpen)
        }
        else{
            save_news.startAnimation(toBottom)

            share.startAnimation(toBottom)
            textToSpeech.startAnimation(toBottom)
            fab.startAnimation(rotateClose)
        }

    }


    private fun setVisibility(clicked: Boolean) {
        if(!clicked){
            save_news.show()
            share.show()
            textToSpeech.show()
        }
        else{
            save_news.hide()
            share.hide()
            textToSpeech.hide()
        }
    }

    private fun setClickable(clicked: Boolean){
        if(!clicked){
            save_news.isClickable= true
            share.isClickable = true
            textToSpeech.isClickable= true

        } else{
            save_news.isClickable= false
            share.isClickable = false
            textToSpeech.isClickable= false
        }
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val res = tts!!.setLanguage(Locale.ENGLISH)
            if(res== TextToSpeech.LANG_NOT_SUPPORTED || res== TextToSpeech.LANG_MISSING_DATA){
                Log.e("TTS","Language not supported")
            } else{
                textToSpeech.isEnabled = true
            }
        }
        else{
            Log.e("TTS","TTS Failed")
        }
    }

    override fun onDestroy() {
        if(tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }


}