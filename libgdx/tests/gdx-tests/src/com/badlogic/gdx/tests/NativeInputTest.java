package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.InputStringValidator;
import com.badlogic.gdx.Input.OnscreenKeyboardType;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.NativeInputConfiguration;
import com.badlogic.gdx.input.TextInputWrapper;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.tests.utils.GdxTest;

public class NativeInputTest extends GdxTest {

    private Stage stage;
    private Skin skin;

    public void create() {
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        Table table = new Table();
        table.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.isStopped()) {
                    Gdx.input.closeTextInputField(false);
                }
                super.clicked(event, x, y);
            }
        });
        table.setFillParent(true);

        final SelectBox<OnscreenKeyboardType> selectBox = new SelectBox<>(skin);
        selectBox.setItems(OnscreenKeyboardType.values());
        selectBox.setWidth(200);
        selectBox.setPosition(200, 200);

        final Label maxLengthLabel = new Label("--", skin);
        final Slider maxLengthSlider = new Slider(0, 15, 1, false, skin);
        maxLengthSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (maxLengthSlider.getValue() == 0)
                    maxLengthLabel.setText("--");
                else
                    maxLengthLabel.setText((int) maxLengthSlider.getValue());
            }
        });

        final CheckBox showPasswordButton = new CheckBox("Show Password button", skin);

        final CheckBox multilineButton = new CheckBox("Multiline", skin);
        final CheckBox noAutocorrectButton = new CheckBox("No Autocorrect", skin);
        final CheckBox useValidatorButton = new CheckBox("Use validator", skin);
        final CheckBox useCustomAutocompleteButton = new CheckBox("Custom Autocomplete", skin);

        Label placeHodlerLabel = new Label("Placeholder:", skin);
        final TextField placeHolder = new TextField(null, skin);
        placeHolder.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("UWU3");
                super.clicked(event, x, y);
                event.stop();
            }
        });

        final TextArea result = new TextArea(null, skin);
        result.setDisabled(true);

        TextButton openInput = new TextButton("Open TextInput", skin);
        openInput.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("UWU2");
                NativeInputConfiguration configuration = new NativeInputConfiguration();
                configuration.setPreventCorrection(noAutocorrectButton.isChecked()).setMultiLine(multilineButton.isChecked())
                        .setShowPasswordButton(showPasswordButton.isChecked()).setPlaceholder(placeHolder.getText())
                        .setType(selectBox.getSelected());
                if (useCustomAutocompleteButton.isChecked())
                    configuration.setAutoComplete(new String[]{"Hello", "Hillo", "Hellale", "Dog", "Dogfood"});
                if (maxLengthSlider.getValue() != 0) configuration.setMaxLength((int) maxLengthSlider.getValue());
                if (useValidatorButton.isChecked()) configuration.setValidator(new InputStringValidator() {
                    @Override
                    public boolean validate(String toCheck) {
                        return !toCheck.contains("!");
                    }
                });
                configuration.setTextInputWrapper(new TextInputWrapper() {
                    @Override
                    public String getText() {
                        return "";
                    }

                    @Override
                    public void setText(String text) {
                        result.setText(text);
                    }

                    @Override
                    public int getSelectionStart() {
                        return 0;
                    }

                    @Override
                    public int getSelectionEnd() {
                        return 0;
                    }

                    @Override
                    public void setPosition(int position) {
                    }

                    @Override
                    public boolean shouldClose() {
                        return true;
                    }
                });
                try {
                    configuration.validate();
                    Gdx.input.openTextInputField(configuration);
                } catch (IllegalArgumentException e) {
                    result.setText(e.getMessage());
                }
                event.stop();
            }
        });
        HorizontalGroup g1 = new HorizontalGroup();
        g1.addActor(selectBox);
        g1.addActor(maxLengthSlider);
        g1.addActor(maxLengthLabel);
        table.add(g1);
        table.row();
        HorizontalGroup g2 = new HorizontalGroup();
        g2.space(5);
        g2.addActor(showPasswordButton);
        g2.addActor(multilineButton);
        g2.addActor(noAutocorrectButton);
        g2.addActor(useValidatorButton);
        table.add(g2);
        table.row();

        HorizontalGroup g3 = new HorizontalGroup();
        g3.addActor(placeHodlerLabel);
        g3.addActor(placeHolder);
        table.add(g3);
        table.row();
        table.add(useCustomAutocompleteButton);
        table.row().padTop(15);
        table.add(openInput);
        table.row().padTop(15);
        table.add(result).grow();

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
}
