package com.badlogic.gdx.tests.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

public class NetAPITest extends GdxTest implements HttpResponseListener {

    SpriteBatch batch;
    Skin skin;
    Stage stage;
    TextButton btnDownloadImage;
    TextButton btnDownloadText;
    TextButton btnDownloadLarge;
    TextButton btnDownloadError;
    TextButton btnPost;
    TextButton btnCancel;
    TextButton btnOpenUri;
    Label statusLabel;
    Texture texture;
    String text;
    BitmapFont font;
    HttpRequest httpRequest;

    Object clickedButton;

    public boolean needsGL20() {
        // just because the non pot, we could change the image instead...
        return true;
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        skin.dispose();
        font.dispose();
        if (texture != null) texture.dispose();
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        font = new BitmapFont();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        {
            statusLabel = new Label("", skin);
            statusLabel.setWrap(true);
            statusLabel.setWidth(Gdx.graphics.getWidth() * 0.96f);
            statusLabel.setAlignment(Align.center);
            statusLabel.setPosition(Gdx.graphics.getWidth() * 0.5f - statusLabel.getWidth() * 0.5f, 30f);
            statusLabel.setColor(Color.CYAN);
            stage.addActor(statusLabel);
        }

        {
            ClickListener clickListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);

                    clickedButton = event.getListenerActor();
                    setButtonDisabled(true);
                    if (texture != null) texture.dispose();
                    texture = null;
                    text = null;

                    String url;
                    String httpMethod = Net.HttpMethods.GET;
                    String requestContent = null;
                    if (clickedButton == btnDownloadImage)
                        url = "https://i.imgur.com/vxomF.jpg";
                    else if (clickedButton == btnDownloadText)
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt";
                    else if (clickedButton == btnDownloadLarge)
                        url = "https://github.com/libgdx/libgdx/archive/refs/tags/1.11.0.zip";
                    else if (clickedButton == btnDownloadError)
                        url = "https://libgdx.com/doesnotexist";
                    else if (clickedButton == btnOpenUri) {
                        Gdx.net.openURI("https://libgdx.com");
                        return;
                    } else {
                        url = "https://httpbin.org/post";
                        httpMethod = Net.HttpMethods.POST;
                        requestContent = "name1=value1&name2=value2";
                    }

                    httpRequest = new HttpRequest(httpMethod);
                    httpRequest.setUrl(url);
                    httpRequest.setContent(requestContent);
                    Gdx.net.sendHttpRequest(httpRequest, NetAPITest.this);

                    statusLabel.setText("Downloading data from " + httpRequest.getUrl());
                }
            };

            ClickListener cancelListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if (httpRequest != null) {
                        Gdx.net.cancelHttpRequest(httpRequest);
                        Gdx.app.log("NetAPITest", "Cancelling request " + httpRequest.getUrl());
                        statusLabel.setText("Cancelling request " + httpRequest.getUrl());
                    }
                }
            };

            btnCancel = new TextButton("Cancel", skin);
            btnCancel.setPosition(Gdx.graphics.getWidth() * 0.10f, 60f);
            btnCancel.addListener(cancelListener);
            stage.addActor(btnCancel);

            btnDownloadImage = new TextButton("GET Image", skin);
            btnDownloadImage.setPosition(btnCancel.getX() + btnCancel.getWidth() + 10, 60f);
            btnDownloadImage.addListener(clickListener);
            stage.addActor(btnDownloadImage);

            btnDownloadText = new TextButton("GET Text", skin);
            btnDownloadText.setPosition(btnDownloadImage.getX() + btnDownloadImage.getWidth() + 10, 60f);
            btnDownloadText.addListener(clickListener);
            stage.addActor(btnDownloadText);

            btnDownloadLarge = new TextButton("GET Large", skin);
            btnDownloadLarge.setPosition(btnDownloadText.getX() + btnDownloadText.getWidth() + 10, 60f);
            btnDownloadLarge.addListener(clickListener);
            stage.addActor(btnDownloadLarge);

            btnDownloadError = new TextButton("GET Error", skin);
            btnDownloadError.setPosition(btnDownloadLarge.getX() + btnDownloadLarge.getWidth() + 10, 60f);
            btnDownloadError.addListener(clickListener);
            stage.addActor(btnDownloadError);

            btnPost = new TextButton("POST", skin);
            btnPost.setPosition(btnDownloadError.getX() + btnDownloadError.getWidth() + 10, 60f);
            btnPost.addListener(clickListener);
            stage.addActor(btnPost);

            btnOpenUri = new TextButton("Open URI", skin);
            btnOpenUri.setPosition(btnPost.getX() + btnPost.getWidth() + 10, 60f);
            btnOpenUri.addListener(clickListener);

            stage.addActor(btnOpenUri);
        }
    }

    @Override
    public void handleHttpResponse(HttpResponse httpResponse) {

        final int statusCode = httpResponse.getStatus().getStatusCode();
        // We are not in main thread right now so we need to post to main thread for ui updates
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                statusLabel.setText("HTTP Request status: " + statusCode);
                setButtonDisabled(false);
            }
        });

        if (statusCode != 200) {
            Gdx.app.log("NetAPITest", "An error ocurred since statusCode is not OK");
            setText(httpResponse);
            return;
        }

        if (clickedButton == btnDownloadImage) {
            final byte[] rawImageBytes = httpResponse.getResult();
            Gdx.app.postRunnable(new Runnable() {
                public void run() {
                    Pixmap pixmap = new Pixmap(rawImageBytes, 0, rawImageBytes.length);
                    texture = new Texture(pixmap);
                }
            });
        } else if (clickedButton == btnDownloadLarge) {
            Gdx.app.postRunnable(new Runnable() {
                public void run() {
                    text = "Retrieving large file...";
                }
            });
            final byte[] rawFileBytes = httpResponse.getResult();
            Gdx.app.postRunnable(new Runnable() {
                public void run() {
                    text = "Retrieved large file: " + rawFileBytes.length;
                }
            });
        } else {
            setText(httpResponse);
        }
    }

    void setText(HttpResponse httpResponse) {
        final String newText = httpResponse.getResultAsString();
        Gdx.app.postRunnable(new Runnable() {
            public void run() {
                text = newText;
            }
        });
    }

    void setButtonDisabled(boolean disabled) {
        Touchable t = disabled ? Touchable.disabled : Touchable.enabled;

        btnDownloadImage.setDisabled(disabled);
        btnDownloadImage.setTouchable(t);

        btnDownloadText.setDisabled(disabled);
        btnDownloadText.setTouchable(t);

        btnDownloadError.setDisabled(disabled);
        btnDownloadError.setTouchable(t);

        btnPost.setDisabled(disabled);
        btnPost.setTouchable(t);
    }

    @Override
    public void failed(Throwable t) {
        setButtonDisabled(false);
        statusLabel.setText("Failed to perform the HTTP Request: " + t.getMessage());
        t.printStackTrace();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

        if (texture != null) {
            batch.begin();
            batch.draw(texture, Gdx.graphics.getWidth() * 0.5f - texture.getWidth() * 0.5f, 100f);
            batch.end();
        } else if (text != null) {
            batch.begin();
            font.draw(batch, text, 10, Gdx.graphics.getHeight() - 10);
            batch.end();
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void cancelled() {
        Gdx.app.postRunnable(new Runnable() {
            public void run() {
                setButtonDisabled(false);
                Gdx.app.log("NetAPITest", "HTTP request cancelled");
                statusLabel.setText("HTTP request cancelled");
            }
        });
    }
}
