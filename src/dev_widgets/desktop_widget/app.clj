(ns dev-widgets.desktop-widget.app
  (:require [cljfx.api :as fx]
            [cljfx.css :as css]
            [clojure.java.shell :as sh]
            [clojure.string :as str]
            [com.evocomputing.colors :as colors]
            [dev-widgets.desktop-widget.util :as util]
            [thi.ng.math.core :as math]))

(defn- linear-gradient [color-fn steps]
  (str "linear-gradient(to right," (str/join "," (map #(colors/rgb-hexstr (colors/create-color (color-fn %))) steps))")"))

(defn style [{:keys [color hue]}]
  (css/register ::style
                {".current-color" {:-fx-fill color}
                 ".hue-gradient" {:-fx-fill (linear-gradient (fn [h] {:h h :s 100 :l 50}) (range 0 361 72))}
                 ".saturation-gradient" {:-fx-fill (linear-gradient (fn [s] {:h hue :s s :l 50}) (range 0 101 20))}
                 ".lightness-gradient" {:-fx-fill (linear-gradient (fn [l] {:h hue :s 100 :l l}) (range 0 101 20))}
                 ".bg-gray-50"	{:-fx-background-color "rgb(249, 250, 251)"}
                 ".bg-gray-100"	{:-fx-background-color "rgb(243, 244, 246)"}
                 ".bg-gray-200"	{:-fx-background-color "rgb(229, 231, 235)"}
                 ".bg-gray-300"	{:-fx-background-color "rgb(209, 213, 219)"}
                 ".bg-gray-400"	{:-fx-background-color "rgb(156, 163, 175)"}
                 ".bg-gray-500"	{:-fx-background-color "rgb(107, 114, 128)"}
                 ".bg-gray-600"	{:-fx-background-color "rgb(75, 85, 99)"}
                 ".bg-gray-700"	{:-fx-background-color "rgb(55, 65, 81)"}
                 ".bg-gray-800"	{:-fx-background-color "rgb(31, 41, 55)"}
                 ".bg-gray-900"	{:-fx-background-color "rgb(17, 24, 39)"}
                 ".stroke-gray-50" {:-fx-stroke "rgb(249 250 251)"}
                 ".stroke-gray-100" {:-fx-stroke "rgb(243,244,246)"}
                 ".stroke-gray-200" {:-fx-stroke "rgb(229, 231, 235)"}
                 ".stroke-gray-300" {:-fx-stroke "rgb(209, 213, 219)"}
                 ".stroke-gray-400" {:-fx-stroke "rgb(156, 163, 175)"}
                 ".stroke-gray-500" {:-fx-stroke "rgb(107, 114, 128)"}
                 ".stroke-gray-600" {:-fx-stroke "rgb(75, 85, 99)"}
                 ".stroke-gray-700" {:-fx-stroke "rgb(55, 65, 81)"}
                 ".stroke-gray-800" {:-fx-stroke "rgb(31, 41, 55)"}
                 ".stroke-gray-900" {:-fx-stroke "rgb(17, 24, 39)"}
                 ".text-gray-50"	 {:-fx-text-fill "rgb(249, 250, 251)"}
                 ".text-gray-100" {:-fx-text-fill "rgb(243, 244, 246)"}
                 ".text-gray-200" {:-fx-text-fill "rgb(229, 231, 235)"}
                 ".text-gray-300" {:-fx-text-fill "rgb(209, 213, 219)"}
                 ".text-gray-400" {:-fx-text-fill "rgb(156, 163, 175)"}
                 ".text-gray-500" {:-fx-text-fill "rgb(107, 114, 128)"}
                 ".text-gray-600" {:-fx-text-fill "rgb(75, 85, 99)"}
                 ".text-gray-700" {:-fx-text-fill "rgb(55, 65, 81)"}
                 ".text-gray-800" {:-fx-text-fill "rgb(31, 41, 55)"}
                 ".text-gray-900" {:-fx-text-fill "rgb(17, 24, 39)"}
                 ".stroke-0" {:-fx-stroke-width 0}
                 ".stroke-1" {:-fx-stroke-width 1}
                 ".stroke-2" {:-fx-stroke-width 2}}))

(def width 280)

(defn transition [{:keys [transition stack-pane/alignment stack-pane/margin] :as desc}]
  (cond->
      {:fx/type fx/ext-let-refs
       :refs {::transition-node (dissoc desc :transition :stack-pane/alignment :stack-pane/margin)}
       :desc {:fx/type fx/ext-let-refs
              :refs {::transition (assoc transition :node {:fx/type fx/ext-get-ref :ref ::transition-node})}
              :desc {:fx/type fx/ext-get-ref :ref ::transition-node}}}
    alignment (assoc :stack-pane/alignment alignment)
    margin (assoc :stack-pane/margin margin)))

(defn fade-in [desc]
  (transition
   (assoc desc :transition {:fx/type :fade-transition
                            :from-value 0.0
                            :to-value 1.0
                            :duration [300 :ms]
                            :status :running})))

(defn slider-track [{:keys [width on-value-changed style-class]}]
  {:fx/type :grid-pane
   :alignment :center
   :on-mouse-dragged on-value-changed
   :on-mouse-clicked on-value-changed
   :children [{:fx/type :rectangle
                :arc-height 10
                :arc-width 10
                :width width
                :height 14
                :style-class style-class}]})

(defn- slider-label [{:keys [shortcut label position]}]
  (fade-in
   {:fx/type :group
    :stack-pane/alignment position
    :children [{:fx/type :h-box
                :padding {(if (str/includes? (name position) "left")
                            :left
                            :right) 10}
                :children [{:fx/type :label
                            :style-class ["text-gray-800" "bg-gray-200"]
                            :style {:-fx-font-size 10
                                    :-fx-background-radius 4
                                    :-fx-padding [0 2 0 2]}
                            :text shortcut}
                           {:fx/type :label
                            :style-class ["text-gray-600" "bg-gray-300"]
                            :style {:-fx-font-size 10
                                    :-fx-background-radius 4
                                    :-fx-padding [0 2 0 2]}
                            :text label}]}]}))

(defn color-slider [{:keys [value max-value on-value-changed style-class active color]}]
  (let [width (- width 90)
        height (if active 54 40)
        slider-position (math/map-interval value [0 max-value] [0 (- width 10)])]
    {:fx/type :grid-pane
     :pref-height height
     :children [{:fx/type :stack-pane
                 :pref-height height
                 :children (cond-> [{:fx/type slider-track
                                     :fx/key active
                                     :width width
                                     :stack-pane/alignment :center
                                     :on-value-changed on-value-changed
                                     :style-class style-class
                                     :active active
                                     :value value
                                     :max-value max-value}
                                    {:fx/type :pane
                                     :children [{:fx/type :rectangle
                                                 :arc-height 15
                                                 :arc-width 15
                                                 :x slider-position
                                                 :y (if active 18 12)
                                                 :width 16
                                                 :height 16
                                                 :style {:-fx-fill (colors/rgb-hexstr color)}
                                                 :style-class ["stroke-gray-200"
                                                               "stroke-2"]}]}]
                             active (conj
                                     (slider-label
                                      {:shortcut "^ A"
                                       :label "-"
                                       :position :top-left})
                                     (slider-label
                                      {:shortcut "^ S"
                                       :label "--"
                                       :position :bottom-left})
                                     (slider-label
                                      {:shortcut "^ D"
                                       :label "+"
                                       :position :top-right})
                                     (slider-label
                                      {:shortcut "^ W"
                                       :label "++"
                                       :position :bottom-right})))
                 :grid-pane/column 0}]}))

(defn color-picker [{:keys [color focus]}]
  [{:fx/type :label
    :text "H"
    :grid-pane/halignment :center
    :grid-pane/row 0
    :grid-pane/column 0}
   {:fx/type color-slider
    :color color
    :active (= focus 0)
    :value (colors/hue color)
    :on-value-changed {:event/type :slider-hue :max-value 359}
    :max-value 359
    :style-class "hue-gradient"
    :grid-pane/row 0
    :grid-pane/column 1
    :grid-pane/valignment :center}
   {:fx/type :label
    :text (util/two-decimals (colors/hue color))
    :grid-pane/row 0
    :grid-pane/halignment :center
    :grid-pane/column 2}
   {:fx/type :label
    :text "S"
    :alignment :center
    :grid-pane/row 1
    :grid-pane/column 0
    :grid-pane/halignment :center}
   {:fx/type color-slider
    :color color
    :active (= focus 1)
    :value (colors/saturation color)
    :on-value-changed {:event/type :slider-saturation :max-value 100}
    :max-value 100
    :style-class "saturation-gradient"
    :grid-pane/row 1
    :grid-pane/column 1}
   {:fx/type :label
    :text (util/two-decimals (colors/saturation color))
    :grid-pane/row 1
    :grid-pane/halignment :center
    :grid-pane/column 2}
   {:fx/type :label
    :text "L"
    :grid-pane/halignment :center
    :grid-pane/row 2
    :grid-pane/column 0}
   {:fx/type color-slider
    :color color
    :active (= focus 2)
    :value (colors/lightness color)
    :on-value-changed {:event/type :slider-lightness :max-value 100}
    :max-value 100
    :style-class "lightness-gradient"
    :grid-pane/row 2
    :grid-pane/column 1}
   {:fx/type :label
    :text (util/two-decimals (colors/lightness color))
    :grid-pane/row 2
    :grid-pane/halignment :center
    :grid-pane/column 2}])

(defn root-view [{:keys [context start-pos focus]}]
  (let [{:keys [value]} context
        [x y] start-pos
        stylesheet (::css/url (style {:color (colors/rgb-hexstr value)
                                      :hue (colors/hue value)}))]
    {:fx/type :stage
     :style :transparent
     :always-on-top true
     :on-close-request (fn [_] (sh/sh "open" "-a" "Emacs"))
     :x x
     :y y
     :showing true
     :width width
     :scene {:fx/type :scene
             :on-key-pressed {:event/type :key-pressed-scene}
             :stylesheets [stylesheet]

             :root {:fx/type :v-box
                    :style-class ["bg-gray-100"]
                    :children [{:fx/type :grid-pane
                                :hgap 10
                                :padding 6
                                :column-constraints [{:fx/type :column-constraints
                                                      :percent-width 0.05}
                                                     {:fx/type :column-constraints
                                                      :percent-width 0.75}
                                                     {:fx/type :column-constraints
                                                      :percent-width 0.2}]
                                :children (color-picker {:color value :focus focus})}]}}}))
