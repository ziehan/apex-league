package com.apexleague.game.screens;

import com.apexleague.game.Main;
import com.apexleague.game.state.GameManager;
import com.apexleague.game.ui.MenuFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LeaderboardScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final com.badlogic.gdx.scenes.scene2d.ui.Skin skin;
    private final Texture pitchTex;
    private final Texture tableBgTex;
    private final Texture rowLightTex;
    private final Texture rowDarkTex;
    private final Drawable tableBgDrawable;
    private final Drawable rowLightDrawable;
    private final Drawable rowDarkDrawable;

    public LeaderboardScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = MenuFactory.createDefaultSkin();
        pitchTex = new Texture("images/football_pitch.png");
        tableBgDrawable = createTableBackground(new Color(0f, 0f, 0f, 0.8f));
        rowLightDrawable = createTableBackground(new Color(1f, 1f, 1f, 0.05f));
        rowDarkDrawable = createTableBackground(new Color(1f, 1f, 1f, 0.02f));

        tableBgTex = ((TextureRegionDrawable) tableBgDrawable).getRegion().getTexture();
        rowLightTex = ((TextureRegionDrawable) rowLightDrawable).getRegion().getTexture();
        rowDarkTex = ((TextureRegionDrawable) rowDarkDrawable).getRegion().getTexture();

        Label header = new Label("GLOBAL LEADERBOARD", skin);
        header.setColor(Color.GOLD);
        header.setFontScale(1.5f);

        // Header Row
        Table headerRow = new Table();
        headerRow.setBackground(rowDarkDrawable);
        headerRow.pad(10f);
        addGridCell(headerRow, "RANK", Color.LIGHT_GRAY, 80f);
        addGridCell(headerRow, "TEAM", Color.LIGHT_GRAY, 160f);
        addGridCell(headerRow, "WINS", Color.LIGHT_GRAY, 100f);
        addGridCell(headerRow, "GOALS", Color.LIGHT_GRAY, 100f);
        addGridCell(headerRow, "SAVES", Color.LIGHT_GRAY, 100f);
        addGridCell(headerRow, "DEMOS", Color.LIGHT_GRAY, 100f);

        // Data Rows
        Table rowOne = buildTeamRow(true);
        Table rowTwo = buildTeamRow(false);

        TextButton backButton = new TextButton("BACK", skin);

        // Main Container
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.setBackground(tableBgDrawable);
        root.pad(40f); // Tambah padding agar lega

        root.add(header).padBottom(20f).row();
        root.add(headerRow).width(720f).height(44f).padBottom(5f).row();
        root.add(rowOne).width(720f).height(56f).padBottom(5f).row();
        root.add(rowTwo).width(720f).height(56f).padBottom(30f).row();
        root.add(backButton).width(220f).height(50f).left();
        stage.addActor(root);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new ProfileScreen(game));
            }
        });
    }

    private Table buildTeamRow(boolean isTopRank) {
        GameManager gm = GameManager.getInstance();
        boolean redOnTop = gm.totalP1Wins > gm.totalP2Wins
            || (gm.totalP1Wins == gm.totalP2Wins && gm.p1Goals >= gm.p2Goals);

        boolean isRedRow = isTopRank == redOnTop;
        String rankText = isTopRank ? "1" : "2";

        String teamName = isRedRow ? "RED" : "BLUE";
        int wins = isRedRow ? gm.totalP1Wins : gm.totalP2Wins;
        int goals = isRedRow ? gm.p1Goals : gm.p2Goals;
        int saves = isRedRow ? gm.p1Saves : gm.p2Saves;
        int demos = isRedRow ? gm.p1Demos : gm.p2Demos;
        Color teamColor = isRedRow ? Color.SCARLET : Color.CYAN;

        Table row = new Table();
        row.setBackground(isTopRank ? rowLightDrawable : rowDarkDrawable);
        row.pad(10f);

        addGridCell(row, rankText, Color.WHITE, 80f);
        addGridCell(row, teamName, teamColor, 160f);
        addGridCell(row, String.valueOf(wins), Color.WHITE, 100f);
        addGridCell(row, String.valueOf(goals), Color.WHITE, 100f);
        addGridCell(row, String.valueOf(saves), Color.WHITE, 100f);
        addGridCell(row, String.valueOf(demos), Color.WHITE, 100f);

        return row;
    }

    // Method tunggal penjamin kerapian grid
    private void addGridCell(Table row, String text, Color color, float width) {
        Label.LabelStyle style = new Label.LabelStyle(skin.getFont("default-font"), color);
        Label label = new Label(text, style);
        label.setAlignment(Align.center); // Paksa teks ke tengah
        row.add(label).width(width).center(); // Paksa kotak selnya selebar width
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.03f, 0.05f, 0.08f, 1f);
        stage.getViewport().apply();
        game.batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        game.batch.setColor(0.15f, 0.15f, 0.15f, 1f);
        game.batch.begin();
        game.batch.draw(pitchTex, 0f, 0f, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        game.batch.end();
        game.batch.setColor(Color.WHITE);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        pitchTex.dispose();
        tableBgTex.dispose();
        rowLightTex.dispose();
        rowDarkTex.dispose();
    }

    private Drawable createTableBackground(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
