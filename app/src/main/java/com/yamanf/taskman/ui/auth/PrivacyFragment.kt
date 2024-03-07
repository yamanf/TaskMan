package com.yamanf.taskman.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import com.yamanf.taskman.R
import com.yamanf.taskman.databinding.FragmentPrivacyBinding
import com.yamanf.taskman.utils.Constants.Companion.PRIVACY_URL
import com.yamanf.taskman.utils.Constants.Companion.TERMS_URL


class PrivacyFragment : DialogFragment() {
    private var _binding: FragmentPrivacyBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val webView = view.findViewById<WebView>(R.id.web_view)
        webView.webViewClient = WebViewClient()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrivacyBinding.inflate(inflater,container,false)
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val webView = view?.findViewById<WebView>(R.id.web_view)
        if (tag=="Terms"){
            webView?.loadUrl(TERMS_URL)
        }else if(tag=="Privacy") webView?.loadUrl(PRIVACY_URL)

    }


}