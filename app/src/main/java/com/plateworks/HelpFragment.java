package com.plateworks;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.plateworks.R;

/**
 * Created by Popov on 13.07.2017.
 */

public class HelpFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.help, null);
        WebView wb = (WebView) view.findViewById(R.id.wbview);
        WebSettings webSettings = wb.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);

        wb.loadUrl("file:///android_asset/help.html");
        return view;
    }
}
