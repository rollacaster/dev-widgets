(ns dev-widgets.desktop-widget.core
  (:require [cljfx.api :as fx]
            [cljfx.css :as css]
            [cljfx.dev :as dev]))

;; I want to build an interactive chart that shows how bouncing object falls
;; on the ground. I want to be able to edit gravity and friction to see how
;; it affects object's behavior, so I will put it into state:

(def *state
  (atom {:gravity 10
         :friction 0.4}))

;; I want to have map event handlers extensible during runtime to avoid full app
;; restarts. One way is using vars instead of functions to get that kind of
;; behavior, but I'll go with another way: multi-methods.

(defmulti event-handler :event/type)

;; Now we'll create our app with dummy root view

(def style
  (css/register ::style
                {::fill "linear-gradient(to bottom, #ff7f50, #6a5acd)"
                 ".test" {:-fx-fill "linear-gradient(to bottom, #ff7f50, #6a5acd)"}}))

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

(defn root-view [{{:keys [gravity friction]} :state}]
  {:fx/type :stage
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
                                         (color-prop {:label "A"})]}]}}})

(def renderer
  (fx/create-renderer
    :middleware (fx/wrap-map-desc (fn [state]
                                    {:fx/type root-view
                                     :state state}))
    :opts {:fx.opt/map-event-handler event-handler}))

(fx/mount-renderer *state renderer)
