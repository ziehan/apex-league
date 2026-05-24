package com.apexleague.game.screens;

import com.apexleague.game.Main;
import com.apexleague.game.managers.GameManager;
import com.apexleague.game.managers.GameManager.LeaderboardEntry;
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

    private final Table leaderboardTable;
    private final ScrollPane scrollPane;

    private String activeCategory = "mmr";

    public LeaderboardScreen(Main game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        skin = MenuFactory.createDefaultSkin();

        pitchTex = new Texture("images/bg.png");

        tableBgDrawable = createTableBackground(new Color(0f, 0f, 0f, 0.8f));
        rowLightDrawable = createTableBackground(new Color(1f, 1f, 1f, 0.05f));
        rowDarkDrawable = createTableBackground(new Color(1f, 1f, 1f, 0.02f));

        tableBgTex = ((TextureRegionDrawable) tableBgDrawable).getRegion().getTexture();
        rowLightTex = ((TextureRegionDrawable) rowLightDrawable).getRegion().getTexture();
        rowDarkTex = ((TextureRegionDrawable) rowDarkDrawable).getRegion().getTexture();

        Label header = new Label("GLOBAL LEADERBOARD", skin);
        header.setColor(Color.GOLD);
        header.setFontScale(1.5f);

        leaderboardTable = new Table();
        leaderboardTable.top();

        scrollPane = new ScrollPane(leaderboardTable, new ScrollPane.ScrollPaneStyle());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        Table tabsTable = new Table();

        TextButton mmrTab = MenuFactory.createTextButton(skin, "MMR");
        TextButton winsTab = MenuFactory.createTextButton(skin, "WINS");
        TextButton goalsTab = MenuFactory.createTextButton(skin, "GOALS");
        TextButton savesTab = MenuFactory.createTextButton(skin, "SAVES");
        TextButton demosTab = MenuFactory.createTextButton(skin, "DEMOS");

        tabsTable.add(mmrTab).width(140f).height(44f).padRight(10f);
        tabsTable.add(winsTab).width(140f).height(44f).padRight(10f);
        tabsTable.add(goalsTab).width(140f).height(44f).padRight(10f);
        tabsTable.add(savesTab).width(140f).height(44f).padRight(10f);
        tabsTable.add(demosTab).width(140f).height(44f);

        TextButton backButton = MenuFactory.createTextButton(skin, "BACK");

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.setBackground(tableBgDrawable);
        root.pad(40f);

        root.add(header).padBottom(20f).row();
        root.add(tabsTable).padBottom(16f).row();
        root.add(scrollPane).width(720f).height(360f).padBottom(30f).row();
        root.add(backButton).width(220f).height(50f).left();

        stage.addActor(root);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.goToProfile();
            }
        });

        mmrTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                loadLeaderboard("mmr");
            }
        });

        winsTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                loadLeaderboard("wins");
            }
        });

        goalsTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                loadLeaderboard("goals");
            }
        });

        savesTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                loadLeaderboard("saves");
            }
        });

        demosTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                loadLeaderboard("demos");
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.setScrollFocus(scrollPane);

        loadLeaderboard(activeCategory);
    }

    private void loadLeaderboard(String category) {
        activeCategory = category;

        showLoadingMessage();

        GameManager.getInstance().fetchLeaderboard(activeCategory, () -> {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    rebuildTable();
                }
            });
        });
    }

    private void showLoadingMessage() {
        leaderboardTable.clearChildren();
        leaderboardTable.clear();

        Label.LabelStyle loadingStyle = new Label.LabelStyle(
            skin.getFont("default-font"),
            Color.LIGHT_GRAY
        );

        Label loadingLabel = new Label("LOADING LEADERBOARD...", loadingStyle);
        loadingLabel.setAlignment(Align.center);

        leaderboardTable.add(loadingLabel).pad(20f).row();
        leaderboardTable.invalidateHierarchy();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.03f, 0.05f, 0.08f, 1f);

        stage.getViewport().apply();

        game.batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        game.batch.setColor(0.15f, 0.15f, 0.15f, 1f);

        game.batch.begin();
        game.batch.draw(
            pitchTex,
            0f,
            0f,
            stage.getViewport().getWorldWidth(),
            stage.getViewport().getWorldHeight()
        );
        game.batch.end();

        game.batch.setColor(Color.WHITE);

        stage.act(delta);
        stage.draw();
    }

    private void rebuildTable() {
        GameManager gm = GameManager.getInstance();

        leaderboardTable.clearChildren();
        leaderboardTable.clear();

        if (gm.globalLeaderboard == null || gm.globalLeaderboard.isEmpty()) {
            Label.LabelStyle emptyStyle = new Label.LabelStyle(
                skin.getFont("default-font"),
                Color.LIGHT_GRAY
            );

            Label label = new Label("NO LEADERBOARD DATA", emptyStyle);
            label.setAlignment(Align.center);

            leaderboardTable.add(label).pad(20f).row();
            leaderboardTable.invalidateHierarchy();
            return;
        }

        Table headerRow = new Table();
        headerRow.setBackground(rowDarkDrawable);
        headerRow.pad(10f);

        addGridCell(headerRow, "RANK", Color.LIGHT_GRAY, 100f);
        addGridCell(headerRow, "USERNAME", Color.LIGHT_GRAY, 360f);
        addGridCell(headerRow, "SCORE", Color.LIGHT_GRAY, 180f);

        leaderboardTable.add(headerRow)
            .width(680f)
            .height(44f)
            .padBottom(6f)
            .row();

        for (int i = 0; i < gm.globalLeaderboard.size; i++) {
            LeaderboardEntry entry = gm.globalLeaderboard.get(i);

            Table row = new Table();
            row.setBackground((i % 2 == 0) ? rowLightDrawable : rowDarkDrawable);
            row.pad(12f);

            addNumberCell(row, String.valueOf(i + 1), Color.WHITE, 100f);

            String safeName = "PLAYER";
            if (entry.username != null && !entry.username.isEmpty()) {
                safeName = entry.username
                    .toUpperCase()
                    .replace("_", " ");
            }

            addGridCell(row, safeName, Color.WHITE, 360f);
            addNumberCell(row, formatScore(entry.score), Color.GOLD, 180f);

            leaderboardTable.add(row)
                .width(680f)
                .height(52f)
                .padBottom(4f)
                .row();
        }

        leaderboardTable.invalidateHierarchy();
    }

    private String formatScore(double score) {
        return String.valueOf((long) score);
    }

    private void addGridCell(Table row, String text, Color color, float width) {
        Label.LabelStyle style = new Label.LabelStyle(
            skin.getFont("default-font"),
            color
        );

        Label label = new Label(text, style);
        label.setAlignment(Align.center);

        row.add(label)
            .width(width)
            .center();
    }

    private void addNumberCell(Table row, String text, Color color, float width) {
        Label.LabelStyle style = new Label.LabelStyle(
            skin.getFont("default-font"),
            color
        );

        Label label = new Label(text, style);
        label.setAlignment(Align.center);

        row.add(label)
            .width(width)
            .center();
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
