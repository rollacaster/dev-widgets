(ns dev-widget
  (:require [babashka.nrepl-client :as nrepl]
            [clojure.tools.cli :as cli]))

(def cli-options [["-pos" "--position POSITION" "Position"
                   :parse-fn #(Integer/parseInt %)]
                  ["-s" "--start-pos START-POSITION" "Start Position"
                   :parse-fn #(read-string %)]
                  ["-p" "--path PATH" "Path" ]])
(defn start! []
  (let [{:keys [position path start-pos]} (:options  (cli/parse-opts *command-line-args* cli-options))]
    (nrepl/eval-expr {:port 7899 :expr
                      (str "(dev-widgets.desktop-widget.core/start! {:position " position " :path \"" path "\" :start-pos " start-pos "})")})))
