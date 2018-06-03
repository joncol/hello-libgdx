(ns hello-libgdx.screens.main-screen
  (:require [hello-libgdx.font-generator :refer [generate-font]]
            [hello-libgdx.util :refer [print-object-fields with-disposable]])
  (:import [com.badlogic.gdx
            Game Gdx Graphics Input Input$Keys InputProcessor Screen]
           [com.badlogic.gdx.graphics
            Color GL20 OrthographicCamera PerspectiveCamera Texture
            Texture$TextureFilter VertexAttributes VertexAttributes$Usage]
           [com.badlogic.gdx.graphics.g2d BitmapFont GlyphLayout SpriteBatch]
           [com.badlogic.gdx.graphics.g3d
            Attribute Environment Material Model ModelBatch ModelInstance]
           [com.badlogic.gdx.graphics.g3d.attributes
            BlendingAttribute ColorAttribute]
           [com.badlogic.gdx.graphics.g3d.environment DirectionalLight]
           [com.badlogic.gdx.graphics.g3d.utils ModelBuilder]
           [com.badlogic.gdx.graphics.glutils
            ShapeRenderer ShapeRenderer$ShapeType]
           [com.badlogic.gdx.math Matrix4 Vector3]
           com.badlogic.gdx.scenes.scene2d.Stage
           [com.badlogic.gdx.scenes.scene2d.ui Label Label$LabelStyle]
           [com.badlogic.gdx.utils Align Disposable]))

(gen-class :name hello-libgdx.screens.main-screen.MainScreen
           :implements [com.badlogic.gdx.Screen
                        com.badlogic.gdx.utils.Disposable]
           :state state
           :init init)

;; https://flatuicolors.com/palette/fr

(def yellow1 [0.980 0.827 0.564])
(def yellow2 [0.964 0.725 0.231])
(def yellow3 [0.980 0.596 0.227])
(def yellow4 [0.898 0.556 0.149])

(def red1 [0.972 0.760 0.568])
(def red2 [0.898 0.313 0.223])
(def red3 [0.921 0.184 0.023])
(def red4 [0.717 0.082 0.250])

(def blue1 [0.415 0.537 0.8])
(def blue2 [0.290 0.411 0.741])
(def blue3 [0.117 0.215 0.6])
(def blue4 [0.047 0.141 0.380])

(def cyan1 [0.509 0.8 0.866])
(def cyan2 [0.376 0.639 0.737])
(def cyan3 [0.235 0.388 0.509])
(def cyan4 [0.039 0.239 0.384])

;; Green
(def green1 [0.721 0.913 0.580])
(def green2 [0.470 0.878 0.560])
(def green3 [0.219 0.678 0.662])
(def green4 [0.027 0.6 0.572])

(defn- ->Color [[r g b & [a]]]
  (Color. r g b (or a 1.0)))

(defn- initial-state []
  (let [camera     (PerspectiveCamera.
                    45
                    (.getWidth Gdx/graphics)
                    (.getHeight Gdx/graphics))
        cube-size  25
        attrs      [(ColorAttribute/createDiffuse (->Color (conj red4 0.85)))
                    (BlendingAttribute. GL20/GL_SRC_ALPHA
                                        GL20/GL_ONE_MINUS_SRC_ALPHA)]
        cube-model (.createBox
                    (ModelBuilder.) cube-size cube-size cube-size
                    (Material. (into-array Attribute attrs))
                    (bit-or VertexAttributes$Usage/Position
                            VertexAttributes$Usage/Normal))
        env        (Environment.)]
    (.. camera -position (set 0.0 0.0 10.0))
    (.lookAt camera 0 0 0)
    (set! (.near camera) 0.001)
    (set! (.far camera) 10000.0)
    (.set env (ColorAttribute. ColorAttribute/AmbientLight
                               (float 0.4) (float 0.4) (float 0.4) (float 1)))
    (.add env (doto (DirectionalLight.)
                (.set (float 0.8) (float 0.8) (float 0.8)
                      (float -1) (float -0.8) (float -10))))
    {:active?       true
     :font          (generate-font "fonts/AbrilFatface-Regular.ttf"
                                   #_"fonts/UbuntuMono-Regular.ttf"
                                   :size 100
                                   :color Color/WHITE
                                   :shadow-offset-x 6
                                   :shadow-offset-y 6
                                   :shadow-color (Color. 0 0 0 0))
     :rect-pos      [0 0]
     :rect-size     50
     :cube-model    cube-model
     :cube-instance (ModelInstance. cube-model)
     :angle1        0
     :angle2        0
     :angle3        0
     :angle4        0
     :angle5        0
     :world-width   (.getWidth Gdx/graphics)
     :world-height  (.getHeight Gdx/graphics)
     :camera        camera
     :environment   env
     :hud           (hello-libgdx.Hud.)}))

(defn -init []
  [[] (atom (initial-state))])

(defn dispose-state [state]
  (doseq [obj (-> state (select-keys [:font :cube-model :hud]) vals)]
    (when obj
      (.dispose obj))))

(defn -dispose [this]
  (dispose-state @(.state this)))

(defn reset-state! [state]
  (try
    (dispose-state @state)
    (swap! state #(merge (initial-state) (select-keys % [:keys-pressed])))
    (catch Exception e
      (prn e))))

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
         (- (/ size 2))
         (- (/ size 2))
         size
         size))

(defn- draw-cube [renderer size]
  (let [h (/ size 2)]
    (.box renderer
          (- h)
          (- h)
          h
          size
          size
          size)))

(defn- set-color [renderer [r g b & [a]]]
  (.setColor renderer r g b (or a 1.0)))

(defn- render-rects [state]
  (let [[x y]     (:rect-pos state)
        z         -250
        rect-size (:rect-size state)
        a1        (:angle1 state)
        a2        (:angle2 state)
        a3        (:angle3 state)
        a4        (:angle4 state)
        a5        (:angle5 state)]
    (with-disposable [renderer (ShapeRenderer.)]
      (try
        (let [m (doto (Matrix4.)
                  (.translate x y z)
                  (.rotate 0 0 1 (* 180
                                    (Math/sin a1)
                                    (Math/sin a2)
                                    (Math/sin a3))))]
          (doto renderer
            (.setProjectionMatrix (.combined (:camera state)))

            (.begin ShapeRenderer$ShapeType/Filled)
            (set-color yellow2)
            (.setTransformMatrix m)
            (draw-rect rect-size)

            (set-color cyan1)
            (.setTransformMatrix m)
            (.translate (- (/ rect-size 2)) (- (/ rect-size 2)) 0)
            (.rotate 0 0 1 (* 90 (Math/sin a1) (Math/sin a3) (Math/sin a2)))
            (draw-rect (/ rect-size 4))

            (set-color cyan2)
            (.setTransformMatrix m)
            (.translate (/ rect-size 2) (- (/ rect-size 2)) 0)
            (.rotate 0 0 1 (* 180 (Math/sin a3) (Math/sin a4) (Math/sin a5)))
            (draw-rect (/ rect-size 4))

            (set-color cyan3)
            (.setTransformMatrix m)
            (.translate (/ rect-size 2) (/ rect-size 2) 0)
            (.rotate 0 0 1 (* 270 (Math/sin a1) (Math/sin a3) (Math/sin a2)))
            (draw-rect (/ rect-size 4))

            (set-color cyan4)
            (.setTransformMatrix m)
            (.translate (- (/ rect-size 2)) (/ rect-size 2) 0)
            (.rotate 0 0 1 (* 360 (Math/sin a1) (Math/sin a3) (Math/sin a2)))
            (draw-rect (/ rect-size 4))

            (set-color red2)
            .identity
            (.translate x y z)
            (.rotate 0 0 1 (* 180 (Math/sin a1) (Math/sin a2) (Math/sin a5)))
            (draw-rect (/ rect-size 2))))
        (finally
          (.end renderer))))))

(defn- render-cube [state]
  (let [[x y]     (:rect-pos state)
        cube-size (:cube-size state)
        a1        (:angle1 state)
        a2        (:angle2 state)
        a3        (:angle3 state)
        a4        (:angle4 state)
        a5        (:angle5 state)]
    (.update (:camera state) true)
    (.. (:cube-instance state)
        -transform
        idt)
    (.. (:cube-instance state)
        -transform
        (translate (float (* 50
                             (Math/sin a1)
                             (Math/sin a3)
                             (Math/sin a5)))
                   (float (* 30
                             (Math/sin a2)
                             (Math/sin a3)
                             (Math/sin a4)))
                   (float -100))
        (rotate 1 0 0 (* 90 (Math/sin a1) (Math/sin a2)
                         (Math/sin a3)))
        (rotate 0 1 0 (* 10 (Math/sin a3) (Math/sin a5)
                         (Math/sin a3)))
        (rotate 0 0 1 (* 90 (Math/sin a4) (Math/sin a3)
                         (Math/sin a3))))
    (with-disposable [batch (ModelBatch.)]
      (try
        (doto batch
          (.begin (:camera state))
          (.render (:cube-instance state)
                   (:environment state)))
        (finally
          (.end batch))))))

(defn- render-text [state text]
  (let [font         (:font state)
        glyph-layout (GlyphLayout. font text)]
    (with-disposable [batch (SpriteBatch.)]
      (try
        (.begin batch)
        (let [w  (.-width glyph-layout)
              h  (.-height glyph-layout)
              a1 (:angle1 state)
              a2 (:angle2 state)
              a3 (:angle3 state)
              a4 (:angle4 state)
              a5 (:angle5 state)]
          (.setTransformMatrix
           batch
           (doto (Matrix4.)
             (.translate (float (/ (.getWidth Gdx/graphics) 2))
                         (float (/ (.getHeight Gdx/graphics) 2))
                         (float 0))
             (.rotate (Vector3. 0 0 1)
                      (* 45
                         (Math/sin (float a1))
                         (Math/sin (float a2))
                         (Math/sin (float a3))
                         (Math/sin (float a4))
                         (Math/sin (float a5))))
             (.translate (float (- (/ w 2)))
                         (float (/ h 2))
                         (float 0)))))
        (.setFilter (.. font getRegion getTexture)
                    Texture$TextureFilter/Linear
                    Texture$TextureFilter/Linear)
        (.draw font batch glyph-layout (float 0) (float 0))
        (catch Exception e
          (prn e))
        (finally
          (.end batch))))))

(defn- update-state [state delta]
  (swap! state
         (fn [state]
           (let [a1 (:angle1 state)
                 a2 (:angle2 state)
                 a3 (:angle3 state)]
             (-> state
                 (update :angle1 #(+ % (* delta 0.5)))
                 (update :angle2 #(+ % (* delta 0.15)))
                 (update :angle3 #(+ % (* delta 0.85)))
                 (update :angle4 #(+ % (* delta 0.35)))
                 (update :angle5 #(+ % (* delta 0.95)))
                 (assoc :rect-pos [(* 400 (Math/sin a1) (Math/sin a2) 0.25)
                                   (* 300 (Math/sin a1) (Math/sin a3) 0.25)])
                 (assoc :rect-size (+ 50 (* 25 (Math/sin a1)))))))))

(defn -render [this delta]
  (try
    (let [state @(.state this)]
      (.glClearColor (Gdx/gl) 0 0 0 0)
      (.glClear (Gdx/gl)
                (bit-or GL20/GL_COLOR_BUFFER_BIT
                        GL20/GL_DEPTH_BUFFER_BIT
                        (if (.. Gdx/graphics getBufferFormat -coverageSampling)
                          GL20/GL_COVERAGE_BUFFER_BIT_NV
                          0)))
      (when (:active? state)
        (render-text state "hello, libGDX!")
        (render-cube state)
        (render-rects state)
        (update-state (.state this) delta))
      (.render (:hud state)))
    (catch Exception e
      (prn e))))

(defn -pause [this])

(defn -resume [this])

(defn -hide [this]
  (swap! (.state this) (fn [state] (update state :active? (constantly false)))))
