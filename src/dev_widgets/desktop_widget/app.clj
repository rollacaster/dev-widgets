(ns dev-widgets.desktop-widget.app
  (:require [cljfx.css :as css]
            [cljfx.dev :as cdev]
            [com.evocomputing.colors :as colors]))

(defn style [color]
  (css/register ::style
                {".test-color" {:-fx-fill color}
                 ".rainbow-gradient" {:-fx-fill "linear-gradient(to right, #ff0000 0%, #ff0 17%, #0f0 33%, #0ff 50%, #00f 67%, #f0f 83%, #f00 100%)"}
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

(defn root-view [{:keys [color color-slider-position start-pos position path]}]
  (let [[x y] start-pos
        stylesheet (::css/url (style (colors/rgb-hexstr color)))]
    {:fx/type :stage
     :always-on-top true
     :x x
     :y y
     :showing true
     :scene {:fx/type :scene
             :stylesheets [stylesheet]
             :root {:fx/type :v-box
                    :children [{:fx/type :pane
                                :on-mouse-dragged {:event/type :update-color :color color :position position :path path}
                                :on-mouse-clicked {:event/type :update-color :color color :position position :path path}
                                :children [{:fx/type :rectangle
                                            :arc-height 10
                                            :arc-width 10
                                            :width 160
                                            :height 160
                                            :style-class "test-color"}
                                           {:fx/type :rectangle
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
                                           (color-prop {:label "L" :value (colors/lightness color)})]}]}}}))
