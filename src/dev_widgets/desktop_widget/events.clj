(ns dev-widgets.desktop-widget.events
  (:require [com.evocomputing.colors :as colors]
            [dev-widgets.desktop-widget.fs :as fs]
            [dev-widgets.desktop-widget.util :as util]))

(defn- update-color-component [event {:keys [range component]}]
  (fn [state]
    (let [position (min (- 160 10) (max 0 (.getX (:fx/event event))))
          color (:color event)
          value (util/interpolate position [0 150] range)
          new-color (colors/create-color
                     (assoc
                      {:h (colors/hue color)
                       :s (colors/saturation color)
                       :l (colors/lightness color)}
                      component value))]
      (fs/write-value
       (:path event)
       (:position event)
       (colors/rgb-hexstr new-color))
      (assoc state :color new-color))))

(defn key-pressed [event update-color]
  (fn [{:keys [color] :as state}]
    (let [pressed-key (.getName (.getCode (:fx/event event)))
          ctrl? (.isControlDown (:fx/event event))]
      (cond
        (and ctrl? (= pressed-key "W")) (assoc state :color (update-color color 10))
        (and ctrl? (= pressed-key "S")) (assoc state :color (update-color color -10))
        (and ctrl? (= pressed-key "A")) (assoc state :color (update-color color -1))
        (and ctrl? (= pressed-key "D")) (assoc state :color (update-color color 1))
        :else state))))

(defn handler [event]
  (case (:event/type event)
    :key-pressed-hue
    (key-pressed event colors/adjust-hue)
    :key-pressed-saturation
    (key-pressed event colors/saturate)
    :key-pressed-lightness
    (key-pressed event colors/lighten)
    :update-hue
    (update-color-component event {:range [0 359] :component :h})
    :update-saturation
    (update-color-component event {:range [0 100] :component :s})
    :update-lightness
    (update-color-component event {:range [0 100] :component :l})))
