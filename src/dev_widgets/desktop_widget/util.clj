(ns dev-widgets.desktop-widget.util)

(defn interpolate [x [from-domain to-domain] [from-range to-range]]
  (float
   (* (if (zero? x) 0 (/ x (- to-domain from-domain)))
      (- to-range from-range))))

(defn add
  ([v] v)
  ([[x1 y1] [x2 y2]]
   [(+ x1 x2) (+ y1 y2)])
  ([v1 v2 & vs]
   (apply add (add v1 v2) vs)))
