(ns hello-libgdx.core
  (:import [com.badlogic.gdx Game Gdx Graphics Input Screen]
           [com.badlogic.gdx.graphics Color GL20 OrthographicCamera]
           [com.badlogic.gdx.graphics.g2d BitmapFont]
           [com.badlogic.gdx.graphics.glutils
            ShapeRenderer ShapeRenderer$ShapeType]
           [com.badlogic.gdx.scenes.scene2d Stage]
           [com.badlogic.gdx.scenes.scene2d.ui Label Label$LabelStyle]
           [com.badlogic.gdx.utils Align]))

(gen-class :name hello-libgdx.core.Game
           :extends com.badlogic.gdx.Game)

(defn -create [^Game this]
  (.setScreen this (hello-libgdx.screens.main-screen.MainScreen.)))
