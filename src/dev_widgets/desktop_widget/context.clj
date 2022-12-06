(ns dev-widgets.desktop-widget.context
  (:require [rewrite-clj.zip :as z]
            [com.evocomputing.colors :as colors]))

(defn in-form? [zloc pos]
  (let [[[start-row start-col] [end-row end-col]] (z/position-span zloc)
        [target-row target-col] pos]
    (and (<= start-row target-row end-row)
         (<= start-col target-col end-col))))

(defn toplevel-form [pos path]
  (loop [zloc (z/of-file path {:track-position? true})]
    (if (in-form? zloc pos)
      zloc
      (recur (z/right zloc)))))

(defn wrapping-forms [pos path]
  (let [toplevel (toplevel-form pos path)
        end-form (z/right toplevel)]
    (loop [z-loc toplevel
           wrapping-forms []]
      (let [next-form (z/next z-loc)]
        (if (and next-form (not= end-form next-form))
          (recur
           next-form
           (cond-> wrapping-forms
             (in-form? next-form pos) (conj next-form)))
          (into [toplevel] wrapping-forms))))))

(defn ->color [pos path]
  (let [[[[x1 y1] [_ y2]] color] (->> (wrapping-forms pos path)
                                      (filter #(= (z/tag %) :token))
                                      (map (juxt z/position-span (comp colors/create-color z/sexpr)))
                                      first)]

    {:type :color
     :position [x1 y1]
     :length (- y2 y1)
     :value color}))
