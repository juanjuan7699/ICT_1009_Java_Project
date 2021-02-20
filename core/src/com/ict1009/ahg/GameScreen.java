package com.ict1009.ahg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ict1009.ahg.gameplay.*;

import java.util.*;

public class GameScreen implements Screen {

    /**Screen**/
    private final Camera camera;
    private final Viewport viewport;

    /** World **/
    public static final float WORLD_WIDTH = 72;
    public static final float WORLD_HEIGHT = 128;

    /**Graphics**/
    private float backgroundHeight; //  of background in world units

    private final SpriteBatch batch;
    public static final TextureAtlas textureAtlas = new TextureAtlas("images.atlas");

    public static Texture explosionTexture;
    private TextureRegion[] backgrounds;
    private TextureRegion playerTextureRegion, player2TextureRegion, bearTextureRegion, crocTextureRegion, duckTextureRegion, goatTextureRegion,
            laserTextureRegion, laser2TextureRegion,enemyLaserTextureRegion, pigTextureRegion, rabbitTextureRegion, snakeTextureRegion,
            elephantTextureRegion, lionTextureRegion, gorillaTextureRegion, camelTextureRegion;


    /**Timing**/
    private final float[] backgroundOffsets = {0, 0, 0, 0};
    private final float backgroundMaxScrollingSpeed;
    private float timeBetweenEnemySpawns = 3;
    private float timeBetweenDamage = 2;
    private float timeBetweenNewMap = 30;
    private float enemySpawnTimer = 0;
    private float damageTimer = 0;
    private float timeElapsed = 0;

    /** Render Queue **/
    public static List<Player> players; //all players goes here
    public static List<Entity> renderQueue; //all simple rendered stuff here, short lived stuff only
    public static List<Animal> mobs; //enemies
    public static List<Explosion> explosionList;

    //gs
    public static int level = 0;
    public static int score = 0;
    public static int levelScore = 0;

    // Sound Effects
    private Sound sound;
    private Music music;

    /*HUD only shows player1, maybe if more players means combine scoregains, lives etc?*/
    BitmapFont font;
    float hudVerticalMargin, hudLeftX, hudRightX, hudCentreX, hudRow1Y, hudRow2Y, hudSectionWidth;

     /*if want to remove animal laser, gameobjects enemylaserlist, enemylaser linkedlist,
    animals variables(class and gamescreen), detectCollisions enemylist, renderlasers 2 lists)  */

    public static Random generator = new Random();
    public static TextureRegion[] animalForestTextures;
    public static TextureRegion[] animalDesertTextures;

    GameScreen() {
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        backgrounds = new TextureRegion[4];
        backgrounds[0] = textureAtlas.findRegion("grassBackground2");
        backgrounds[1] = textureAtlas.findRegion("grassBackground2");
        backgrounds[2] = textureAtlas.findRegion("grassBackground2");
        backgrounds[3] = textureAtlas.findRegion("grassBackground2");

        // Animal textures
        bearTextureRegion = textureAtlas.findRegion("bear2");
        crocTextureRegion = textureAtlas.findRegion("crocodile");
        elephantTextureRegion = textureAtlas.findRegion("elephant");
        lionTextureRegion = textureAtlas.findRegion("lion");
        gorillaTextureRegion = textureAtlas.findRegion("gorilla");
        camelTextureRegion = textureAtlas.findRegion("camel");

        laser2TextureRegion = textureAtlas.findRegion("laserBlue12"); // Change this value if setting player 2 laser to another colour
        enemyLaserTextureRegion = textureAtlas.findRegion("laserOrange12");
        explosionTexture = new Texture("explosion.png");

        backgroundMaxScrollingSpeed = WORLD_HEIGHT / 4;

//        animalTextures = new TextureRegion[]{bearTextureRegion, elephantTextureRegion, lionTextureRegion, gorillaTextureRegion, camelTextureRegion};
        animalForestTextures = new TextureRegion[]{bearTextureRegion, elephantTextureRegion};
        animalDesertTextures = new TextureRegion[]{lionTextureRegion, camelTextureRegion};


        //1f, 4, 120, .35f //laser data
        mobs = new ArrayList<>();
        renderQueue = new ArrayList<>();
        players = new ArrayList<>();
        explosionList = new ArrayList<>();

        for (int i = 0; i < 2; i++) { //set to amount of players
            new Player().addToRenderQueue(); //temporary
        }

        batch = new SpriteBatch();

        prepareHud();

        try{
            music = Gdx.audio.newMusic(Gdx.files.internal("across_the_valley.ogg"));
            music.setVolume(0.2f);
            music.setLooping(true);
            music.play();
        }catch (RuntimeException e){
            System.out.println("Music file not found: " + e);
        }

    }

    private void prepareHud() {
        //Create a BitmapFont from our font file
        // FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("TheFoxTailRegular.otf")); //doesnt work
        try{
            FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("test.otf"));
            // FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("killerblack.otf")); //lives doesnt show negative
            FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

            fontParameter.size = 72;
            fontParameter.borderWidth = 3.6f;
            fontParameter.color = new Color (1,1,1,0.3f);
            fontParameter.borderColor = new Color(0,0,0,0.3f);

            font = fontGenerator.generateFont(fontParameter);

            //Scale the font to fit world
            font.getData().setScale(0.08f);
        }catch (RuntimeException e){
            System.out.println("Font file not found: " + e);
        }

        //Calculate hud margins, etc
        hudVerticalMargin = font.getCapHeight() / 2;
        hudLeftX = hudVerticalMargin;
        hudRightX = WORLD_WIDTH * 2 / 3 - hudLeftX;
        hudCentreX = WORLD_WIDTH / 3;
        hudRow1Y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2Y = WORLD_HEIGHT  - hudVerticalMargin * 1.5f- font.getCapHeight();
        hudSectionWidth = WORLD_WIDTH / 3;
    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        //Scrolling background
        renderBackground(deltaTime);
        detectInput(deltaTime);

        //player renderer queue
        for (Player player : players) {
            player.update(deltaTime);
            player.draw(batch);
        }

        spawnEnemyAnimals(deltaTime);//change to spawnpoint

        //animal renderer queue
        for (Animal mob : mobs) {
            moveEnemy(mob, deltaTime);
            mob.update(deltaTime);
            mob.draw(batch);
        }

        renderLasers(deltaTime);
        detectCollisions(deltaTime);

        updateLevel();

        // Explosions
        updateAndRenderExplosions(deltaTime);
        //hud rendering
        updateAndRenderHUD();

        batch.end();
    }

    private void updateLevel(){
        if(levelScore /  1000  == 1){
            levelScore = 0;
            level += 1;
        }

        if (level % 2 == 0) {
            backgrounds[0] = textureAtlas.findRegion("grassBackground2");
            backgrounds[1] = textureAtlas.findRegion("grassBackground2");
            backgrounds[2] = textureAtlas.findRegion("grassBackground2");
            backgrounds[3] = textureAtlas.findRegion("grassBackground2");
        }else {
            backgrounds[0] = textureAtlas.findRegion("desertBackground");
            backgrounds[1] = textureAtlas.findRegion("desertBackground");
            backgrounds[2] = textureAtlas.findRegion("desertBackground");
            backgrounds[3] = textureAtlas.findRegion("desertBackground");
        }
    }

    private int getLevel(){
        return level;
    }

    private void updateAndRenderHUD(){
        //render top row labels
        font.draw(batch, "Score", hudLeftX, hudRow1Y, hudSectionWidth, Align.left, false);
        font.draw(batch, "Level", hudCentreX, hudRow1Y, hudSectionWidth, Align.center, false);
        font.draw(batch, "Lives", hudRightX, hudRow1Y, hudSectionWidth, Align.right, false);
        //render second row values
        font.draw(batch, String.format(Locale.getDefault(), "%06d", score), hudLeftX, hudRow2Y, hudSectionWidth, Align.left, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", level), hudCentreX, hudRow2Y, hudSectionWidth, Align.center, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02f/%02f", players.get(0).getCurrentHealth(), players.get(0).getMaxHealth()), hudRightX, hudRow2Y, hudSectionWidth, Align.right, false);
    }

    private void spawnEnemyAnimals(float deltaTime) {
        enemySpawnTimer += deltaTime;
        timeElapsed += deltaTime;

        int randomIndex = generator.nextInt(animalForestTextures.length);

        if (enemySpawnTimer > timeBetweenEnemySpawns && mobs.size() < 10) {
            TextureRegion animalTexture;
            if (level % 2 == 0) {
                animalTexture = animalForestTextures[randomIndex];
            } else {
                animalTexture = animalDesertTextures[randomIndex];
            }
            new Animal().addToRenderQueue();
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
//            float xChange = player.movementSpeed * deltaTime;
//            xChange = Math.min(xChange, rightLimit);
//            player.translate(xChange, 0f);

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

        //Player 2
        if (Gdx.input.isKeyPressed(Input.Keys.D) && rightLimit2 > 0) {
//            float xChange = player.movementSpeed * deltaTime;
//            xChange = Math.min(xChange, rightLimit);
//            player.translate(xChange, 0f);

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
        damageTimer += deltaTime;

        ListIterator<Entity> laserListIterator = renderQueue.listIterator();
        while(laserListIterator.hasNext()) {
            Entity entity = laserListIterator.next();
            if (entity instanceof Laser) {
                Laser laser = (Laser)entity;

                ListIterator<Animal> enemyAnimalListIterator = mobs.listIterator();
                while (enemyAnimalListIterator.hasNext()) {
                    Animal enemyAnimal = enemyAnimalListIterator.next();

                    if (enemyAnimal.collisionTest(laser)){
                        // Touches animal
                        enemyAnimal.takeDamage(laser.getDamageScale() * laser.getOwner().getDamageScale(), 0);
                        if(enemyAnimal.isPendingRemoval())
                        {
                            enemyAnimalListIterator.remove();
                        }
                        laserListIterator.remove();
                        break;
                    }

                    // Player 1 takes damage from enemy hitbox
                    if (enemyAnimal.collisionTest(players.get(0))){
                        if (damageTimer > timeBetweenDamage) {
                            players.get(0).takeDamage(enemyAnimal.getDamageScale(), 0);
                            damageTimer = 0;
                        }
                    }

                    // Player 2 takes damage from enemy hitbox
                    if (enemyAnimal.collisionTest(players.get(1))){
                        if (damageTimer > timeBetweenDamage) {
                            players.get(1).takeDamage(enemyAnimal.getDamageScale(), 0);
                            damageTimer -= timeBetweenDamage;
                        }
                    }
                }
            }

        }
        //Check if player2 laser intersects animal
//        ListIterator<Laser> laser2ListIterator = laser2LinkedList.listIterator();
//        while(laser2ListIterator.hasNext()) {
//            Laser laser = laser2ListIterator.next();
//            ListIterator<Animals> enemyAnimalListIterator = enemyAnimalList.listIterator();
//            while (enemyAnimalListIterator.hasNext()) {
//                Animals enemyAnimal = enemyAnimalListIterator.next();
//
//                if (enemyAnimal.intersects(laser.boundingBox)){
//                    // Touches animal
//                    if(enemyAnimal.hitAndCheckKilled(laser))
//                    {
//                        enemyAnimalListIterator.remove();
//                        explosionList.add(
//                                new Explosion(explosionTexture,
//                                        new Rectangle (enemyAnimal.boundingBox),
//                                        0.7f));
//                        //Killed and obtain score
//                        score += 250;
//                        levelScore += 250;
//                    }
//                    laser2ListIterator.remove();
//                    break;
//                }
//
//                if (enemyAnimal.intersects(player2.boundingBox)){
//                    player2.lives --;
//                }
//            }
//        }
    }

    private void updateAndRenderExplosions(float deltaTime){
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while (explosionListIterator.hasNext()){
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTime);
            if(explosion.isFinished()){
                explosionListIterator.remove();
            }
            else {
                explosion.draw(batch);
            }
        }
    }

    private void renderLasers(float deltaTime){
        // Create new lasers
        if (players.get(0).canFireLaser()) {
            Laser[] lasers = players.get(0).attack();
            for (Laser laser : lasers) {
                laser.addToRenderQueue();
            }
        }
        // Draw lasers
        // Remove old lasers
        ListIterator<Entity> iterator = renderQueue.listIterator();
        while(iterator.hasNext()) {
            Entity entity = iterator.next();
            if (entity instanceof Laser) {
                Laser laser = (Laser)entity;
                laser.draw(batch);
                laser.getBoundingBox().y += laser.getMovementSpeed()*deltaTime;
                if (laser.getBoundingBox().y + laser.getBoundingBox().height < 0) {
                    iterator.remove();
                }
            }

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