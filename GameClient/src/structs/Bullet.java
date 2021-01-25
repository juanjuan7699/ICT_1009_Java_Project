package structs;

enum BulletType { //how the bullet is going to travel
    PROJECTILE_BULLET,
    PROJECTILE_SHOTGUN, //multi projectile
    HITSCAN_BULLET,
    HITSCAN_SHOTGUN,
    MISSILE_BULLET,
    LASER_BULLET,
    GRENADE,
}

enum TraceType { //what kind of aoe damage it is going to deal after travelling to the location
    SINGLE, //single enemy only, line trace
    CIRCLE, //multi enemy, explosives like grenades
    CONE, //cone shaped trace, basically 1/nth of a circle
    FRONT_REC,
    CHAINED, //chain to the nearest enemy like chain lightning
}

public class Bullet extends Entity {
    private BulletType bulletType;
    private TraceType traceType;

    private float finalDamage; //this.damage from Entity + (instigator)player.damage only when shooting
    private float radius; //only for non TraceType.SINGLE traces
    private float velocity; //speed of non hitscan/laser bullets
    private float damageOverTime; // >1 if you want to deal baseDamage/damageOverTime damage every second instead of instant

    private boolean activateOnCollision; //activates on collision with another object
    private boolean activateAfterRange; //or/and activate once its reached its max range
    private boolean hitsAllies; //something like ana's heal/damage gun from overwatch

    public Bullet() {
        super(EntityType.BULLET_ENTITY);
    } //base bullet, can extend to be maybe missles, grenades, etc

    public void tryHit(Entity instigator, Entity target) { //on collision do this
        if (target.getEntityType() != EntityType.ANIMAL_ENTITY || target.getEntityType() != EntityType.PLAYER_ENTITY) {
            //deal damage here
            this.finalDamage = this.getDamage() + instigator.getDamage();
            target.updateHealth(-this.finalDamage);
            //also check if its damageovertime etc
            //check if its an ally, etc
        }
    }

    //TODO: for cloning the same bullet everytime the player shoots
    public Bullet shootAgain() {
        return this; //change to cloned instead
    }

    public BulletType getBulletType() {
        return bulletType;
    }

    public void setBulletType(BulletType bulletType) {
        this.bulletType = bulletType;
    }

    public TraceType getTraceType() {
        return traceType;
    }

    public void setTraceType(TraceType traceType) {
        this.traceType = traceType;
    }

    public float getFinalDamage() {
        return finalDamage;
    }

    public void setFinalDamage(float finalDamage) {
        this.finalDamage = finalDamage;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public float getDamageOverTime() {
        return damageOverTime;
    }

    public void setDamageOverTime(float damageOverTime) {
        this.damageOverTime = damageOverTime;
    }

    public boolean isActivateOnCollision() {
        return activateOnCollision;
    }

    public void setActivateOnCollision(boolean activateOnCollision) {
        this.activateOnCollision = activateOnCollision;
    }

    public boolean isActivateAfterRange() {
        return activateAfterRange;
    }

    public void setActivateAfterRange(boolean activateAfterRange) {
        this.activateAfterRange = activateAfterRange;
    }

    public boolean isHitsAllies() {
        return hitsAllies;
    }

    public void setHitsAllies(boolean hitsAllies) {
        this.hitsAllies = hitsAllies;
    }
}
