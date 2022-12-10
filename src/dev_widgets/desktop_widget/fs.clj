(ns dev-widgets.desktop-widget.fs
  (:require [babashka.fs :as fs]))

(defn write-value [path {:keys [position length]} value]
  (let [[row col] position]
    (fs/write-lines
     path
     (-> (fs/read-all-lines path)
         (update (dec row) (fn [line]
                             (str
                              (subs line 0 (dec col))
                              value
                              (subs line (+ col (dec length))))))))))
