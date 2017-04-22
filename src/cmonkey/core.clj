(ns cmonkey.core
  (:import [com.jme3
            app.SimpleApplication
            asset.AssetManager
            light.DirectionalLight
            system.AppSettings
            system.JmeSystem
            material.Material
            scene.Geometry
            scene.Node
            scene.Spatial
            scene.shape.Box
            math.Vector3f
            math.ColorRGBA]))

(comment
  alot of these methods do not include all of
  the constructers needed right now as this is
  a playground to see what is actually needed)

(defn asset-manager
  "returns a new, configured assetManager" []
  (JmeSystem/newAssetManager
   (.getResource
    (.getContextClassLoader (Thread/currentThread))
    "com/jme3/asset/Desktop.cfg")))

(defn vector3f->vec
  "converts jmonkey's vector3F to a normal clojure vec"
  [v3f]
  [(.getX v3f) (.getY v3f) (.getZ v3f)])

(defn vec->vector3f
  "converts a clojure vector to a jmonkey vector3F.
  Must be of atleast length 3 all values past the
  2nd index will be ignored"
  [v]
  (Vector3f. (first v) (second v) (nth v 2)))

(defn box
  "creates a jmonkey box class given a vector3f or vec and a size"
  [position w h l]
  (let [pos (if (= (type position) clojure.lang.PersistentVector)
              (vec->vector3f position)
              position)]
    (Box. pos w h l)))

;;wanted to call (apply...) here but does not work with java constructors
;;as arguments must be known ahead of time, can use reflection at the cost of speed
;;probably best to avoid it for now
(defn geometry
  "given a name and a mesh creates a jmonkey geometry"
  [n mesh]
  (Geometry. n mesh))

(defn material
  "creates a material given an assetManager and a asset path"
  [am ap]
  (Material. am ap))

(defn spatial
  "given assetManager and asset path creates a spatial"
  [am ap]
  (.loadModel am ap))


;;we will try to hide the mutation of these better in the future
;;might want to return mutated object AND mutated value in the future
;;for these setter functions
(defn set-mat-color!
  "sets the color of a material given the material a string
  and a jmonkey math.ColorRGBA. Will return the mutated object"
  [mat string color]
  (.setColor mat string color)
  mat)

(defn set-geom-mat!
  "set the material to a geometry"
  [geom mat]
  (.setMaterial geom mat)
  geom)

(defn set-local-translation!
  "sets local transform of geometry"
  [geo v]
  (let [pos (if (= (type v) clojure.lang.PersistentVector)
                (vec->vector3f v)
                v)]
    (.setLocalTranslation geo pos)
    geo))

(def unshaded "Common/MatDefs/Misc/Unshaded.j3md")

(def teapot "assets/Models/Teapot/Teapot.obj")

(def show-normals "Common/MatDefs/Misc/ShowNormals.j3md")

(defn primitive-box
  "creates an origin based white box"
  [am]
  (doto (geometry "Box" (box [0 0 0] 1 1 1))
    (set-geom-mat! (doto (material am unshaded)
                     (set-mat-color! "Color" ColorRGBA/White)))))

(defn populate-scene
  "TODO: given an the root node and a hashmap of nodes fills the scen graph"
  [am scene-state]
  )

(comment
  ;;some testing
  ;;mutable objects need we need to revaluate alot when we want to "reset things"
  
  (def app (proxy [SimpleApplication] []
             (simpleInitApp []
               (let [asset-manager (.getAssetManager this)
                     root-node (.getRootNode this)]
                 (doseq [x [-1 0 1]
                         y [-1 0 1]
                         z [-1 0 1]]
                   (.attachChild root-node
                                 (set-local-translation!
                                  (primitive-box asset-manager)
                                  [x y z])))))))

  (def app2 (proxy [SimpleApplication] []
              (simpleInitApp
                []
                (let [am (.getAssetManager this)
                      rn (.getRootNode this)
                      sp (spatial am teapot)]
                  (.attachChild rn
                                (set-geom-mat!
                                 sp
                                 (material am show-normals)))
                  (.addLight rn
                             (doto (DirectionalLight.)
                               (.setDirection (vec->vector3f [-0.1 -0.7 -1]))))))))

  (def app3 (let [player (primitive-box (asset-manager))]
                (proxy [SimpleApplication] []
                   (simpleInitApp
                     []
                     (.attachChild (.getRootNode this)
                                   player))
                   (simpleUpdate
                     [tpf]
                     (.rotate player 0 (* 2 tpf) 0)))))
  
  (defn start [app]
    (doto app
      (.setShowSettings true)
      #_(.setSettings *app-settings*)
      (.start)))
  
  (start app)
  
  (start app3)

  )
