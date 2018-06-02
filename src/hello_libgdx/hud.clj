(ns hello-libgdx.hud
  (:require [clojure.pprint :refer [print-table]])
  (:import [com.badlogic.gdx Gdx]
           [com.badlogic.gdx.graphics Color OrthographicCamera]
           [com.badlogic.gdx.graphics.g2d BitmapFont GlyphLayout SpriteBatch]
           [com.badlogic.gdx.graphics.g2d.freetype
            FreeTypeFontGenerator FreeTypeFontGenerator$FreeTypeFontParameter]
           [com.badlogic.gdx.utils BufferUtils Disposable TimeUtils]))

(gen-class :name hello-libgdx.Hud
           :implements [com.badlogic.gdx.utils.Disposable]
           :state state
           :init init
           :methods [[render [] void]
                     [resize [int int] void]
                     [show [] void]
                     [hide [] void]
                     [toggleShow [] void]])

(defn -dispose [this]
  (let [state @(.state this)]
    (.dispose (:batch state))
    (.dispose (:font state))
    (.dispose (:glyph-layout state))
    (.dispose (:camera state))))

(defn- font-params []
  (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
    (set! (.size params) 20)
    params))

(defn -init []
  (let [font-gen (FreeTypeFontGenerator.
                  (.internal Gdx/files "fonts/UbuntuMono-Regular.ttf"))
        font     (.generateFont font-gen (font-params))]
    (.dispose font-gen)
    [[] (atom {:active       true
               :batch        (SpriteBatch.)
               :font         font
               :glyph-layout (GlyphLayout.)
               :camera       (OrthographicCamera.
                              (.getWidth Gdx/graphics)
                              (.getHeight Gdx/graphics))})]))

(defn -resize [this width height]
  (let [state  @(.state this)
        camera (:camera state)]
    (.setToOrtho camera false (float width) (float height))
    (.update camera)
    (.setProjectionMatrix (:batch state) (.combined camera))))

(defn -render [this]
  (when (:active @(.state this))
    (let [state     @(.state this)
          fps       (str (int (.getFramesPerSecond Gdx/graphics)) " fps")
          shortcuts (with-out-str
                      (print-table ["Key" "Function"]
                                   [{"Key" "F1" "Function" "Toggle HUD"}
                                    {"Key" "C-h" "Function" "Toggle screen"}
                                    {"Key" "C-r" "Function" "Reset state"}]))]
      (.setText (:glyph-layout state) (:font state) (str fps "\n" shortcuts))
      (.begin (:batch state))
      (.draw (:font state) (:batch state) (:glyph-layout state)
             (float 16) (float (- (.getHeight Gdx/graphics) 16)))
      (.end (:batch state)))))

(defn -show [this]
  (swap! (.state this) #(assoc % :active true)))

(defn -hide [this]
  (swap! (.state this) #(assoc % :active false)))

(defn -toggleShow [this]
  (swap! (.state this) #(update % :active not)))
