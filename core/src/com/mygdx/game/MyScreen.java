package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MyScreen implements Screen {


    private final MyGdxGame game;

    private final OrthographicCamera camera;
    private final OrthogonalTiledMapRenderer renderer;
    private final Viewport viewport;
    private final TiledMapTileLayer background;
    private final LayerAndActors redLayerAndObjects;
    private final TiledMapTileLayer greenLayer;

    private final TiledMap map;
    private final AssetManager manager;

    public MyScreen(MyGdxGame game) {

        // Pas utilisé ici mais cela pourrait être utile dans un cas plus abouti
        this.game = game;

        String fileName = "untitled.tmx";
        manager = new AssetManager();
        manager.setLoader(TiledMap.class, new TmxMapLoader());
        manager.load(fileName, TiledMap.class);
        manager.finishLoading();

        // Chargement de la map
        map = manager.get(fileName, TiledMap.class);
        // Création du renderer
        renderer = new OrthogonalTiledMapRenderer(map);

        // Récupération des dimensions de la map
        MapProperties properties = map.getProperties();
        Integer tileWidth = properties.get("tilewidth", Integer.class);
        Integer tileHeight = properties.get("tileheight", Integer.class);
        Integer mapWidthInTiles = properties.get("width", Integer.class);
        Integer mapHeightInTiles = properties.get("height", Integer.class);
        int mapWidthInPixels = mapWidthInTiles * tileWidth;
        int mapHeightInPixels = mapHeightInTiles * tileHeight;

        // Création de la camera
        camera = new OrthographicCamera(320, 208);
        camera.position.x = mapWidthInPixels * .5f;
        camera.position.y = mapHeightInPixels * .35f;
        viewport = new FitViewport(320, 208, camera);
        // exemples pour zoomer/dézoomer
        // zoom in:    viewport.setWorldWidth(160);
        // zoom out:viewport.setWorldWidth(1600);

        // Chargement des layers et des objects
        MapLayers mapLayers = map.getLayers();
        background = (TiledMapTileLayer) mapLayers.get("Background");
        // Le rouge utilise l'objet avec acteurs et layer regroupés
        redLayerAndObjects = new LayerAndActors((TiledMapTileLayer) mapLayers.get("RedLayer"));
        // Pas le vert car il n'a pas d'acteur associé
        greenLayer = (TiledMapTileLayer) mapLayers.get("GreenLayer");
        // Exemple d'utilisation d'un group avec plusieurs calques
        final MapGroupLayer group1 = (MapGroupLayer) mapLayers.get("Group1");
        MapLayer redObjectsLine1 = group1.getLayers().get("RedObjectsLine1");
        MapLayer redObjectsLine2 = group1.getLayers().get("RedObjectsLine2");
        // Création de l'objet stage qui gere les acteurs et les événments qu'ils reçoivent
        Stage stage = new Stage(viewport);
        // Un clic sur une figure affiche son nom dans la console
        // On parcourt les objets du calque
        for (final RectangleMapObject object : redObjectsLine1.getObjects().getByType(RectangleMapObject.class)) {
            // Récupération de l'emplacement occupé par l'objet : ici on a créé des rectangle dans Tiled
            Rectangle rect = object.getRectangle();
            // Création de l'acteur
            final Actor face = new Actor();
            // Ajout dans notre objet regroupant le layer et les acteurs
            redLayerAndObjects.add(face);
            // Ajout de l'acteur dans le "stage"
            stage.addActor(face);
            // Définition de la zone correspondant à l'acteur
            face.setBounds(rect.x, rect.y, rect.width, rect.height);
            // Ecoute du clic sur l'acteur
            face.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Ligne 1 : " + object.getName());
                }
            });
        }
        for (final RectangleMapObject object : redObjectsLine2.getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            final Actor face = new Actor();
//            redLayerAndObjects.add(face); Il faut décommenter cette ligne sinon les acteurs sont toujours cliquable
//            lorsque le layer rouge est rendu invisible !!!
            stage.addActor(face);
            face.setBounds(rect.x, rect.y, rect.width, rect.height);
            face.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Ligne 2 : " + object.getName());
                }
            });
        }

        // Récupération des deux boutons
        MapLayer buttonLayer = mapLayers.get("ButtonLayer");
        // Un clic sur un bouton inverse la visibilité du calque de même couleur. On se sert ici du nom des objets
        // pour les différencier mais on aurait aussi bien pu créer un object layer par bouton
        for (final RectangleMapObject object : buttonLayer.getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            final Actor button = new Actor();
            stage.addActor(button);
            button.setBounds(rect.x, rect.y, rect.width, rect.height);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if ("green button".equals(object.getName())) {
                        greenLayer.setVisible(!greenLayer.isVisible());
                    } else {
                        redLayerAndObjects.setVisible(!redLayerAndObjects.isVisible());
                    }
                }
            });
        }
        // On indique que c'est l'objet stage qui gère les événements de type input sur l'écran (souris, clavier...)
        Gdx.input.setInputProcessor(stage);
        viewport.apply();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // On effae l'écran avec chaque ré affichage
        ScreenUtils.clear(.0f, .0f, .0f, 1);

        // O  utilise la camera pour l'affichage
        camera.update();
        renderer.setView(camera);
        renderer.getBatch().begin();
        renderer.renderTileLayer(background);
        // Il faut tester la visibilité du layer pour savoir si on doit le dessiner ou non
        if (greenLayer.isVisible()) {
            renderer.renderTileLayer(greenLayer);
        }
        // delegue l'affichage à notre objet regroupant le layer et l'acteur car on a pas voulu garder de référence
        // au layer seul en dehors de cet objet
        redLayerAndObjects.render(renderer);
        renderer.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        // Indispensable sinon on perd la synchronisation entre position mémorisées des objets et leurs positions
        // d'affichage en cas de redimensionnement de la fenêtre
        viewport.update(width, height);
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
    public void dispose() {
        // libère toutes les resources allouées
        map.dispose();
        manager.dispose();
        renderer.dispose();
    }
}
