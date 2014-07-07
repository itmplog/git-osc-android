/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.gitapp.util;

import static net.oschina.gitapp.common.Contanst.CHARSET_UTF8;
import static net.oschina.gitapp.bean.CodeFile.ENCODING_BASE64;
import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import net.oschina.gitapp.bean.CodeFile;
import net.oschina.gitapp.common.UIHelper;

/**
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressLint("SetJavaScriptEnabled")
public class SourceEditor {

    private static final String URL_PAGE = "file:///android_asset/source-editor.html";

    private final WebView view;
    
    private boolean wrap;

    private String name;

    private String content;

    private boolean encoded;

    private boolean markdown;

    /**
     * Create source editor using given web view
     *
     * @param view
     */
    @SuppressLint("JavascriptInterface")
	public SourceEditor(final WebView view) {
        
        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);
        view.addJavascriptInterface(this, "SourceEditor");
        this.view = view;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return content
     */
    public String getRawContent() {
        return content;
    }

    /**
     * @return content
     */
    public String getContent() {
        if (encoded)
            try {
                return new String(EncodingUtils.fromBase64(content),
                		CHARSET_UTF8);
            } catch (UnsupportedEncodingException e) {
                return getRawContent();
            }
        else
            return getRawContent();
    }

    /**
     * @return wrap
     */
    public boolean getWrap() {
        return wrap;
    }

    /**
     * @return markdown
     */
    public boolean isMarkdown() {
        return markdown;
    }

    /**
     * Set whether lines should wrap
     *
     * @param wrap
     * @return this editor
     */
    public SourceEditor setWrap(final boolean wrap) {
        this.wrap = wrap;
        loadSource();
        return this;
    }

    /**
     * Sets whether the content is a markdown file
     *
     * @param markdown
     * @return this editor
     */
    public SourceEditor setMarkdown(final boolean markdown) {
        this.markdown = markdown;
        return this;
    }

    /**
     * Bind content to current {@link WebView}
     *
     * @param name
     * @param content
     * @param encoded
     * @return this editor
     */
    public SourceEditor setSource(final String name, final String content,
            final boolean encoded) {
        this.name = name;
        this.content = content;
        this.encoded = encoded;
        loadSource();

        return this;
    }

    private void loadSource() {
    	if (name != null && content != null)
            if (name.endsWith(".md"))
                view.loadDataWithBaseURL(null, UIHelper.WEB_STYLE + getContent(), "text/html", CHARSET_UTF8, null);
            else
                view.loadUrl(URL_PAGE);
    }

    /**
     * Bind blob content to current {@link WebView}
     *
     * @param name
     * @param blob
     * @return this editor
     */
    public SourceEditor setSource(final String name, final CodeFile blob) {
        String content = blob.getContent();
        if (content == null)
            content = "";
        boolean encoded = !TextUtils.isEmpty(content)
                && ENCODING_BASE64.equals(blob.getEncoding());
        return setSource(name, content, encoded);
    }

    /**
     * Toggle line wrap
     *
     * @return this editor
     */
    public SourceEditor toggleWrap() {
        return setWrap(!wrap);
    }

    /**
     * Toggle markdown file rendering
     *
     * @return this editor
     */
    public SourceEditor toggleMarkdown() {
        return setMarkdown(!markdown);
    }
}