(ns dev-widgets.desktop-widget.fs
  (:require [babashka.fs :as fs]))

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
        bytes (vec (fs/read-all-bytes path))]
    (->> (reduce into [(subvec bytes 0 start)
                       (mapv int value)
                       (subvec bytes end)])
         byte-array
         (fs/write-bytes path))))
