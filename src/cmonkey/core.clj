(ns cmonkey.core
  (:import [com.jme3
            app.SimpleApplication
            asset.AssetManager
            system.AppSettings
            system.JmeSystem
            material.Material
            scene.Geometry
            scene.Node
            scene.shape.Box
            math.Vector3f
            math.ColorRGBA]))

(comment
  alot of these methods do not include all of
  the constructers needed right now as this is
  a playground to see what is actually needed)

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

(defn set-local-tranform!
  "sets local transform of geometry"
  [geo v]
  (let [pos (if (= (type v) clojure.lang.PersistentVector)
                (vec->vector3f v)
                v)]
    (.setLocalTransform geo (vec->vector3f pos))
    geo))

(def unshaded "Common/MatDefs/Misc/Unshaded.j3md")

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
                                 (set-local-tranform!
                                  (primitive-box asset-manager)
                                  [x y z])))))))

  (def app2 (proxy [SimpleApplication] []
              (simpleInitApp []
                (.attachChild
                 (.getRootNode this)
                 (primitive-box (.getAssetManager this))))))
  
  (defn start [& args]
    (doto app
      (.setShowSettings true)
      #_(.setSettings *app-settings*)
      (.start)))

  (defn start2 [& args]
    (doto app2
      (.setShowSettings true)
      #_(.setSettings *app-settings*)
      (.start)))
  
  (start2)
  
  (start))
