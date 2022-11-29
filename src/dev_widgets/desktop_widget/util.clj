(ns dev-widgets.desktop-widget.util)

(defn add
  ([v] v)
  ([[x1 y1] [x2 y2]]
   [(+ x1 x2) (+ y1 y2)])
  ([v1 v2 & vs]
   (apply add (add v1 v2) vs)))

(defn two-decimals [number]
  (java.lang.String/format java.util.Locale/US "%.2f" (to-array [number])))
