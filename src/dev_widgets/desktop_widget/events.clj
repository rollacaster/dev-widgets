(ns dev-widgets.desktop-widget.events
  (:require [com.evocomputing.colors :as evo.colors]
            [dev-widgets.desktop-widget.colors :as colors]
            [dev-widgets.desktop-widget.fs :as fs]
            [dev-widgets.desktop-widget.util :as util]))

(defn- update-color [state {:keys [path position color]}]
  (fs/write-value path position (evo.colors/rgb-hexstr color))
  (assoc state :color color))

(defn- slider [event update-color-component]
  (fn [{:keys [color position  path] :as state}]
    (let [slider-position (min (- 160 10) (max 0 (.getX (:fx/event event))))
          value (util/interpolate slider-position [0 150] [0 (:max-value event)])
          new-color (update-color-component color value)]
      (update-color state {:path path :position position :color new-color}))))

(defn- key-pressed [event update-color-component]
  (fn [{:keys [color path position] :as state}]
    (let [pressed-key (.getName (.getCode (:fx/event event)))
          ctrl? (.isControlDown (:fx/event event))
          new-color (cond
                      (and ctrl? (= pressed-key "W")) (update-color-component color 10)
                      (and ctrl? (= pressed-key "S")) (update-color-component color -10)
                      (and ctrl? (= pressed-key "A")) (update-color-component color -1)
                      (and ctrl? (= pressed-key "D")) (update-color-component color 1)
                      :else color)]
      (update-color state {:path path :position position :color new-color}))))

(defn handler [event]
  (case (:event/type event)
    :key-pressed-hue
    (key-pressed event evo.colors/adjust-hue)
    :key-pressed-saturation
    (key-pressed event evo.colors/saturate)
    :key-pressed-lightness
    (key-pressed event evo.colors/lighten)
    :slider-hue
    (slider event colors/set-hue)
    :slider-saturation
    (slider event colors/set-saturation)
    :slider-lightness
    (slider event colors/set-lightness)))
