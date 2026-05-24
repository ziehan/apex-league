package com.apexleague.game.entities;

import com.apexleague.game.components.InputComponent;
import com.apexleague.game.components.JumpComponent;
import com.apexleague.game.components.PhysicsComponent;
import com.apexleague.game.components.TextureComponent;
import com.apexleague.game.factories.CarFactory;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public final class PlayerEntity {
    private PlayerEntity() {
    }

    public static Entity create(World world, float x, float y, float jumpForce) {
        Body body = CarFactory.createCarBody(world, x, y);

        Entity player = new Entity();
        PhysicsComponent physicsComponent = new PhysicsComponent(body);
        TextureComponent textureComponent = new TextureComponent();
        textureComponent.region = new TextureRegion(new Texture("images/blue_car.png"));
        float bodyWidth = 0.512f * 2f;
        float bodyHeight = 0.896f * 2f;
        textureComponent.scale.set(
            bodyWidth / textureComponent.region.getRegionWidth(),
            bodyHeight / textureComponent.region.getRegionHeight()
        );
        player.add(physicsComponent);
        player.add(textureComponent);
        player.add(new InputComponent());
        player.add(new JumpComponent(jumpForce));
        body.setUserData(physicsComponent);
        return player;
    }

    public static Entity create(World world, float x, float y, float jumpForce, TextureRegion textureRegion, int playerId) {
        Body body = CarFactory.createCarBody(world, x, y);

        Entity player = new Entity();
        PhysicsComponent physicsComponent = new PhysicsComponent(body);
        TextureComponent textureComponent = new TextureComponent();
        textureComponent.region = textureRegion;
        float bodyWidth = 0.512f * 2f;
        float bodyHeight = 0.896f * 2f;
        textureComponent.scale.set(
            bodyWidth / textureComponent.region.getRegionWidth(),
            bodyHeight / textureComponent.region.getRegionHeight()
        );
        InputComponent inputComponent = new InputComponent();
        inputComponent.playerId = playerId;
        player.add(physicsComponent);
        player.add(textureComponent);
        player.add(inputComponent);
        player.add(new JumpComponent(jumpForce));
        body.setUserData(physicsComponent);
        return player;
    }
}
