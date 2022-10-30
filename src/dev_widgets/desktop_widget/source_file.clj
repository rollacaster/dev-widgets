(ns dev-widgets.desktop-widget.source-file
  (:require [babashka.fs :as fs]
            [dev-widgets.desktop-widget.renderer :refer [reload]]))

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
    (use (symbol ns) :reload)
    (reload)))

(comment
  (read-value "/Users/thomas/projects/dev-widgets/src/dev_widgets/desktop_widget/app.clj" 290)
  (write-value "/Users/thomas/projects/dev-widgets/src/dev_widgets/desktop_widget/app.clj" 290 "#ff00ff"))
