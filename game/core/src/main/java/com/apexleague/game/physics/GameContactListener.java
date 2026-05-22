package com.apexleague.game.physics;

import com.apexleague.game.components.PhysicsComponent;
import com.apexleague.game.managers.BoostManager;
import com.apexleague.game.screens.PlayScreen;
import com.apexleague.game.state.GameManager;
import com.apexleague.game.ui.HUD;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameContactListener implements ContactListener {
    private final GameManager gameManager;
    private final PlayScreen playScreen;
    private final BoostManager boostManager;
    private final HUD hud;

    public GameContactListener(GameManager gameManager, PlayScreen playScreen, BoostManager boostManager, HUD hud) {
        this.gameManager = gameManager;
        this.playScreen = playScreen;
        this.boostManager = boostManager;
        this.hud = hud;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        Object dataA = fixA.getUserData();
        Object dataB = fixB.getUserData();

        if (dataA == null || dataB == null) {
            return;
        }

        checkGoal(dataA, dataB);
        checkDemolition(fixA, fixB);
        checkBoostPads(fixA, fixB);
    }

    private void checkGoal(Object a, Object b) {
        if (gameManager.isResetting || gameManager.isGameOver) {
            return;
        }
        if ((a.equals("BALL") && b.equals("GOAL_LEFT")) || (b.equals("BALL") && a.equals("GOAL_LEFT"))) {
            gameManager.addScore(false);
            if (gameManager.isOvertime) {
                gameManager.isGameOver = true;
                gameManager.winnerText = "KANAN MENANG!";
                if (hud != null) {
                    hud.setCenterText(gameManager.winnerText);
                }
                return;
            }
            if (hud != null) {
                hud.setCenterText("GOAL!");
            }
            playScreen.triggerShake(0.5f, 0.5f);
            gameManager.startReset();
        } else if ((a.equals("BALL") && b.equals("GOAL_RIGHT")) || (b.equals("BALL") && a.equals("GOAL_RIGHT"))) {
            gameManager.addScore(true);
            if (gameManager.isOvertime) {
                gameManager.isGameOver = true;
                gameManager.winnerText = "KIRI MENANG!";
                if (hud != null) {
                    hud.setCenterText(gameManager.winnerText);
                }
                return;
            }
            if (hud != null) {
                hud.setCenterText("GOAL!");
            }
            playScreen.triggerShake(0.5f, 0.5f);
            gameManager.startReset();
        }
    }

    private void checkDemolition(Fixture fixA, Fixture fixB) {
        if (gameManager.isResetting) {
            return;
        }
        if (!("PLAYER".equals(fixA.getUserData()) && "PLAYER".equals(fixB.getUserData()))) {
            return;
        }

        Body bodyA = fixA.getBody();
        Body bodyB = fixB.getBody();
        if (bodyA == bodyB) {
            return;
        }

        PhysicsComponent physA = bodyA.getUserData() instanceof PhysicsComponent ? (PhysicsComponent) bodyA.getUserData() : null;
        PhysicsComponent physB = bodyB.getUserData() instanceof PhysicsComponent ? (PhysicsComponent) bodyB.getUserData() : null;
        if (physA == null || physB == null) {
            return;
        }

        if (physA.isSupersonic && !physB.isSupersonic) {
            physB.pendingDemolition = true;
        } else if (physB.isSupersonic && !physA.isSupersonic) {
            physA.pendingDemolition = true;
        }
    }

    private void checkBoostPads(Fixture fixA, Fixture fixB) {
        if (!("PLAYER".equals(fixA.getUserData()) || "PLAYER".equals(fixB.getUserData()))) {
            return;
        }

        Fixture playerFixture = "PLAYER".equals(fixA.getUserData()) ? fixA : fixB;
        Fixture padFixture = playerFixture == fixA ? fixB : fixA;
        Object padData = padFixture.getUserData();
        if (!(padData instanceof BoostManager.BoostPad)) {
            return;
        }

        BoostManager.BoostPad pad = (BoostManager.BoostPad) padData;
        if (!pad.isActive()) {
            return;
        }

        PhysicsComponent phys = playerFixture.getBody().getUserData() instanceof PhysicsComponent ? (PhysicsComponent) playerFixture.getBody().getUserData() : null;
        if (phys == null) {
            return;
        }

        if (phys.boostAmount >= 100f) {
            return;
        }

        if (pad.isLarge()) {
            phys.boostAmount = 100f;
            pad.deactivate(10f);
        } else {
            phys.boostAmount = Math.min(100f, phys.boostAmount + 12f);
            pad.deactivate(3f);
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
