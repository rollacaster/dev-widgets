(ns dev-widgets.desktop-widget.position-span)

(defn length [[[_ y1] [_ y2]]]
  (- y2 y1))

(defn update-length [[[x1 y1] [x2 _]] length]
  [[x1 y1] [x2 (+ y1 length)]])

(defn col-start [[[_ y1]]]
  y1)

(defn row-start [[[x1]]]
  x1)
