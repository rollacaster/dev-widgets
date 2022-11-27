(ns dev-widgets.desktop-widget.app
  (:require [cljfx.api :as fx]
            [cljfx.css :as css]
            [clojure.string :as str]
            [com.evocomputing.colors :as colors]
            [thi.ng.math.core :as math]))

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

(def width 200)

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

(defn slider-track [{:keys [width on-value-changed style-class active]}]
  {:fx/type :grid-pane
   :alignment :center
   :on-mouse-dragged on-value-changed
   :on-mouse-clicked on-value-changed
   :children [(transition
               {:fx/type :rectangle
                :arc-height 10
                :arc-width 10
                :width width
                :height 10
                :style-class style-class
                :transition (if active
                              {:fx/type :scale-transition,
                               :from-x 1
                               :to-x 0.8
                               :duration [300 :ms],
                               :status :running}
                              {:fx/type :scale-transition,
                               :from-x 0.8
                               :to-x 1
                               :duration [300 :ms],
                               :status :running})})]})

(defn color-slider [{:keys [value max-value on-value-changed style-class active ]}]
  (let [width (- width 25)
        height 40
        slider-position (math/map-interval value
                                           [0 max-value]
                                           [(if active (* width 0.1) 0)
                                            (- (if active (* width 0.9) width) 10)])]
    {:fx/type :grid-pane
     :alignment :center
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
                                                 :arc-height 10
                                                 :arc-width 10
                                                 :x slider-position
                                                 :y 15
                                                 :width 10
                                                 :height 10
                                                 :style-class ["bg-gray-400"]}]}]
                             active (conj
                                     (fade-in
                                      {:fx/type :label
                                       :stack-pane/alignment :top-left
                                       :padding {:left 15}
                                       :style {:-fx-font-size 11}
                                       :text "-1 C-a"})
                                     (fade-in
                                      {:fx/type :label
                                       :stack-pane/alignment :bottom-left
                                       :padding {:left 15}
                                       :style {:-fx-font-size 11}
                                       :text "-10 C-s"})
                                     (fade-in
                                      {:fx/type :label
                                       :stack-pane/alignment :top-right
                                       :padding {:right 15}
                                       :style {:-fx-font-size 11}
                                       :text "+1 C-d"})
                                     (fade-in
                                      {:fx/type :label
                                       :stack-pane/alignment :bottom-right
                                       :padding {:right 15}
                                       :style {:-fx-font-size 11}
                                       :text "+10 C-w"})))
                 :grid-pane/column 0}
                #_{:fx/type :label
                 :text (subs (str value) 0 4)
                 :grid-pane/column 1}]}))

(defn root-view [{:keys [color start-pos focus]}]
  (let [[x y] start-pos
        stylesheet (::css/url (style {:color (colors/rgb-hexstr color)
                                      :hue (colors/hue color)}))]
    {:fx/type :stage
     :always-on-top true
     :x x
     :y y
     :showing true
     :scene {:fx/type :scene
             :on-key-pressed {:event/type :key-pressed-scene}
             :stylesheets [stylesheet]
             :root {:fx/type :v-box
                    :children [{:fx/type :grid-pane
                                :children [{:fx/type :label
                                            :text "H"
                                            :grid-pane/halignment :center
                                            :grid-pane/row 0
                                            :grid-pane/column 0}
                                           {:fx/type color-slider
                                            :active (= focus 0)
                                            :value (colors/hue color)
                                            :on-value-changed {:event/type :slider-hue :max-value 359}
                                            :max-value 359
                                            :style-class "hue-gradient"
                                            :grid-pane/row 0
                                            :grid-pane/column 1
                                            :grid-pane/valignment :center}
                                           {:fx/type :label
                                            :text "S"
                                            :alignment :center
                                            :grid-pane/row 1
                                            :grid-pane/column 0
                                            :grid-pane/halignment :center}
                                           {:fx/type color-slider
                                            :active (= focus 1)
                                            :value (colors/saturation color)
                                            :on-value-changed {:event/type :slider-saturation :max-value 100}
                                            :max-value 100
                                            :style-class "saturation-gradient"
                                            :grid-pane/row 1
                                            :grid-pane/column 1}
                                           {:fx/type :label
                                            :text "L"
                                            :grid-pane/halignment :center
                                            :grid-pane/row 2
                                            :grid-pane/column 0}
                                           {:fx/type color-slider
                                            :active (= focus 2)
                                            :value (colors/lightness color)
                                            :on-value-changed {:event/type :slider-lightness :max-value 100}
                                            :max-value 100
                                            :style-class "lightness-gradient"
                                            :grid-pane/row 2
                                            :grid-pane/column 1}]}
                               {:fx/type :rectangle
                                :width width
                                :height 40
                                :style-class "current-color"}
                               #_{:fx/type :flow-pane
                                  :max-width width
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
