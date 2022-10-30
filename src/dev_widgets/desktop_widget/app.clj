(ns dev-widgets.desktop-widget.app
  (:require [cljfx.css :as css]))

(def style
  (css/register ::style
                {::fill "linear-gradient(to bottom, #ff0000, #6a5acd)"
                 ".test" {:-fx-fill "linear-gradient(to bottom, #00ffff, #6a5acd)"}}))

(defn color-prop [{:keys [label]}]
  {:fx/type :v-box
   :children
   [{:fx/type :label
     :alignment :center
     :text label
     :pref-width 40}
    {:fx/type :text-field
     :text "5"
     :alignment :center
     :on-text-changed prn
     :pref-width 40}]})

(defn mouse-pos []
  (let [point (.. java.awt.MouseInfo getPointerInfo getLocation)]
    [(.getX point) (.getY point)]))

(defn root-view [_]
  (let [[x y] (mouse-pos)]
    {:fx/type :stage
     :x x
     :y y
     :showing true
     :scene {:fx/type :scene
             :stylesheets [(::css/url style)]
             :root {:fx/type :v-box
                    :children [{:fx/type :rectangle
                                :width 160
                                :height 100
                                :style-class "test"}
                               {:fx/type :h-box
                                :children [(color-prop {:label "H"})
                                           (color-prop {:label "S"})
                                           (color-prop {:label "L"})
                                           (color-prop {:label "A"})]}]}}}))
