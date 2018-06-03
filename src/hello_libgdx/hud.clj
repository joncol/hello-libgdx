(ns hello-libgdx.hud
  (:require [clojure.pprint :refer [print-table]]
            [hello-libgdx.font-generator :refer [generate-font]])
  (:import [com.badlogic.gdx Gdx]
           [com.badlogic.gdx.graphics Color OrthographicCamera]
           [com.badlogic.gdx.graphics.g2d BitmapFont GlyphLayout SpriteBatch]
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

(defn -init []
  [[] (atom {:active    true
             :batch     (SpriteBatch.)
             :font      (generate-font "fonts/UbuntuMono-Regular.ttf"'
                                       :size 20)
             :camera    (OrthographicCamera.
                         (.getWidth Gdx/graphics)
                         (.getHeight Gdx/graphics))
             :shortcuts (with-out-str
                          (print-table
                           ["Key" "Function"]
                           [{"Key" "F1" "Function" "Toggle HUD"}
                            {"Key" "C-h" "Function" "Toggle screen"}
                            {"Key" "C-r" "Function" "Reset state"}
                            {"Key" "C-q" "Function" "Quit"}]))})])

(defn dispose-state [state]
  (doseq [obj (-> state (select-keys [:batch :font]) vals)]
    (when obj
      (.dispose obj))))

(defn -dispose [this]
  (dispose-state @(.state this)))

(defn -resize [this width height]
  (let [state  @(.state this)
        camera (:camera state)]
    (.setToOrtho camera false (float width) (float height))
    (.update camera)
    (.setProjectionMatrix (:batch state) (.combined camera))))

(defn -render [this]
  (when (:active @(.state this))
    (let [state        @(.state this)
          fps          (str (int (.getFramesPerSecond Gdx/graphics)) " fps")
          glyph-layout (GlyphLayout.)]
      (.setText glyph-layout (:font state) (str fps "\n" (:shortcuts state)))
      (.begin (:batch state))
      (.draw (:font state) (:batch state) glyph-layout
             (float 16) (float (- (.getHeight Gdx/graphics) 16)))
      (.end (:batch state)))))

(defn -show [this]
  (swap! (.state this) #(assoc % :active true)))

(defn -hide [this]
  (swap! (.state this) #(assoc % :active false)))

(defn -toggleShow [this]
  (swap! (.state this) #(update % :active not)))
