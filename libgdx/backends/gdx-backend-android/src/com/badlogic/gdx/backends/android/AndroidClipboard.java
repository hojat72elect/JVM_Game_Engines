package com.badlogic.gdx.backends.android;

import android.content.ClipData;
import android.content.Context;

import androidx.annotation.NonNull;

import com.badlogic.gdx.utils.Clipboard;

public class AndroidClipboard implements Clipboard {

    private final android.content.ClipboardManager clipboard;

    public AndroidClipboard(Context context) {
        clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public boolean hasContents() {
        return clipboard.hasPrimaryClip();
    }

    @Override
    public String getContents() {
        ClipData clip = clipboard.getPrimaryClip();
        if (clip == null) return null;
        CharSequence text = clip.getItemAt(0).getText();
        if (text == null) return null;
        return text.toString();
    }

    @Override
    public void setContents(@NonNull final String contents) {
        ClipData data = ClipData.newPlainText(contents, contents);
        clipboard.setPrimaryClip(data);
    }
}
