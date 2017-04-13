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

(def app (proxy [SimpleApplication] []
  (simpleInitApp []
    (let [b (Box. Vector3f/ZERO 1 1 1)
          geom (Geometry. "Box" b)
          mat (Material. (.getAssetManager this)
                         "Common/MatDefs/Misc/Unshaded.j3md")]
      (.setColor mat "Color" ColorRGBA/Blue)
      (.setMaterial geom mat)
      (doto (.getRootNode this) (.attachChild geom))))))
 
(defn -main [& args]
 (doto app
   (.start)))
