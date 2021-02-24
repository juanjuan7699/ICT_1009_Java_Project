package com.ict1009.ahg.gameplay;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.ict1009.ahg.screens.GameScreen;

import static com.ict1009.ahg.screens.GameScreen.*;

public class Laser extends Entity {

    private Entity owner;
    private int team; //0 = players, 1 = animals

    public Laser(Entity owner, int team) {
        this.owner = owner;
        this.team = team;
        this.setMovementSpeed(460);
        this.setDamageScale(6);
        this.setSprite(newPlayerTextures[2]); //defaults

        this.setBoundingBox(new Rectangle(owner.getBoundingBox().x + owner.getBoundingBox().width *.72f, owner.getBoundingBox().y + owner.getBoundingBox().height *.98f,1,4));
    }

    public void applyOnHit(Entity target) {

    }

    @Override
    public void tryMove(int direction) { //up or down?

    }

    @Override
    public void tryTeleport(Vector3 targetLocation) {

    }

    @Override
    public void addToRenderQueue() {
        synchronized (renderQueue) {
            GameScreen.renderQueue.add(this);
        }
    }

    @Override
    public void onDestroy(Entity instigator) {

    }

    @Override
    public void update(float deltaTime) {

    }

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    @Override
    public int getTeam() {
        return team;
    }

    @Override
    public void setTeam(int team) {
        this.team = team;
    }
}
