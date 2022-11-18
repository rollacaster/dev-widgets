(ns user
  (:require [dev-widgets.desktop-widget.core :refer [current-renderer *state]]))

(defn reload []
  (swap! *state assoc :start-pos [0 0])
  (@current-renderer))
