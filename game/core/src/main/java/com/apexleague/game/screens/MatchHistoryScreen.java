package com.apexleague.game.screens;

import com.apexleague.game.Main;
import com.apexleague.game.managers.GameManager;
import com.apexleague.game.managers.GameManager.MatchRecord;
import com.apexleague.game.ui.MenuFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MatchHistoryScreen implements Screen {
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

    private final Table listTable;
    private final ScrollPane scrollPane;
    private int lastCount = -1;

    public MatchHistoryScreen(Main game) {
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

        Label header = new Label("MATCH HISTORY", skin);
        header.setColor(Color.GOLD);
        header.setFontScale(1.5f);

        listTable = new Table();
        listTable.top();

        scrollPane = new ScrollPane(listTable, new ScrollPane.ScrollPaneStyle());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        TextButton backButton = MenuFactory.createTextButton(skin, "BACK");

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.setBackground(tableBgDrawable);
        root.pad(30f);
        root.add(header).padBottom(20f).row();
        root.add(scrollPane).width(700f).height(360f).padBottom(20f).row();
        root.add(backButton).width(220f).height(50f).left();
        stage.addActor(root);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.goToProfile();
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        GameManager.getInstance().fetchUserStats(() -> lastCount = -1);
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

        rebuildIfNeeded();

        stage.act(delta);
        stage.draw();
    }

    private void rebuildIfNeeded() {
        GameManager gm = GameManager.getInstance();
        if (gm.matchHistory.size == lastCount) {
            return;
        }
        lastCount = gm.matchHistory.size;
        listTable.clearChildren();

        Label.LabelStyle dateStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.LIGHT_GRAY);
        Label.LabelStyle redStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.SCARLET);
        Label.LabelStyle blueStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.CYAN);
        Label.LabelStyle redWinStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.SCARLET);
        Label.LabelStyle blueWinStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.CYAN);
        Label.LabelStyle drawStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.GOLD);

        for (int i = gm.matchHistory.size - 1; i >= 0; i--) {
            MatchRecord record = gm.matchHistory.get(i);
            Table row = new Table();
            row.setBackground((i % 2 == 0) ? rowLightDrawable : rowDarkDrawable);
            row.pad(16f);

            Label dateLabel = new Label(record.date != null ? record.date : "Match", dateStyle);
            dateLabel.setFontScale(0.9f);

            Label redScore = new Label(String.valueOf(record.p1Score), redStyle);
            Label blueScore = new Label(String.valueOf(record.p2Score), blueStyle);
            Label dash = new Label("-", skin);
            redScore.setFontScale(1.2f);
            blueScore.setFontScale(1.2f);
            dash.setFontScale(1.2f);

            Table scoreTable = new Table();
            scoreTable.add(redScore).padRight(6f);
            scoreTable.add(dash).padRight(6f);
            scoreTable.add(blueScore);

            boolean p1Win = record.p1Score > record.p2Score;
            boolean p2Win = record.p2Score > record.p1Score;
            boolean isDraw = record.p1Score == record.p2Score;

            String statusText = isDraw ? "DRAW" : (p1Win ? "RED WIN" : "BLUE WIN");
            Label.LabelStyle currentStatusStyle = isDraw ? drawStyle : (p1Win ? redWinStyle : blueWinStyle);

            Label statusLabel = new Label(statusText, currentStatusStyle);
            statusLabel.setAlignment(Align.center);

            row.add(dateLabel).width(180f).left().padRight(20f);
            row.add(scoreTable).expandX().center();
            row.add(statusLabel).width(80f).right();

            listTable.add(row).expandX().fillX().padBottom(10f).row();
        }
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
