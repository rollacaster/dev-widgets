(ns dev-widgets.desktop-widget.events
  (:require [com.evocomputing.colors :as colors]
            [dev-widgets.desktop-widget.fs :as fs]
            [dev-widgets.desktop-widget.util :as util]))

(defn handler [event]
  (case (:event/type event)
    :update-color
    (fn [state]
      (let [position (min (- 160 10) (max 0 (.getX (:fx/event event))))
            color (:color event)
            hue (util/interpolate position [0 150] [0 359])
            new-color (colors/rgb-hexstr (colors/create-color
                                          {:h hue
                                           :s (colors/saturation color)
                                           :l (colors/lightness color)}))]
        (fs/write-value
         (:path event)
         (:position event)
         new-color)
        (assoc state
               :color-slider-position position
               :color new-color)))))
