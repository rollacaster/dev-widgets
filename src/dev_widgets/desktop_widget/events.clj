(ns dev-widgets.desktop-widget.events
  (:require [com.evocomputing.colors :as evo.colors]
            [dev-widgets.desktop-widget.colors :as colors]
            [dev-widgets.desktop-widget.context :as context]
            [dev-widgets.desktop-widget.fs :as fs]
            [thi.ng.math.core :as math]))

(defn- update-color [{:keys [context path] :as state} color]
  (let [new-value (context/write context color)]
    (fs/write-value path context new-value)
    (-> state
        (assoc-in [:context :value] color)
        (assoc-in [:context :length] (count new-value)))))

(defn- update-color-component [{:keys [focus context] :as state} value]
  (let [color-component (case focus 0 :hue 1 :saturate 2 :lighten)
        new-color ((case color-component
                     :hue evo.colors/adjust-hue
                     :saturate evo.colors/saturate
                     :lighten evo.colors/lighten)
                   (:value context)
                   value)]
    (update-color state new-color)))

(defn- slider [event update-color-component]
  (fn [{:keys [context] :as state}]
    (let [slider-position (min (- 160 10) (max 0 (.getX (:fx/event event))))
          value (math/map-interval slider-position [0 150] [0 (:max-value event)])
          new-color (update-color-component (:value context) value)]
      (update-color state new-color))))

(defn- key-pressed [event]
  (fn [state]
    (let [pressed-key (.getName (.getCode (:fx/event event)))
          ctrl? (.isControlDown (:fx/event event))
          shift? (.isShiftDown (:fx/event event))]
      (case (cond->> [pressed-key]
              shift? (into [:shift])
              ctrl? (into [:ctrl]))
        ["Tab"] (update state :focus (fnil (fn [focus-idx] (-> focus-idx inc (mod 3))) 0))
        [:shift "Tab"] (update state :focus (fnil (fn [focus-idx] (-> focus-idx dec (mod 3))) 0))
        [:ctrl "W"] (update-color-component state 10)
        [:ctrl "S"] (update-color-component state -10)
        [:ctrl "A"] (update-color-component state -1)
        [:ctrl "D"] (update-color-component state 1)
        state))))

(defn handler [event]
  (case (:event/type event)
    :key-pressed-scene
    (key-pressed event)
    :slider-hue
    (slider event colors/set-hue)
    :slider-saturation
    (slider event colors/set-saturation)
    :slider-lightness
    (slider event colors/set-lightness)))
