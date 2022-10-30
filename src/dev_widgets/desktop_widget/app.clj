(ns dev-widgets.desktop-widget.app
  (:require [cljfx.css :as css]
            [com.evocomputing.colors :as colors]
            [cljfx.dev :as cdev]
            [dev-widgets.desktop-widget.renderer :refer [reload]]
            [dev-widgets.desktop-widget.core :refer [*state]]))

(def style
  (css/register ::style
                {".test-gradient" {:-fx-fill "linear-gradient(to bottom, #ff00ff, #6a5acd)"}
                 ".rainbow-gradient" {:-fx-fill "linear-gradient(to right, #f00 0%, #ff0 17%, #0f0 33%, #0ff 50%, #00f 67%, #f0f 83%, #f00 100%)"}
                 ".bg-slate-400" {:-fx-fill "rgb(148,163,184)"}}))

(defn color-prop [{:keys [label value]}]
  {:fx/type :v-box
   :children
   [{:fx/type :label
     :alignment :center
     :text label
     :pref-width 40}
    {:fx/type :text-field
     :text (str value)
     :alignment :center
     :on-text-changed prn
     :pref-width 40}]})

(defn mouse-pos []
  (let [point (.. java.awt.MouseInfo getPointerInfo getLocation)]
    [(.getX point) (.getY point)]))

(def start-pos (mouse-pos))

(defn root-view [{{:keys [color color-slider-position]} :state}]
  (let [[x y] start-pos
        color (colors/create-color color)]
    {:fx/type :stage
     :always-on-top true
     :x x
     :y y
     :showing true
     :scene {:fx/type :scene
             :stylesheets [(::css/url style)]
             :root {:fx/type :v-box
                    :children [{:fx/type :rectangle
                                :width 160
                                :height 100
                                :style-class "test-gradient"}
                               {:fx/type :pane
                                :on-mouse-dragged (fn [e]
                                                    (swap! *state assoc :color-slider-position (min (- 160 10) (max 0 (.getX e)))))
                                :children [{:fx/type :rectangle
                                            :arc-height 10
                                            :arc-width 10
                                            :width 160
                                            :height 10
                                            :style-class "rainbow-gradient"}
                                           {:fx/type :rectangle
                                            :arc-height 10
                                            :arc-width 10
                                            :x color-slider-position
                                            :width 10
                                            :height 10
                                            :style-class ["bg-slate-400"]}]}
                               {:fx/type :h-box
                                :children [(color-prop {:label "H" :value (colors/hue color)})
                                           (color-prop {:label "S" :value (colors/saturation color)})
                                           (color-prop {:label "L" :value (colors/lightness color)})
                                           (color-prop {:label "A"})]}]}}}))
(comment
  (reload))
