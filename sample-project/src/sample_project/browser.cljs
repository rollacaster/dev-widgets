(ns sample-project.browser
  (:require [reagent.core :as r]
            [reagent.dom :as dom]))

(defn app []
  [:div {:style {:width 100
                 :height 100
                 :background-color "#A17DE8"}}])

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (js/console.log "start")
  (dom/render [app] (.getElementById js/document "app")))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (js/document.getElementById "box")
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
