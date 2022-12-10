(ns dev-widgets.desktop-widget.context
  (:require [com.evocomputing.colors :as colors]
            [dev-widgets.desktop-widget.util :as util]
            [rewrite-clj.zip :as z]))

(defn in-form? [zloc pos]
  (let [[[start-row] [end-row]] (z/position-span zloc)
        [target-row] pos]
    (<= start-row target-row end-row)))

(defn toplevel-form [position path]
  (loop [zloc (z/of-file path {:track-position? true})]
    (if (in-form? zloc position)
      zloc
      (recur (z/right zloc)))))

(defn wrapping-forms [position path]
  (let [toplevel (toplevel-form position path)
        end-form (z/right toplevel)]
    (loop [z-loc toplevel
           wrapping-forms []]
      (let [next-form (z/next z-loc)]
        (if (and next-form (not= end-form next-form))
          (recur
           next-form
           (cond-> wrapping-forms
             (in-form? next-form position) (conj next-form)))
          (into [toplevel] wrapping-forms))))))
(def rgba-regex #"rgba\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*([0-1](?:\.\d+)?|\.\d+)\s*\)")
(def rgb-regex #"rgb\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)")

(defn color-format [s]
  (cond
    (re-find rgba-regex s) :rgba
    (re-find rgb-regex s) :rgb
    (re-matches #"#?([a-fA-F0-9]{6}|[a-fA-F0-9]{3})" s) :hex))

(defn color-value [format s]
  (case format
    :rgba (let [[r g b a] (->> s
                               (re-find rgba-regex)
                               rest
                               (map #(and % (parse-double %))))]
            (colors/create-color {:r (int r) :g (int g) :b (int b) :a (int (* 255 a))}))
    :rgb (let [[r g b] (->> s (re-find rgb-regex) rest (map #(and % (parse-long %))))]
                   (colors/create-color {:r r :g g :b b}))
    :hex (colors/create-color s)))

(defn- find-context  [form]
  (cond
    (string? (z/sexpr form))
    (cond (color-format (z/sexpr form))
          [(z/position-span form)
           (color-format (z/sexpr form))
           (color-value (color-format (z/sexpr form)) (z/sexpr form))])
    :else nil))

(defn detect [position path]
  (let [[[[x1 y1] [_ y2]] color-format color]
        (->> (wrapping-forms position path)
             (keep find-context)
             last)]
    {:type [:color color-format]
     :position [x1 y1]
     :length (- y2 y1)
     :value color}))

(defn write [{:keys [type]} value]
  (str "\""
       (case type
         [:color :hex] (colors/rgb-hexstr value)
         [:color :rgb] (str "rgb(" (colors/red value) ", " (colors/green value) ", " (colors/blue value) ")")
         [:color :rgba] (str "rgba(" (colors/red value) ", " (colors/green value) ", " (colors/blue value) ", " (util/one-decimal (double (/ (colors/alpha value) 255))) ")"))
       "\""))
