(ns dev-widgets.desktop-widget.core
  (:require [cljfx.api :as fx]
            [dev-widgets.desktop-widget.app :refer [root-view]]
            [dev-widgets.desktop-widget.context :as context]
            [dev-widgets.desktop-widget.events :as events]
            [dev-widgets.desktop-widget.util :as util]
            [nrepl.server :as nrepl]))

(prn "Dev widgets are ready")
(defonce server (nrepl/start-server :port 7899))
(defonce current-renderer (atom nil))
(def *state
  (atom nil))

(defn renderer []
  (fx/create-renderer
   :middleware (fx/wrap-map-desc (fn [state] (merge {:fx/type root-view} state)))
   :opts {:fx.opt/map-event-handler #(swap! *state (events/handler %))}))

(defn start! [{:keys [position path start-pos]}]
  (swap! *state assoc
         :focus 0
         :context (context/detect position path)
         :start-pos (util/add start-pos [10 20]))
  (fx/mount-renderer *state (reset! current-renderer (renderer))))
