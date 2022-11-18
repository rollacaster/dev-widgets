(ns dev-widgets.desktop-widget.app
  (:require [cljfx.css :as css]
            [cljfx.dev :as cdev]
            [clojure.string :as str]
            [com.evocomputing.colors :as colors]
            [dev-widgets.desktop-widget.util :as util]))

(defn- linear-gradient [color-fn steps]
  (str "linear-gradient(to right," (str/join "," (map #(colors/rgb-hexstr (colors/create-color (color-fn %))) steps))")"))

(defn style [{:keys [color hue]}]
  (css/register ::style
                {".current-color" {:-fx-fill color}
                 ".hue-gradient" {:-fx-fill (linear-gradient (fn [h] {:h h :s 100 :l 50}) (range 0 361 72))}
                 ".saturation-gradient" {:-fx-fill (linear-gradient (fn [s] {:h hue :s s :l 50}) (range 0 101 20))}
                 ".lightness-gradient" {:-fx-fill (linear-gradient (fn [l] {:h hue :s 100 :l l}) (range 0 101 20))}
                 ".bg-slate-400" {:-fx-fill "rgb(148,163,184)"}}))
(defn color-prop [{:keys [label value on-key-pressed]}]
  {:fx/type :v-box
   :children
   [{:fx/type :label
     :alignment :center
     :text label
     :pref-width 40}
    {:fx/type :text-field
     :text (str value)
     :alignment :center
     :on-key-pressed on-key-pressed
     :pref-width 40}]})

(defn slider [{:keys [value max-value on-value-changed style-class]}]
  (let [slider-position (util/interpolate value [0 max-value] [0 150])]
    {:fx/type :pane
     :on-mouse-dragged on-value-changed
     :on-mouse-clicked on-value-changed
     :children [{:fx/type :rectangle
                 :arc-height 10
                 :arc-width 10
                 :width 160
                 :height 10
                 :style-class style-class}
                {:fx/type :rectangle
                 :arc-height 10
                 :arc-width 10
                 :x slider-position
                 :width 10
                 :height 10
                 :style-class ["bg-slate-400"]}]}))

(defn root-view [{:keys [color start-pos]}]
  (let [[x y] start-pos
        stylesheet (::css/url (style {:color (colors/rgb-hexstr color)
                                      :hue (colors/hue color)}))]
    {:fx/type :stage
     :always-on-top true
     :x x
     :y y
     :showing true
     :scene {:fx/type :scene
             :stylesheets [stylesheet]
             :root {:fx/type :v-box
                    :children [{:fx/type :v-box
                                :children [{:fx/type slider
                                            :value (colors/hue color)
                                            :on-value-changed {:event/type :slider-hue :max-value 359}
                                            :max-value 359
                                            :style-class "hue-gradient"}
                                           {:fx/type slider
                                            :value (colors/saturation color)
                                            :on-value-changed {:event/type :slider-saturation :max-value 100}
                                            :max-value 100
                                            :style-class "saturation-gradient"}
                                           {:fx/type slider
                                            :value (colors/lightness color)
                                            :on-value-changed {:event/type :slider-lightness :max-value 100}
                                            :max-value 100
                                            :style-class "lightness-gradient"}]}
                               {:fx/type :rectangle
                                :arc-height 10
                                :arc-width 10
                                :width 160
                                :height 160
                                :style-class "current-color"}
                               {:fx/type :h-box
                                :children [{:fx/type color-prop :label "H" :value (colors/hue color) :on-key-pressed {:event/type :key-pressed-hue}}
                                           {:fx/type color-prop :label "S" :value (colors/saturation color) :on-key-pressed {:event/type :key-pressed-saturation}}
                                           {:fx/type color-prop :label "L" :value (colors/lightness color) :on-key-pressed {:event/type :key-pressed-lightness}}]}]}}}))
