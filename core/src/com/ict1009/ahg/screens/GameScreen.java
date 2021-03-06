package com.ict1009.ahg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ict1009.ahg.AnimalHunter;
import com.ict1009.ahg.enums.StatusType;
import com.ict1009.ahg.gameplay.*;
import com.ict1009.ahg.interfaces.ISpawnPoint;

import java.util.*;

public class GameScreen implements Screen {

    private static TextureRegion bossTexture;
    private static TextureRegion animalTexture;
    private final AnimalHunter game;

    /**Screen**/
    private final Camera camera;
    private final Viewport viewport;

    /** World **/
    public static final float WORLD_WIDTH = 72;
    public static final float WORLD_HEIGHT = 128;

    private final SpriteBatch batch;
    public static final TextureAtlas textureAtlas = new TextureAtlas("images.atlas");
    public static final TextureAtlas gunsTextureAtlas = new TextureAtlas("guns.atlas");
    public static final TextureAtlas animalTextureAtlas = new TextureAtlas("animals.atlas");
    public static final TextureAtlas potionsTextureAtlas = new TextureAtlas("potions.atlas");
    public static final TextureAtlas newPlayerTextureAtlas = new TextureAtlas("playerAssets.atlas");
    public static final TextureAtlas backgroundTextureAtlas = new TextureAtlas("backgrounds.atlas");

    public static final Texture onHitTexture = new Texture("testblood.png");         //100 by 100
    public static final Texture explosionTexture = new Texture("bloodsprite3.png");     //480 by 480
    public static final Texture onHitSwarmTexture = new Texture("SwarmLaserSprite.png");    //64 by 64
    public static final Texture onHitStasisTexture = new Texture("IceCastSprite.png");  //96 by 96
    public static final Texture onHitGenericTexture = new Texture("GenericLaserSprite.png"); //96 by 96
    private final TextureRegion[] backgrounds;

    /**Timing**/
    private final float[] backgroundOffsets = {0, 0, 0, 0};

    private final float timeBetweenNewMap = 15;
    private final float timeBetweenDamage = 0.2f;
    private final float backgroundMaxScrollingSpeed = WORLD_HEIGHT / 4;

    private float mapTimer = 0;
    private float damageTimer1 = 0;
    private float damageTimer2 = 0;
    private float enemySpawnTimer = 0;
    private float timeBetweenEnemySpawns = 2.45f;

    /** Render Queue **/
    public static List<Animal> mobs; //enemies
    public static List<Player> players; //all players goes here
    public static List<Entity> renderQueue; //all simple rendered stuff here, short lived stuff only
    public static List<OnHitAndExplosion> onHitAndExplosionList;

    /** spawners  **/
    private static ISpawnPoint pickupSpawner;

    //gs
    public static int level = 1;
    public static int score = 0;

    private int maxMobs = 10;
    private int spawnPerCycle = 1;

    private boolean bossSpawned = false;

    // Sound Effects
    private Music music;

    //HUD
    BitmapFont font;
    BitmapFont font2;

    float hudLeftX;
    float hudRow1Y;
    float hudRow2Y;
    float hudRightX;
    float hudSectionWidth;
    float hudVerticalMargin;

    public static final Random generator = new Random();

    public static TextureRegion[] gunTextures;
    public static TextureRegion[] playerTextures;
    public static TextureRegion[] potionTextures;
    public static TextureRegion[] newPlayerTextures;
    public static TextureRegion[] animalSnowTextures;
    public static TextureRegion[] animalRockTextures;
    public static TextureRegion[] animalBossTextures;
    public static TextureRegion[] animalForestTextures;
    public static TextureRegion[] animalDesertTextures;
    
    public GameScreen(AnimalHunter game) {
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        backgrounds = new TextureRegion[] {
                textureAtlas.findRegion("grassBackground2"),
                textureAtlas.findRegion("grassBackground2"),
                textureAtlas.findRegion("grassBackground2"),
                textureAtlas.findRegion("grassBackground2")};

        playerTextures = new TextureRegion[] {
                textureAtlas.findRegion("soldier1_gun"),
                textureAtlas.findRegion("manBlue_gun"),
                textureAtlas.findRegion("laserBlue12"),
                textureAtlas.findRegion("laserOrange12"),
                textureAtlas.findRegion("laserRed12")};


        // New player textures
        newPlayerTextures = new TextureRegion[] {
                newPlayerTextureAtlas.findRegion("manBlue_redGun"),
                newPlayerTextureAtlas.findRegion("soldier1_redGun"),

                // Laser textures
                newPlayerTextureAtlas.findRegion("laserOrange12"),
                newPlayerTextureAtlas.findRegion("laserPurple12"),
                newPlayerTextureAtlas.findRegion("laserBlue12"),
                newPlayerTextureAtlas.findRegion("laserRed12"),
                newPlayerTextureAtlas.findRegion("laserYellow12"),
                newPlayerTextureAtlas.findRegion("laserGreen12"),

                // Player 0 with guns textures
                newPlayerTextureAtlas.findRegion("manBlue_greenGun"),
                newPlayerTextureAtlas.findRegion("manBlue_yellowGun"),
                newPlayerTextureAtlas.findRegion("manBlue_purpleGun"),
                newPlayerTextureAtlas.findRegion("manBlue_blueGun"),
                newPlayerTextureAtlas.findRegion("manBlue_orangeGun"),

                // Player 1 with guns textures
                newPlayerTextureAtlas.findRegion("soldier1_greenGun"),
                newPlayerTextureAtlas.findRegion("soldier1_yellowGun"),
                newPlayerTextureAtlas.findRegion("soldier1_purpleGun"),
                newPlayerTextureAtlas.findRegion("soldier1_blueGun"),
                newPlayerTextureAtlas.findRegion("soldier1_orangeGun"),};

        // Animal textures
        animalForestTextures = new TextureRegion[]{
                animalTextureAtlas.findRegion("bear"),
                animalTextureAtlas.findRegion("elephant")};

        animalDesertTextures = new TextureRegion[]{
                animalTextureAtlas.findRegion("lion"),
                animalTextureAtlas.findRegion("camel")};

        animalSnowTextures = new TextureRegion[]{
                animalTextureAtlas.findRegion("wolf"),
                animalTextureAtlas.findRegion("deer")};

        animalRockTextures = new TextureRegion[]{
                animalTextureAtlas.findRegion("goat"),
                animalTextureAtlas.findRegion("dinosaur")};

        // Boss textures
        animalBossTextures = new TextureRegion[]{
                animalTextureAtlas.findRegion("chimera"),
                animalTextureAtlas.findRegion("werewolf"),
                animalTextureAtlas.findRegion("yeti")};

        // Potion textures
        potionTextures = new TextureRegion[]{
                potionsTextureAtlas.findRegion("potion1"),
                potionsTextureAtlas.findRegion("potion2"),
                potionsTextureAtlas.findRegion("potion3"),
                potionsTextureAtlas.findRegion("potion4")};

        // Gun textures
        gunTextures = new TextureRegion[]{
                gunsTextureAtlas.findRegion("yellow_gun"),
                gunsTextureAtlas.findRegion("orange_gun"),
                gunsTextureAtlas.findRegion("darkblue_gun"),
                gunsTextureAtlas.findRegion("purple_gun"),
                gunsTextureAtlas.findRegion("green_gun"),
                gunsTextureAtlas.findRegion("red_gun")};

        mobs = new ArrayList<>();
        players = new ArrayList<>();
        onHitAndExplosionList = new ArrayList<>();
        renderQueue = Collections.synchronizedList(new ArrayList<Entity>());
        batch = new SpriteBatch();
        prepareHud();

        for (int i = 0; i < 2; i++) { //set to amount of players
            Player player = new Player(i);
            player.addToRenderQueue(); //send to render
            player.startAttacking(); //request to start attacking
        }

        pickupSpawner = new PickupSpawner();
        pickupSpawner.startSpawning();

        try{
            music = Gdx.audio.newMusic(Gdx.files.internal("across_the_valley.ogg"));
            music.setVolume(0.2f);
            music.setLooping(true);
            music.play();
        }catch (RuntimeException e){
            System.out.println("Music file not found: " + e);
        }

        this.game = game;
    }

    private void prepareHud() {
        //Create a BitmapFont from our font file
        try{
            //Font for HUD
            FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("test.otf"));
            FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

            fontParameter.size = 72;
            fontParameter.borderWidth = 3.6f;
            fontParameter.color = new Color (1,1,1,0.3f);
            fontParameter.borderColor = new Color(0,0,0,0.3f);

            font = fontGenerator.generateFont(fontParameter);

            //Scale the font to fit world
            font.getData().setScale(0.08f);

            //Font for player
            FreeTypeFontGenerator.FreeTypeFontParameter fontParameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();

            fontParameter2.size = 50;
            fontParameter2.borderWidth = 3.6f;
            fontParameter2.color = new Color (0,255,0,1f);
            fontParameter2.borderColor = new Color(0,0,0,0.3f);

            font2 = fontGenerator.generateFont(fontParameter2);

            //Scale the font to fit world
            font2.getData().setScale(0.08f);
        }catch (RuntimeException e){
            System.out.println("Font file not found: " + e);
        }

        //Calculate hud margins, etc
        hudVerticalMargin = font.getCapHeight() / 2;
        hudLeftX = hudVerticalMargin;
        hudRightX = WORLD_WIDTH * 2 / 3 - hudLeftX;
        hudRow1Y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2Y = WORLD_HEIGHT  - hudVerticalMargin * 1.5f- font.getCapHeight();
        hudSectionWidth = WORLD_WIDTH / 3;
    }

    public static TextureRegion getAnimalTexture() {
        return animalTexture;
    }

    public static TextureRegion getBossTexture() {
        return bossTexture;
    }

    @Override
    public void render(float deltaTime) { //render method should only have rendering stuff, spawning should be other area
        batch.begin();

        //Scrolling background
        renderBackground(deltaTime);
        detectInput(deltaTime);

        //move out from render q
        spawnEnemyAnimals(deltaTime);//change to spawnpoint

        //player renderer queue
        for (Player player : players) {
            player.update(deltaTime);
            player.draw(batch);
        }

        //animal renderer queue
        for (Animal mob : mobs) {
            moveEnemy(mob, deltaTime); //move to animal AI
            mob.update(deltaTime);
            mob.draw(batch);
        }

        synchronized (renderQueue) {
            //misc rendering
            for (Entity entity : renderQueue) {
                entity.update(deltaTime);
                entity.draw(batch);
            }

            removePending(deltaTime); //removing old lasers only
            detectCollisions(deltaTime);
        }



        updateLevel(deltaTime);

        updateAndRenderOnHitAndExplosions(deltaTime);
        updateAndRenderHUD();

        getHealth();

        if (players.get(0).hasStatus(StatusType.DOWNED) && players.get(1).hasStatus(StatusType.DOWNED)) {
            music.dispose();
            game.setScreen(new EndScreen(game));
        }

        batch.end();
    }

    public static void gameReset() {
        for (Player p : players) {
            p.attackTimer.cancel();
        }

        pickupSpawner.destroySpawner();
        level = 1;
        score = 0;
    }

    private void getHealth(){

        if (players.get(0).getCurrentHealth() == 0 && players.get(1).getCurrentHealth() == 0) {
            music.dispose();
            game.setScreen(new EndScreen(game));
        }
    }

    private void updateLevel(float deltaTime){
        mapTimer += deltaTime;

        if(mapTimer > timeBetweenNewMap){
            level += 1;
            bossSpawned = false;
            timeBetweenEnemySpawns = (float) Math.max(0.9, timeBetweenEnemySpawns * 0.9);
            maxMobs = Math.min(40, maxMobs+1); //hard cap 40 mobs
            spawnPerCycle = Math.min(4, 1 + level/30); //increase mob spawn rate per level, max 4 per spawn cycle
            mapTimer -= timeBetweenNewMap;
        }

        if (level % 4 == 0) { //stop flashing for now
            backgrounds[0] = backgroundTextureAtlas.findRegion("grassBackground2");
            backgrounds[1] = backgroundTextureAtlas.findRegion("grassBackground2");
            backgrounds[2] = backgroundTextureAtlas.findRegion("grassBackground2");
            backgrounds[3] = backgroundTextureAtlas.findRegion("grassBackground2");
        } else if (level % 4 == 1) {
            backgrounds[0] = backgroundTextureAtlas.findRegion("desertBackground");
            backgrounds[1] = backgroundTextureAtlas.findRegion("desertBackground");
            backgrounds[2] = backgroundTextureAtlas.findRegion("desertBackground");
            backgrounds[3] = backgroundTextureAtlas.findRegion("desertBackground");
        } else if (level % 4 == 2) {
            backgrounds[0] = backgroundTextureAtlas.findRegion("snowBackground");
            backgrounds[1] = backgroundTextureAtlas.findRegion("snowBackground");
            backgrounds[2] = backgroundTextureAtlas.findRegion("snowBackground");
            backgrounds[3] = backgroundTextureAtlas.findRegion("snowBackground");
        } else {
            backgrounds[0] = backgroundTextureAtlas.findRegion("rockBackground");
            backgrounds[1] = backgroundTextureAtlas.findRegion("rockBackground");
            backgrounds[2] = backgroundTextureAtlas.findRegion("rockBackground");
            backgrounds[3] = backgroundTextureAtlas.findRegion("rockBackground");
        }
    }

    private void updateAndRenderHUD() {
        //render top row labels
        font.draw(batch, "Score", hudLeftX, hudRow1Y, hudSectionWidth, Align.left, false);
        font.draw(batch, "Level", hudRightX, hudRow1Y, hudSectionWidth, Align.right, false);
        //render second row values
        font.draw(batch, String.format(Locale.getDefault(), "%06d", score), hudLeftX, hudRow2Y, hudSectionWidth, Align.left, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", level), hudRightX, hudRow2Y, hudSectionWidth, Align.right, false);

        if (players.get(0).hasStatus(StatusType.DOWNED)) {
            font2.draw(batch, "DOWNED", players.get(0).getBoundingBox().x - 6, players.get(0).getBoundingBox().y, hudSectionWidth, Align.center, false);
        }
        else {
            font2.draw(batch, String.format(Locale.getDefault(), "%.0f/%.0f", players.get(0).getCurrentHealth(), players.get(0).getMaxHealth()), players.get(0).getBoundingBox().x - 6, players.get(0).getBoundingBox().y, hudSectionWidth, Align.center, false);
        }

        if (players.get(1).hasStatus(StatusType.DOWNED)) {
            font2.draw(batch, "DOWNED", players.get(1).getBoundingBox().x - 6, players.get(1).getBoundingBox().y, hudSectionWidth, Align.center, false);
        }
        else {
            font2.draw(batch, String.format(Locale.getDefault(), "%.0f/%.0f", players.get(1).getCurrentHealth(), players.get(1).getMaxHealth()), players.get(1).getBoundingBox().x - 6, players.get(1).getBoundingBox().y, hudSectionWidth, Align.center, false);
        }
    }

    private void spawnEnemyAnimals(float deltaTime) {
        enemySpawnTimer += deltaTime;

        int randomIndex = generator.nextInt(animalForestTextures.length);
        if (enemySpawnTimer > timeBetweenEnemySpawns && mobs.size() < maxMobs) {
            if (level % 4 == 0) {
                animalTexture = animalForestTextures[randomIndex];
            } else if (level % 4 == 1) {
                animalTexture = animalDesertTextures[randomIndex];
            } else if (level % 4 == 2) {
                animalTexture = animalSnowTextures[randomIndex];
            } else {
                animalTexture = animalRockTextures[randomIndex];
            }

            if (level % 5 == 0) {
                if (level % 4 == 0) {
                    bossTexture = animalBossTextures[0];
                }
                else if (level % 4 == 1) {
                    bossTexture = animalBossTextures[0];
                }
                else if (level % 4 == 2) {
                    bossTexture = animalBossTextures[2];
                }
                else {
                    bossTexture = animalBossTextures[1];
                }
            }

            for (int i = 0; i < spawnPerCycle; i++) {
                new Animal().addToRenderQueue();

                if (level % 5 == 0 && !bossSpawned) {
                    new Boss().addToRenderQueue();
                    bossSpawned = true;
                }
            }
            enemySpawnTimer -= timeBetweenEnemySpawns;
        }
    }


    private void detectInput(float deltaTime) {
        // Keyboard Input

        // Strategy: determine the max distance the player can move
        // Check each key that matters and move accordingly

        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -players.get(0).getBoundingBox().x;
        downLimit = -players.get(0).getBoundingBox().y;
        rightLimit = WORLD_WIDTH - players.get(0).getBoundingBox().x - players.get(0).getBoundingBox().width;
        upLimit = WORLD_HEIGHT - players.get(0).getBoundingBox().y - players.get(0).getBoundingBox().height;

        float leftLimit2, rightLimit2, upLimit2, downLimit2;
        leftLimit2 = -players.get(1).getBoundingBox().x;
        downLimit2 = -players.get(1).getBoundingBox().y;
        rightLimit2 = WORLD_WIDTH - players.get(1).getBoundingBox().x - players.get(1).getBoundingBox().width;
        upLimit2 = WORLD_HEIGHT - players.get(1).getBoundingBox().y - players.get(1).getBoundingBox().height;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightLimit > 0) {
            players.get(0).translate(Math.min(players.get(0).getMovementSpeed() * deltaTime, rightLimit), 0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) && upLimit > 0) {
            players.get(0).translate(0f, Math.min(players.get(0).getMovementSpeed() * deltaTime, upLimit));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit < 0) {
            players.get(0).translate(Math.max(-players.get(0).getMovementSpeed() * deltaTime, leftLimit), 0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && downLimit < 0) {
            players.get(0).translate(0f, Math.max(-players.get(0).getMovementSpeed() * deltaTime, downLimit));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) { //revive and heal
            if (players.get(0).collisionTest(players.get(1)) && players.get(1).hasStatus(StatusType.DOWNED)) {
                players.get(1).removeStatus(StatusType.DOWNED);
                players.get(1).addStatus(StatusType.ALIVE);
                players.get(1).modifyHealth(25);
                players.get(1).resetBuffs();
            }
        }

        //switch weapon
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
            players.get(0).setWeapon(0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
            players.get(0).setWeapon(1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_6)) {
            players.get(0).setWeapon(2);
        }

        //Player 2
        if (Gdx.input.isKeyPressed(Input.Keys.D) && rightLimit2 > 0) {
            players.get(1).translate(Math.min(players.get(1).getMovementSpeed() * deltaTime, rightLimit2), 0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) && upLimit2 > 0) {
            players.get(1).translate(0f, Math.min(players.get(1).getMovementSpeed() * deltaTime, upLimit2));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) && leftLimit2 < 0) {
            players.get(1).translate(Math.max(-players.get(1).getMovementSpeed() * deltaTime, leftLimit2), 0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S) && downLimit2 < 0) {
            players.get(1).translate(0f, Math.max(-players.get(1).getMovementSpeed() * deltaTime, downLimit2));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) { //revive and heal
            if (players.get(1).collisionTest(players.get(0)) && players.get(0).hasStatus(StatusType.DOWNED)) {
                players.get(0).removeStatus(StatusType.DOWNED);
                players.get(0).addStatus(StatusType.ALIVE);
                players.get(0).modifyHealth(25);
                players.get(0).resetBuffs();
            }
        }

        //switch weapon
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            players.get(1).setWeapon(0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            players.get(1).setWeapon(1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            players.get(1).setWeapon(2);
        }
    }

    private void moveEnemy(Animal enemyAnimal, float deltaTime) {
        // Strategy: determine the max distance the enemy can move

        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -enemyAnimal.getBoundingBox().x;
        downLimit = -enemyAnimal.getBoundingBox().y;
        rightLimit = WORLD_WIDTH - enemyAnimal.getBoundingBox().x - enemyAnimal.getBoundingBox().width;
        upLimit = WORLD_HEIGHT - enemyAnimal.getBoundingBox().y - enemyAnimal.getBoundingBox().height;

        float xMove = enemyAnimal.getDirectionVector().x * enemyAnimal.getMovementSpeed() * deltaTime;
        float yMove = enemyAnimal.getDirectionVector().y * enemyAnimal.getMovementSpeed() * deltaTime;

        if (xMove > 0) xMove = Math.min(xMove, rightLimit);
        else xMove = Math.max(xMove,leftLimit);

        if (yMove > 0) yMove = Math.min(yMove, upLimit);
        else yMove = Math.max(yMove,downLimit);

        enemyAnimal.translate(xMove,yMove);
    }

    private void detectCollisions(float deltaTime){
        //Check if player1 laser intersects animal
        damageTimer1 += deltaTime; //player1 iframe
        damageTimer2 += deltaTime; //player2 iframe

        //Collision test with animals
        try {
            for (Entity entity : renderQueue) {
                if (entity instanceof Laser) {
                    Laser laser = (Laser) entity;

                    for (Animal enemyAnimal : mobs) {
                        if (enemyAnimal.collisionTest(laser)) {
                            // Touches animal
                            enemyAnimal.takeDamage(laser.getDamageScale() * laser.getOwner().getDamageScale(), 0, laser.getOwner());
                            laser.applyOnHit(enemyAnimal);
                            laser.setPendingRemoval(!(laser instanceof NovaLaser)); //nova lasers dont get removed on hit
                            break;
                        }
                    }
                } else if (entity instanceof Pickup) {
                    for (Player p : players) {
                        if (p.collisionTest(entity)) {
                            entity.onDestroy(p);
                        }
                    }
                }
            }

            ListIterator<Animal> enemyAnimalListIterator = mobs.listIterator();
            while (enemyAnimalListIterator.hasNext()) {
                Animal enemyAnimal = enemyAnimalListIterator.next();
                if (enemyAnimal.collisionTest(players.get(0))) {
                    if (damageTimer1 > timeBetweenDamage) {
                        players.get(0).takeDamage(enemyAnimal.getDamageScale(), 0, enemyAnimal);
                        damageTimer1 = 0;
                    }
                }
            // Player 2 takes damage from enemy hitbox
                if (enemyAnimal.collisionTest(players.get(1))) {
                    if (damageTimer2 > timeBetweenDamage) {
                        players.get(1).takeDamage(enemyAnimal.getDamageScale(), 0, enemyAnimal);
                        damageTimer2 = 0;
                    }
                }

                if (enemyAnimal.isPendingRemoval()) {
                    enemyAnimalListIterator.remove();
                    if (enemyAnimal instanceof Boss) { //boss killed, immediately go next level
                        bossSpawned = false;
                        level +=1;
                    }
                }
            }

        }
        catch (Exception e) {
            System.out.println("cannot detect collision now, trying later");
        }
    }

    private void updateAndRenderOnHitAndExplosions(float deltaTime) {
        try {
            ListIterator<OnHitAndExplosion> onHitandExplosionListIterator = onHitAndExplosionList.listIterator();
            while (onHitandExplosionListIterator.hasNext()){
                OnHitAndExplosion onHitAndExplosion = onHitandExplosionListIterator.next();
                onHitAndExplosion.update(deltaTime);
                if(onHitAndExplosion.isFinished()){
                    onHitandExplosionListIterator.remove();
                }
                else {
                    onHitAndExplosion.draw(batch);
                }
            }
        }

        catch (Exception e) {
            System.out.println("cannot render explosions now, try again later");
        }

    }

    private void removePending(float deltaTime) {
        try {
            ListIterator<Entity> iterator = renderQueue.listIterator();
            while(iterator.hasNext()) {
                Entity entity = iterator.next();
                if (entity.isPendingRemoval()) {
                    iterator.remove();
                    continue;
                }

                if (entity instanceof Laser) {
                    Laser laser = (Laser)entity;
                    laser.draw(batch);
                    laser.getBoundingBox().y += laser.getMovementSpeed()*deltaTime;

                    if (laser.getBoundingBox().y + laser.getBoundingBox().height < 0) {
                        laser.setPendingRemoval(true);
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println("Cannot modify right now, trying later");
        }

    }

    private void renderBackground(float deltaTime) {

        backgroundOffsets[0] += deltaTime * backgroundMaxScrollingSpeed / 8;
        backgroundOffsets[1] += deltaTime * backgroundMaxScrollingSpeed / 4;
        backgroundOffsets[2] += deltaTime * backgroundMaxScrollingSpeed / 2;
        backgroundOffsets[3] += deltaTime * backgroundMaxScrollingSpeed;

        for (int layer = 0; layer < backgroundOffsets.length; layer++) {
            if (backgroundOffsets[layer] > WORLD_HEIGHT) {
                backgroundOffsets[layer] = 0;
            }

            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer], WORLD_WIDTH, WORLD_HEIGHT);
            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer] + WORLD_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public void dispose() {

    }
}
