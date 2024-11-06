package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;


public class UIButton {
    private TextButton button;

    public UIButton(String text, Stage stage) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = new BitmapFont();

        //Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        /*
        BitmapFont font = new BitmapFont();
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("buttons/buttons.pack"));
        skin.addRegions(atlas);
        style.font = font;
        style.up = skin.getDrawable("up-button");
        style.down = skin.getDrawable("down-button");
        style.checked = skin.getDrawable("checked-button");
        */
        button = new TextButton(text, style);
        button.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        stage.addActor(button);
    }
}
