(ns dev-widgets.desktop-widget.colors
  (:require [com.evocomputing.colors :as colors]))

(defn set-hue [color hue]
  (colors/create-color
   {:h hue
    :s (colors/saturation color)
    :l (colors/lightness color)
    :a (colors/alpha color)}))

(defn set-saturation [color saturation]
  (colors/create-color
   {:h (colors/hue color)
    :s saturation
    :l (colors/lightness color)
    :a (colors/alpha color)}))

(defn set-lightness [color lightness]
  (colors/create-color
   {:h (colors/hue color)
    :s (colors/saturation color)
    :l lightness
    :a (colors/alpha color)}))
