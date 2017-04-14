(ns cmonkey.core
  (:import [com.jme3
            app.SimpleApplication
            system.AppSettings
            system.JmeSystem
            material.Material
            scene.Geometry
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

(defn geometry
  "given a name and a mesh creates a jmonkey geometry"
  [n mesh]
  (Geometry. n mesh))

(defn material
  "creates a material given an assetManager and a asset path"
  [am ap]
  (Material. am ap))


;;we will try to hide the mutation of these better in the future
(defn set-mat-color!
  "sets the color of a material given the material a string
  and a jmonkey math.ColorRGBA"
  [mat string color]
  (.setColor mat string color))

(defn set-geom-mat!
  "set the material to a geometry"
  [geom mat]
  (.setMaterial geom mat))

(comment
  ;;some testing
         
  (def app (proxy [SimpleApplication] []
             (simpleInitApp []
               (let [b (box [0 0 0] 1 1 1)
                     geom (geometry "Box" b)
                     mat (material (.getAssetManager this)
                                   "Common/MatDefs/Misc/Unshaded.j3md")]
                 (set-mat-color! mat "Color" ColorRGBA/Blue)
                 (set-geom-mat! geom mat)
                 ;;we need a macro here to hide the java calls, if we decide to do that.
                 (doto (.getRootNode this) (.attachChild geom))))))
  
  (defn start [& args]
    (doto app
      (.start)))

  (start))
