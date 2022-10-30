(ns dev-widgets.desktop-widget.renderer
  (:require [cljfx.api :as fx]
            [dev-widgets.desktop-widget.app :refer [root-view]]))

(def renderer
  (fx/create-renderer
    :middleware (fx/wrap-map-desc (fn [state]
                                    {:fx/type root-view
                                     :state state}))))

(defn reload []
  (renderer))
