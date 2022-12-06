(ns dev-widgets.desktop-widget.fs
  (:require [babashka.fs :as fs]))

(defn read-value [path [row col]]
  (apply str
         (subvec (vec (nth (fs/read-all-lines path) (dec row)))
                 col
                 (+ col 7))))

(defn write-value [path [row col] value]
  (fs/write-lines
   path
   (-> (fs/read-all-lines path)
       (update (dec row) (fn [line]
                           (str
                            (subs line 0 col)
                            value
                            (subs line (+ col 7))))))))
