(ns dev-widgets.desktop-widget.app
  (:require [cljfx.api :as fx]
            [cljfx.css :as css]
            [cljfx.dev :as cdev]
            [clojure.string :as str]
            [com.evocomputing.colors :as colors]
            [dev-widgets.desktop-widget.util :as util]
            [clojure.java.javadoc]))

(defn- linear-gradient [color-fn steps]
  (str "linear-gradient(to right," (str/join "," (map #(colors/rgb-hexstr (colors/create-color (color-fn %))) steps))")"))

(defn style [{:keys [color hue]}]
  (css/register ::style
                {".current-color" {:-fx-fill color}
                 ".hue-gradient" {:-fx-fill (linear-gradient (fn [h] {:h h :s 100 :l 50}) (range 0 361 72))}
                 ".saturation-gradient" {:-fx-fill (linear-gradient (fn [s] {:h hue :s s :l 50}) (range 0 101 20))}
                 ".lightness-gradient" {:-fx-fill (linear-gradient (fn [l] {:h hue :s 100 :l l}) (range 0 101 20))}
                 ".bg-gray-50"	{:-fx-fill "rgb(249 250 251)"}
                 ".bg-gray-100"	{:-fx-fill "rgb(243 244 246)"}
                 ".bg-gray-200"	{:-fx-fill "rgb(229 231 235)"}
                 ".bg-gray-300"	{:-fx-fill "rgb(209 213 219)"}
                 ".bg-gray-400"	{:-fx-fill "rgb(156 163 175)"}
                 ".bg-gray-500"	{:-fx-fill "rgb(107 114 128)"}
                 ".bg-gray-600"	{:-fx-fill "rgb(75 85 99)"}
                 ".bg-gray-700"	{:-fx-fill "rgb(55 65 81)"}
                 ".bg-gray-800"	{:-fx-fill "rgb(31 41 55)"}
                 ".bg-gray-900"	{:-fx-fill "rgb(17 24 39)"}
                 ".stroke-gray-50" {:-fx-stroke "rgb(249 250 251)"}
                 ".stroke-gray-100" {:-fx-stroke "rgb(243,244,246)"}
                 ".stroke-gray-200" {:-fx-stroke "rgb(229 231 235)"}
                 ".stroke-gray-300" {:-fx-stroke "rgb(209 213 219)"}
                 ".stroke-gray-400" {:-fx-stroke "rgb(156 163 175)"}
                 ".stroke-gray-500" {:-fx-stroke "rgb(107 114 128)"}
                 ".stroke-gray-600" {:-fx-stroke "rgb(75 85 99)"}
                 ".stroke-gray-700" {:-fx-stroke "rgb(55 65 81)"}
                 ".stroke-gray-800" {:-fx-stroke "rgb(31 41 55)"}
                 ".stroke-gray-900" {:-fx-stroke "rgb(17 24 39)"}}))

(defn transition [{:keys [transition] :as desc}]
  {:fx/type fx/ext-let-refs
   :refs {::transition-node (dissoc desc :transition)}
   :desc {:fx/type fx/ext-let-refs
          :refs {::transition (assoc transition :node {:fx/type fx/ext-get-ref :ref ::transition-node})}
          :desc {:fx/type fx/ext-get-ref :ref ::transition-node}}})

(defn slider [{:keys [value max-value on-value-changed style-class label]}]
  (let [width 130
        slider-position (util/interpolate value [0 max-value] [0 width])]
    {:fx/type :h-box
     :alignment :center
     :children [{:fx/type :label
                 :alignment :center
                 :text label
                 :pref-width 20}
                {:fx/type :pane
                 :padding {:bottom 12}
                 :translate-y 5
                 :on-mouse-dragged on-value-changed
                 :on-mouse-clicked on-value-changed
                 :children [(transition
                             {:fx/type :rectangle
                              :arc-height 10
                              :arc-width 10
                              :width (+ width 10)
                              :height 10
                              :style-class style-class
                              :transition {:fx/type :scale-transition,
                                           :by-x -0.2,
                                           :duration [1 :s],
                                           :status :running}})
                            {:fx/type :rectangle
                             :arc-height 10
                             :arc-width 10
                             :x slider-position
                             :y 1
                             :width 9
                             :height 9
                             :style-class ["bg-gray-400"]}]}]}))

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
                                            :style-class "hue-gradient"
                                            :label "H"}
                                           {:fx/type slider
                                            :value (colors/saturation color)
                                            :on-value-changed {:event/type :slider-saturation :max-value 100}
                                            :max-value 100
                                            :style-class "saturation-gradient"
                                            :label "S"}
                                           {:fx/type slider
                                            :value (colors/lightness color)
                                            :on-value-changed {:event/type :slider-lightness :max-value 100}
                                            :max-value 100
                                            :style-class "lightness-gradient"
                                            :label "L"}]}
                               {:fx/type :rectangle
                                :width 160
                                :height 40
                                :style-class "current-color"}
                               {:fx/type :flow-pane
                                :max-width 160
                                :vgap 10
                                :hgap 10
                                :padding 5
                                :children (map
                                           (fn [color]
                                             {:fx/type :rectangle
                                              :width 15
                                              :height 15
                                              :style-class "stroke-gray-200"
                                              :style {:-fx-fill (-> color
                                                                    colors/create-color
                                                                    colors/rgba-hexstr)}})
                                           (repeatedly
                                            8
                                            (fn []
                                              {:h (rand-nth (range 0 360))
                                               :s (rand-nth (range 90 100))
                                               :l (rand-nth (range 40 60))})))}]}}}))
