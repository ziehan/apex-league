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

        Table headerRow = new Table();
        headerRow.setBackground(rowDarkDrawable);
        headerRow.pad(10f);

        Label.LabelStyle headerStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.LIGHT_GRAY);
        addHeaderCell(headerRow, "RANK", headerStyle, 70f);
        addHeaderCell(headerRow, "TEAM", headerStyle, 170f);
        addHeaderCell(headerRow, "WINS", headerStyle, 90f);
        addHeaderCell(headerRow, "GOALS", headerStyle, 90f);
        addHeaderCell(headerRow, "SAVES", headerStyle, 90f);
        addHeaderCell(headerRow, "DEMOS", headerStyle, 90f);

        Table rowOne = buildTeamRow(true);
        Table rowTwo = buildTeamRow(false);

        TextButton backButton = new TextButton("BACK", skin);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.setBackground(tableBgDrawable);
        root.pad(30f);
        root.add(header).padBottom(18f).row();
        root.add(headerRow).width(760f).height(44f).padBottom(10f).row();
        root.add(rowOne).width(760f).height(56f).padBottom(8f).row();
        root.add(rowTwo).width(760f).height(56f).padBottom(18f).row();
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
        row.pad(12f);

        Label.LabelStyle teamStyle = new Label.LabelStyle(skin.getFont("default-font"), teamColor);
        Label.LabelStyle statStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.WHITE);

        addDataCell(row, rankText, statStyle, 70f, Align.center);
        addDataCell(row, teamName, teamStyle, 170f, Align.left);
        addDataCell(row, String.valueOf(wins), statStyle, 90f, Align.center);
        addDataCell(row, String.valueOf(goals), statStyle, 90f, Align.center);
        addDataCell(row, String.valueOf(saves), statStyle, 90f, Align.center);
        addDataCell(row, String.valueOf(demos), statStyle, 90f, Align.center);

        return row;
    }

    private void addHeaderCell(Table row, String text, Label.LabelStyle style, float width) {
        Label label = new Label(text, style);
        label.setAlignment(Align.center);
        row.add(label).width(width).center();
    }

    private void addDataCell(Table row, String text, Label.LabelStyle style, float width, int align) {
        Label label = new Label(text, style);
        label.setAlignment(align);
        row.add(label).width(width);
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
    public void pause() {
    }

    @Override
    public void resume() {
    }

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

