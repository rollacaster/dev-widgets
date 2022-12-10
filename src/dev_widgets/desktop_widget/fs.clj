(ns dev-widgets.desktop-widget.fs
  (:require [babashka.fs :as fs]))

(defn write-value [{:keys [path from to value line]}]
  (fs/write-lines
   path
   (-> (fs/read-all-lines path)
       (update (dec line) (fn [line] (str (subs line 0 (dec from))
                                         value
                                         (subs line (dec to))))))))
