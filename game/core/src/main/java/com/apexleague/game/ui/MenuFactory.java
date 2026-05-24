package com.apexleague.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.files.FileHandle;
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
        BitmapFont defaultFont = null;
        BitmapFont titleFont = null;

        FileHandle minecraftoryFont = Gdx.files.internal("ui/Minecraftory.ttf");
        FileHandle fallbackMinecraftoryFont = Gdx.files.internal("ui/Minercraftory.ttf");
        FileHandle fontFile = minecraftoryFont.exists() ? minecraftoryFont : (fallbackMinecraftoryFont.exists() ? fallbackMinecraftoryFont : null);

        if (fontFile != null) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
            try {
                FreeTypeFontGenerator.FreeTypeFontParameter defaultParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
                defaultParams.size = 20;
                defaultFont = generator.generateFont(defaultParams);

                FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
                titleParams.size = 40;
                titleFont = generator.generateFont(titleParams);
            } finally {
                generator.dispose();
            }
        }

        if (defaultFont == null) {
            defaultFont = new BitmapFont();
        }
        if (titleFont == null) {
            titleFont = new BitmapFont();
        }

        skin.add("default-font", defaultFont);
        skin.add("title-font", titleFont);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        skin.add("white", texture);

        Label.LabelStyle labelStyle = new Label.LabelStyle(defaultFont, Color.WHITE);
        skin.add("default", labelStyle);

        Drawable up = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(0.16f, 0.2f, 0.26f, 0.9f));
        Drawable over = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(0.22f, 0.28f, 0.34f, 0.9f));
        Drawable down = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(0.12f, 0.16f, 0.22f, 0.95f));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = up;
        buttonStyle.over = over;
        buttonStyle.down = down;
        buttonStyle.font = defaultFont;
        buttonStyle.fontColor = Color.WHITE;
        skin.add("default", buttonStyle);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = defaultFont;
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

    public static TextButton createTextButton(Skin skin, String text) {
        return new TextButton(text, skin);
    }

    public static Drawable createStadiumBackground() {
        FileHandle bgFile = Gdx.files.internal("images/bg.png");
        if (bgFile.exists()) {
            Texture bgTexture = new Texture(bgFile);
            return new TextureRegionDrawable(new TextureRegion(bgTexture));
        }
        return null;
    }
}
