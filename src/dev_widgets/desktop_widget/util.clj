(ns dev-widgets.desktop-widget.util)

(defn add
  ([v] v)
  ([[x1 y1] [x2 y2]]
   [(+ x1 x2) (+ y1 y2)])
  ([v1 v2 & vs]
   (apply add (add v1 v2) vs)))

(defn- format-decimals-nubers [decimal-count number]
  (java.lang.String/format java.util.Locale/US (str "%." decimal-count "f") (to-array [number])))

(defn two-decimals [number]
  (format-decimals-nubers 2 number))

(defn one-decimal [number]
  (format-decimals-nubers 1 number))
