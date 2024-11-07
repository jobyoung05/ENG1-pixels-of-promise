package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;


public class UIButton extends TextButton{
    static private TextButton.TextButtonStyle style;
    static {
        // style only needs defining once, not every time a UIButton instance is created
        // so we define style in a static block (which will only run once)
        style = new TextButton.TextButtonStyle();
        style.font = new BitmapFont();
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("default", new BitmapFont());
        style.up = skin.newDrawable("white", Color.LIGHT_GRAY);
        style.down = skin.newDrawable("white", Color.DARK_GRAY);
        style.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        style.checked = skin.newDrawable("white", Color.DARK_GRAY);
    }
    private String actionName;

    public UIButton(Stage stage, String text, String actionName, int x, int y, int w, int h) {
        super(text, style);
        this.actionName = actionName;
        this.setPosition(x, y);
        this.setSize(w, h);
        stage.addActor(this);
    }

    public String getActionName() {
        return actionName;
    }

}
