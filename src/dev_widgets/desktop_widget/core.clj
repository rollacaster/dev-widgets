(ns dev-widgets.desktop-widget.core
  (:gen-class)
  (:require
   [babashka.fs :as fs]
   [cljfx.api :as fx]
   [com.evocomputing.colors :as colors]
   [dev-widgets.desktop-widget.app :refer [root-view]]))

(defonce *state
  (atom {:color nil}))

(defn read-value [path pos]
  (let [start (dec pos)
        end (+ start 7)
        path  path
        bytes (vec (fs/read-all-bytes path))]
    (->> (subvec bytes start end)
         (map char)
         (apply str))))

(defn write-value [path pos value]
  (let [start (dec pos)
        end (+ start 7)
        [first-line] (fs/read-all-lines path)
        ;; TODO use a proper parser instead of a regexp
        [_ ns] (re-find #"\(ns ([\w\-.]*)" first-line)
        bytes (vec (fs/read-all-bytes path))]
    (->> (reduce into [(subvec bytes 0 start)
                       (mapv int value)
                       (subvec bytes end)])
         byte-array
         (fs/write-bytes path))
    (use (symbol ns) :reload)))

(defn interpolate [x [from-domain to-domain] [from-range to-range]]
  (float
   (* (if (zero? x) 0 (/ x (- to-domain from-domain)))
      (- to-range from-range))))


(defn map-event-handler [event]
  (case (:event/type event)
    :update-color
    (fn [state]
      (let [position (min (- 160 10) (max 0 (.getX (:fx/event event))))
            color (:color event)
            hue (interpolate position [0 150] [0 359])
            new-color (colors/rgb-hexstr (colors/create-color
                                          {:h hue
                                           :s (colors/saturation color)
                                           :l (colors/lightness color)}))]
        (write-value
         (:path event)
         (:position event)
         new-color)
        (assoc state
               :color-slider-position position
               :color new-color)))))

(defn renderer []
  (fx/create-renderer
    :middleware (fx/wrap-map-desc assoc :fx/type root-view)
    :opts {:fx.opt/map-event-handler #(swap! *state (map-event-handler %))}))

(defn reload []
  (renderer))

(defn add
  ([v] v)
  ([[x1 y1] [x2 y2]]
   [(+ x1 x2) (+ y1 y2)])
  ([v1 v2 & vs]
   (apply add (add v1 v2) vs)))

(defn -main [& args]
  (let [[path position start-pos] args]
    (swap! *state assoc
           :color (read-value path position)
           :position position
           :path path
           :color-slider-position 0
           :start-pos (add start-pos [10 20])))
  (fx/mount-renderer *state (renderer)))

(comment
  (-main
   "/Users/thomas/projects/dev-widgets/src/dev_widgets/desktop_widget/app.clj"
   231)
  (dev-widgets.desktop-widget.core/-main "/Users/thomas/projects/dev-widgets/src/dev_widgets/desktop_widget/app.clj" 231))
