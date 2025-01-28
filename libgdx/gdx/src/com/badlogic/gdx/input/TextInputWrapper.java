package com.badlogic.gdx.input;

public interface TextInputWrapper {

    String getText();

    void setText(String text);

    int getSelectionStart();

    int getSelectionEnd();

    void setPosition(int position);

    boolean shouldClose();
}
