package structs;

import enums.BulletType;
import enums.TraceType;
import maths.GVector;

public class Player extends Entity {

    private boolean canAttack;
    private Bullet currentWeapon; //current weapon, respawn(clone) this every time you shoot !!DO NOT CLONE LASERS EVERY TICK!!

    public Player() { //probably add spawn location etc
        super(EntityType.PLAYER_ENTITY); //generate default player
        this.setAttackSpeed(999);
        this.setDamage(1000);
        this.setMovable(true);
        this.setVisible(true);
        this.setCollision(true);
        this.canAttack = true;

        //base wepaon
        this.currentWeapon = new Bullet();
        currentWeapon.setBulletType(BulletType.PROJECTILE_BULLET);
        currentWeapon.setTraceType(TraceType.SINGLE);
        currentWeapon.setDamage(100);
        currentWeapon.setVelocity(new GVector(10, 0)); //generic
    }

    public void tryAttack() { //attack stuff here ettc etc
        if (canAttack) {
            System.out.println("a attack");
        }
        else {
            System.out.println("cannot attack");
        }
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public boolean isCanAttack() {
        return this.canAttack;
    }
}
