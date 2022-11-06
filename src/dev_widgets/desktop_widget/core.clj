(ns dev-widgets.desktop-widget.core
  (:gen-class)
  (:require [cljfx.api :as fx]
            [dev-widgets.desktop-widget.app :refer [root-view]]
            [dev-widgets.desktop-widget.events :as events]
            [dev-widgets.desktop-widget.fs :as fs]
            [dev-widgets.desktop-widget.util :as util]))

(def *state
  (atom {:color-slider-position 0}))

(def init
  {:cursor-position 321
   :path "/Users/thomas/projects/dev-widgets/src/dev_widgets/desktop_widget/app.clj"
   :cursor-coord [2809 338]})

(defn renderer []
  (fx/create-renderer
   :middleware (fx/wrap-map-desc (fn [state]
                                   (merge {:fx/type root-view}
                                          (let [{:keys [path cursor-position cursor-coord]} init]
                                            {:position cursor-position
                                             :path path
                                             :start-pos (util/add cursor-coord [10 20])
                                             :color (fs/read-value path cursor-position)})
                                          state)))
   :opts {:fx.opt/map-event-handler #(swap! *state (events/handler %))}))

(defn -main [& args]
  (let [[path position start-pos] args]
    (swap! *state assoc
           :color (fs/read-value path position)
           :position position
           :path path
           :color-slider-position 0
           :start-pos (util/add start-pos [10 20])))
  (fx/mount-renderer *state (renderer)))

(comment
  (-main
   "/Users/thomas/projects/dev-widgets/src/dev_widgets/desktop_widget/app.clj"
   231)
  (dev-widgets.desktop-widget.core/-main "/Users/thomas/projects/dev-widgets/src/dev_widgets/desktop_widget/app.clj" 231))
