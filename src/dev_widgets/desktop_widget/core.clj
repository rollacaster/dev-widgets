(ns dev-widgets.desktop-widget.core
  (:gen-class)
  (:require
   [cljfx.api :as fx]
   [dev-widgets.desktop-widget.app :refer [root-view]]))

(def renderer
  (fx/create-renderer
   :middleware (fx/wrap-map-desc (fn [state]
                                   {:fx/type root-view
                                    :state state}))))

(defn reload []
  (renderer))

(defn -main [& args]
  (fx/mount-renderer (atom {}) renderer))
