(ns dev-widgets.desktop-widget.core
  (:gen-class)
  (:require
   [cljfx.api :as fx]
   [dev-widgets.desktop-widget.renderer :refer [renderer]]
   [dev-widgets.desktop-widget.source-file :as source-file]))

(defonce *state
  (atom {:color nil}))

(defn -main [& args]
  (swap! *state assoc :color (source-file/read-value "/Users/thomas/projects/dev-widgets/src/dev_widgets/desktop_widget/app.clj" 389))
  (fx/mount-renderer *state renderer))
