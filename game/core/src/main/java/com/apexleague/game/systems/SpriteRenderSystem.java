package com.apexleague.game.systems;

import com.apexleague.game.components.PhysicsComponent;
import com.apexleague.game.components.TextureComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;

public class SpriteRenderSystem extends IteratingSystem {
    private final SpriteBatch batch;
    private final ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    private final ComponentMapper<TextureComponent> textureMapper = ComponentMapper.getFor(TextureComponent.class);

    public SpriteRenderSystem(SpriteBatch batch) {
        super(Family.all(PhysicsComponent.class, TextureComponent.class).get());
        this.batch = batch;
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        super.addedToEngine(engine);
    }

    @Override
    public void update(float deltaTime) {
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physics = physicsMapper.get(entity);
        TextureComponent texture = textureMapper.get(entity);
        TextureRegion region = texture.region;
        if (region == null) {
            return;
        }

        Body body = physics.body;
        float width = region.getRegionWidth() * texture.scale.x;
        float height = region.getRegionHeight() * texture.scale.y;
        float x = body.getPosition().x - width * 0.5f;
        float y = body.getPosition().y - height * 0.5f;
        float originX = width * 0.5f;
        float originY = height * 0.5f;
        float rotation = body.getAngle() * MathUtils.radiansToDegrees;

        batch.draw(region, x, y, originX, originY, width, height, 1f, 1f, rotation);
    }
}
