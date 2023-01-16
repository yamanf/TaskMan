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
            webView?.loadUrl("https://doc-hosting.flycricket.io/taskman-terms-of-use/7a44b8d1-988d-484b-a214-62674347abc5/terms")
        }else if(tag=="Privacy") webView?.loadUrl("https://doc-hosting.flycricket.io/taskman-privacy-policy/364822f1-3d8b-45da-882b-406ff834e311/privacy")

    }


}