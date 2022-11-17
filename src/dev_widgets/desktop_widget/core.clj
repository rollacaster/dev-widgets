(ns dev-widgets.desktop-widget.core
  (:require [cljfx.api :as fx]
            [com.evocomputing.colors :as colors]
            [dev-widgets.desktop-widget.app :refer [root-view]]
            [dev-widgets.desktop-widget.events :as events]
            [dev-widgets.desktop-widget.fs :as fs]
            [dev-widgets.desktop-widget.util :as util]
            [nrepl.server :as nrepl]))

(defonce server (nrepl/start-server :port 7888))

(def *state
  (atom {:color-slider-position 0}))

(defn renderer []
  (fx/create-renderer
   :middleware (fx/wrap-map-desc (fn [state] (merge {:fx/type root-view} state)))
   :opts {:fx.opt/map-event-handler #(swap! *state (events/handler %))}))

(defn start! [{:keys [position path start-pos]}]
  (let [color (colors/create-color (fs/read-value path position))]
    (swap! *state assoc
           :color color
           :position position
           :path path
           :color-slider-position (util/interpolate (colors/hue color)
                                                    [0 359]
                                                    [0 150])
           :start-pos (util/add start-pos [10 20])))
  (fx/mount-renderer *state (renderer)))
