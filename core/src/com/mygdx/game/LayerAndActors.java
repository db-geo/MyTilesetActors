package com.mygdx.game;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de gérer la visiblité d'un layer et des ses acteurs comme celle d'un même élément
 */
public class LayerAndActors {
    private final TiledMapTileLayer layer;
    private final List<Actor> actors;

    /**
     * Constructeur qui affecte le layer
     *
     * @param layer
     */
    public LayerAndActors(TiledMapTileLayer layer) {
        this.layer = layer;
        this.actors = new ArrayList();
    }

    /**
     * Ajoute un acteur
     *
     * @param actor
     */
    public void add(Actor actor) {
        actors.add(actor);
    }

    /**
     * Indique la visibilité courante du layer
     *
     * @return true if visible
     */
    public boolean isVisible() {
        return layer.isVisible();
    }

    /**
     * Affecte la visibilité du layer et de ses acteurs
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        layer.setVisible(visible);
        for (Actor actor : actors) {
            actor.setVisible(visible);
        }
    }

    /**
     * Demande au renderer de dessiner le layer uniquement s'il est visible
     * @param renderer
     */
    public void render(OrthogonalTiledMapRenderer renderer) {
        if (layer.isVisible()) {
            renderer.renderTileLayer(layer);
        }

    }
}
