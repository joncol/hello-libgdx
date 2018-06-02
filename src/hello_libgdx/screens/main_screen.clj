(ns hello-libgdx.screens.main-screen
  (:require [hello-libgdx.font-generator :refer [generate-font]])
  (:import [com.badlogic.gdx
            Game Gdx Graphics Input Input$Keys InputProcessor Screen]
           [com.badlogic.gdx.graphics Color GL20 OrthographicCamera]
           [com.badlogic.gdx.graphics.g2d BitmapFont GlyphLayout SpriteBatch]
           [com.badlogic.gdx.graphics.glutils
            ShapeRenderer ShapeRenderer$ShapeType]
           [com.badlogic.gdx.math Matrix4 Vector3]
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

(defn- ->Color [[r g b & [a]]]
  (Color. r g b (or a 1.0)))

(defn- initial-state []
  {:active?        true
   :font           (generate-font "fonts/AbrilFatface-Regular.ttf"
                                  :size 100 #_#_:border-width 2
                                  :shadow-offset-x 4
                                  :shadow-offset-y 4
                                  :shadow-color (Color. 0 0 0 0.25))
   :rect-pos       [0 0]
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
   :hud            (hello-libgdx.Hud.)})

(defn -init []
  [[] (atom (initial-state))])

(defn reset-state! [state]
  (swap! state #(merge (initial-state) (select-keys % [:keys-pressed]))))

(defn keys-pressed? [state ks]
  (some identity (vals (select-keys (:keys-pressed @state) ks))))

(defn- ctrl-pressed? [state]
  (keys-pressed? state [Input$Keys/CONTROL_LEFT
                        Input$Keys/CONTROL_RIGHT]))

(defn- toggle-active [state]
  (swap! state
         (fn [state]
           (update state :active? not))))

(defn key-down [state key-code]
  (cond
    (= key-code Input$Keys/F1) (.toggleShow (:hud @state))
    (and (ctrl-pressed? state) (= key-code Input$Keys/R)) (reset-state! state)
    (and (ctrl-pressed? state) (= key-code Input$Keys/H)) (toggle-active state)
    (and (ctrl-pressed? state) (= key-code Input$Keys/Q)) (.exit Gdx/app))

  (swap! state (fn [state] (assoc-in state [:keys-pressed key-code] true))))

(defn key-typed [state ch])

(defn key-up [state key-code]
  (swap! state (fn [state] (assoc-in state [:keys-pressed key-code] false))))

(defn input-processor [state]
  (proxy [com.badlogic.gdx.InputProcessor] []
    (keyDown [key-code]
      (key-down state key-code)
      true)
    (keyTyped [ch]
      (key-typed state ch)
      true)
    (keyUp [key-code]
      (key-up state key-code)
      true)
    (mouseMoved [x y] false)
    (scrolled [amount] false)
    (touchDown [x y pointer button] false)
    (touchDragged [x y pointer] false)
    (touchUp [x y pointer button] false)))

(defn -show [this]
  (swap! (.state this) (fn [state] (update state :active? (constantly true))))
  (.setInputProcessor Gdx/input (input-processor (.state this))))

(defn -resize [this width height]
  (let [state @(.state this)]
    ;; (.setToOrtho camera false (float width) (float height))
    ;; (.update camera)
    (.resize (:hud state) width height)))

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

(defn- render-rects [state]
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
        (.end (:shape-renderer state))))))

(defn- render-text [state text]
  (let [font         (:font state)
        glyph-layout (GlyphLayout. font text)
        w            (.-width glyph-layout)
        h            (.-height glyph-layout)
        batch        (SpriteBatch.)
        a1           (:angle1 state)
        a2           (:angle2 state)
        a3           (:angle3 state)
        a4           (:angle4 state)
        a5           (:angle5 state)]
    (try
      (.begin batch)
      ;; (.setTransformMatrix batch transl)
      (let [m (doto (Matrix4.)
                (.setToTranslation (float (/ (.getWidth Gdx/graphics) 2))
                                   (float (/ (.getHeight Gdx/graphics) 2))
                                   (float 0))
                (.mul (doto (Matrix4.)
                        (.setToRotation (Vector3. 0 0 1)
                                        (* 45
                                           (Math/sin (float a1))
                                           (Math/sin (float a2))
                                           (Math/sin (float a3))
                                           (Math/sin (float a4))
                                           (Math/sin (float a5))))))
                (.mul (doto (Matrix4.)
                        (.setToTranslation
                         (float (- (/ w 2)))
                         (float (/ h 2))
                         (float 0)))))]
        (.setTransformMatrix batch m))
      (.draw font batch glyph-layout (float 0) (float 0))
      (catch Exception e
        (prn e))
      (finally
        (.end batch)))))

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
  (try
    (let [state @(.state this)]
      (.glClearColor (Gdx/gl) 0 0 0 0)
      (.glClear (Gdx/gl) GL20/GL_COLOR_BUFFER_BIT)
      (when (:active? state)
        (render-rects state)
        (render-text state "hello, libgdx!")
        (doto (:stage state)
          (.act delta)
          (.draw))
        (update-state (.state this) delta))
      (.render (:hud state)))
    (catch Exception e
      (prn e))))

(defn -pause [this])

(defn -resume [this])

(defn -hide [this]
  (swap! (.state this) (fn [state] (update state :active? (constantly false)))))
