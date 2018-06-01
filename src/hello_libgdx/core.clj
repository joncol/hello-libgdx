(ns hello-libgdx.core
  (:import [com.badlogic.gdx Game Gdx Graphics Input Screen]
           [com.badlogic.gdx.graphics Color GL20]
           [com.badlogic.gdx.graphics.g2d BitmapFont]
           [com.badlogic.gdx.graphics.glutils
            ShapeRenderer ShapeRenderer$ShapeType]
           [com.badlogic.gdx.scenes.scene2d Stage]
           [com.badlogic.gdx.scenes.scene2d.ui Label Label$LabelStyle]
           [com.badlogic.gdx.utils Align]))

(gen-class :name hello-libgdx.core.Game
           :extends com.badlogic.gdx.Game)

(def rect-size 250)

(def angle (atom 0))

(defn show [stage])

(defn render [stage frame-rate delta]
  (reset! stage (Stage.))
  (.glClearColor (Gdx/gl) 0 0 0 0)
  (.glClear (Gdx/gl) GL20/GL_COLOR_BUFFER_BIT)
  (swap! angle #(+ % (* delta 0.5)))
  (when (.isTouched Gdx/input)
    (doto (ShapeRenderer.)
      (.begin ShapeRenderer$ShapeType/Filled)
      (.setColor 0.980 0.745 0.345 1.0)
      (.identity)
      (.translate (.getX Gdx/input) (- (.getHeight Gdx/graphics)
                                       (.getY Gdx/input)) 0)
      (.rotate 0 0 1 (* 180 (Math/sin @angle)))
      (.rect (- (quot rect-size 2))
             (- (quot rect-size 2))
             rect-size rect-size)
      (.end)))
  (let [style (Label$LabelStyle. (BitmapFont.) (Color. 1 1 1 1))
        label (doto (Label. "hello, libgdx!" style)
                (.setFontScale 4)
                (.setWidth (.getWidth Gdx/graphics))
                (.setAlignment Align/center)
                (.setPosition 0 (quot (.getHeight Gdx/graphics) 2)))]
    (.addActor @stage label))
  (doto @stage
    (.act delta)
    (.draw))
  (.render frame-rate))

(defn main-screen []
  (let [stage      (atom nil)
        frame-rate (hello-libgdx.FrameRate.)]
    (proxy [Screen] []
      (show []
        (show stage))
      (render [delta]
        (render stage frame-rate delta))
      (dispose [])
      (hide [])
      (pause [])
      (resize [w h]
        (.resize frame-rate w h))
      (resume []))))

(defn -create [^Game this]
  (.setScreen this (main-screen)))
