package com.apexleague.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public final class MenuFactory {
    private MenuFactory() {
    }

    public static Skin createDefaultSkin() {
        Skin skin = new Skin();
        FreeTypeFontGenerator titleGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Robot-Crush.ttf"));
        FreeTypeFontGenerator uiGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Dunkerque-Regular.otf"));

        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 72;
        BitmapFont titleFont = titleGenerator.generateFont(titleParams);
        skin.add("title-font", titleFont);

        FreeTypeFontGenerator.FreeTypeFontParameter uiParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        uiParams.size = 24;
        BitmapFont font = uiGenerator.generateFont(uiParams);
        skin.add("default-font", font);

        titleGenerator.dispose();
        uiGenerator.dispose();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        skin.add("white", texture);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", labelStyle);

        Drawable up = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(0.16f, 0.2f, 0.26f, 0.9f));
        Drawable over = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(0.22f, 0.28f, 0.34f, 0.9f));
        Drawable down = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(0.12f, 0.16f, 0.22f, 0.95f));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = up;
        buttonStyle.over = over;
        buttonStyle.down = down;
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        skin.add("default", buttonStyle);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.background = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(0.12f, 0.16f, 0.22f, 0.9f));
        textFieldStyle.cursor = new TextureRegionDrawable(new TextureRegion(texture)).tint(Color.WHITE);
        textFieldStyle.selection = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(0.25f, 0.35f, 0.45f, 0.6f));
        skin.add("default", textFieldStyle);

        return skin;
    }

    public static Drawable createPanelDrawable(Skin skin, Color color) {
        return skin.newDrawable("white", color);
    }
}

