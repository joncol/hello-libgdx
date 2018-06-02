(ns hello-libgdx.screens.main-screen
  (:import [com.badlogic.gdx Game Gdx Graphics Input Screen]
           [com.badlogic.gdx.graphics Color GL20 OrthographicCamera]
           [com.badlogic.gdx.graphics.g2d BitmapFont]
           [com.badlogic.gdx.graphics.glutils
            ShapeRenderer ShapeRenderer$ShapeType]
           [com.badlogic.gdx.scenes.scene2d Stage]
           [com.badlogic.gdx.scenes.scene2d.ui Label Label$LabelStyle]
           [com.badlogic.gdx.utils Align]))

(gen-class :name hello-libgdx.screens.main-screen.MainScreen
           :implements [com.badlogic.gdx.Screen]
           :state state
           :init init)

;; https://flatuicolors.com/palette/fr

(def yellow2 [0.964 0.725 0.231])

(def blue2 [0.290 0.411 0.741])

(def red1 [0.972 0.760 0.568])
(def red2 [0.898 0.313 0.223])
(def red3 [0.921 0.184 0.023])
(def red4 [0.717 0.082 0.250])

(def cyan1 [0.509 0.8 0.866])
(def cyan2 [0.376 0.639 0.737])
(def cyan3 [0.235 0.388 0.509])
(def cyan4 [0.039 0.239 0.384])

(defn -init []
  [[] (atom {:rect-pos       [0 0]
             :rect-size      250
             :angle1         0
             :angle2         0
             :angle3         0
             :angle4         0
             :angle5         0
             :shape-renderer (ShapeRenderer.)
             :world-width    (.getWidth Gdx/graphics)
             :world-height   (.getHeight Gdx/graphics)
             :camera         (OrthographicCamera. (.getWidth Gdx/graphics)
                                                  (.getHeight Gdx/graphics))
             :stage          (Stage.)
             :frame-rate     (hello-libgdx.FrameRate.)})])

(defn -show [this]
  (let [state @(.state this)
        stage (:stage state)
        style (Label$LabelStyle. (BitmapFont.) (Color. 1 1 1 1))
        label (doto (Label. "hello, libgdx!" style)
                (.setFontScale 4)
                (.setWidth (.getWidth Gdx/graphics))
                (.setAlignment Align/center)
                (.setPosition 0 (quot (.getHeight Gdx/graphics) 2)))]
    (.addActor stage label)))

(defn -resize [this width height]
  (let [state @(.state this)]
    ;; (.setToOrtho camera false (float width) (float height))
    ;; (.update camera)
    (.resize (:frame-rate state) width height)))

(defn- draw-rect [renderer size]
  (.rect renderer
         (- (quot size 2))
         (- (quot size 2))
         size
         size))

(defn- draw-triangle [renderer size]
  (.rect renderer
         (- (quot size 2))
         (- (quot size 2))
         size
         size))

(defn- set-color [renderer [r g b & [a]]]
  (.setColor renderer r g b (or a 1.0)))

(defn- update-state [state delta]
  (swap! state
         (fn [state]
           (let [a1 (:angle1 state)
                 a2 (:angle2 state)
                 a3 (:angle3 state)
                 w  (:world-width state)
                 h  (:world-height state)]
             (-> state
                 (update :angle1 #(+ % (* delta 0.5)))
                 (update :angle2 #(+ % (* delta 0.15)))
                 (update :angle3 #(+ % (* delta 0.85)))
                 (update :angle4 #(+ % (* delta 0.35)))
                 (update :angle5 #(+ % (* delta 0.95)))
                 (assoc :rect-pos [(* w (Math/sin a1) (Math/sin a2) 0.25)
                                   (* h (Math/sin a1) (Math/sin a3) 0.25)])
                 (assoc :rect-size (+ 500 (* 200 (Math/sin a1)))))))))

(defn -render [this delta]
  (let [state @(.state this)]
    (.glClearColor (Gdx/gl) 0 0 0 0)
    (.glClear (Gdx/gl) GL20/GL_COLOR_BUFFER_BIT)
    (let [[x y]     (:rect-pos state)
          rect-size (:rect-size state)
          a1        (:angle1 state)
          a2        (:angle2 state)
          a3        (:angle3 state)
          a4        (:angle4 state)
          a5        (:angle5 state)]
      (try
        (doto (:shape-renderer state)
          (.setProjectionMatrix (.combined (:camera state)))
          (.begin ShapeRenderer$ShapeType/Filled)
          (set-color yellow2)
          .identity
          (.translate x y 0)
          (.rotate 0 0 1 (* 180 (Math/sin a1) (Math/sin a2) (Math/sin a3)))
          (draw-rect rect-size)

          (set-color cyan1)
          .identity
          (.translate x y 0)
          (.rotate 0 0 1 (* 180 (Math/sin a1) (Math/sin a2) (Math/sin a3)))
          (.translate (- (quot rect-size 2)) (- (quot rect-size 2)) 0)
          (.rotate 0 0 1 (* 90 (Math/sin a1) (Math/sin a3) (Math/sin a2)))
          (draw-triangle (quot rect-size 4))

          (set-color cyan2)
          .identity
          (.translate x y 0)
          (.rotate 0 0 1 (* 180 (Math/sin a1) (Math/sin a2) (Math/sin a3)))
          (.translate (quot rect-size 2) (- (quot rect-size 2)) 0)
          (.rotate 0 0 1 (* 180 (Math/sin a3) (Math/sin a4) (Math/sin a5)))
          (draw-triangle (quot rect-size 4))

          (set-color cyan3)
          .identity
          (.translate x y 0)
          (.rotate 0 0 1 (* 180 (Math/sin a1) (Math/sin a2) (Math/sin a3)))
          (.translate (quot rect-size 2) (quot rect-size 2) 0)
          (.rotate 0 0 1 (* 270 (Math/sin a1) (Math/sin a3) (Math/sin a2)))
          (draw-triangle (quot rect-size 4))

          (set-color cyan4)
          .identity
          (.translate x y 0)
          (.rotate 0 0 1 (* 180 (Math/sin a1) (Math/sin a2) (Math/sin a3)))
          (.translate (- (quot rect-size 2)) (quot rect-size 2) 0)
          (.rotate 0 0 1 (* 360 (Math/sin a1) (Math/sin a3) (Math/sin a2)))
          (draw-triangle (quot rect-size 4))

          (set-color red2)
          .identity
          (.translate x y 0)
          (.rotate 0 0 1 (* 180 (Math/sin a1) (Math/sin a2) (Math/sin a5)))
          (draw-rect (quot rect-size 2)))
        (catch Exception e
          (prn e))
        (finally
          (.end (:shape-renderer state)))))
    (doto (:stage state)
      (.act delta)
      (.draw))
    (.render (:frame-rate state))
    (update-state (.state this) delta)))

(defn -pause [this])

(defn -resume [this])

(defn -hide [this])
