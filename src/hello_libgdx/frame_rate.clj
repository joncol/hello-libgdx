(ns hello-libgdx.frame-rate
  (:import [com.badlogic.gdx Gdx]
           [com.badlogic.gdx.graphics Color OrthographicCamera]
           [com.badlogic.gdx.graphics.g2d BitmapFont GlyphLayout SpriteBatch]
           [com.badlogic.gdx.utils BufferUtils Disposable TimeUtils])
  (:require [clojure.pprint :refer [print-table]]
            [clojure.reflect :as r]))

(gen-class :name hello-libgdx.FrameRate
           :implements [com.badlogic.gdx.utils.Disposable]
           :state state
           :init init
           :methods [[render [] void]
                     [resize [int int] void]])

(defn -dispose [this]
  (let [state @(.state this)]
    (.dispose (:batch state))
    (.dispose (:font state))
    (.dispose (:glyph-layout state))
    (.dispose (:camera state))))

(defn -init []
  [[] (atom {:batch        (SpriteBatch.)
             :font         (BitmapFont.)
             :glyph-layout (GlyphLayout.)
             :camera       (OrthographicCamera. (.getWidth Gdx/graphics)
                                                (.getHeight Gdx/graphics))})])

(defn -resize [this width height]
  (let [state  @(.state this)
        camera (:camera state)]
    (.setToOrtho camera false (float width) (float height))
    (.update camera)
    (.setProjectionMatrix (:batch state) (.combined camera))))

(defn -render [this]
  (let [state @(.state this)
        msg   (str (int (.getFramesPerSecond Gdx/graphics)) " fps")]
    (try
      (.setText (:glyph-layout state) (:font state) msg)
      (.begin (:batch state))
      (.draw (:font state) (:batch state) (:glyph-layout state)
             (float 5) (float (- (.getHeight Gdx/graphics)
                                 (.-height (:glyph-layout state)))))
      (catch Exception e
        (prn e))
      (finally
        (.end (:batch state))))))
