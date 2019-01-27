(ns xmlfmt-clj.main
  (:require [xmlfmt-clj.core :as xmlfmt])
  (:gen-class))

(defn find-files [folder]
  (->> (clojure.java.io/file folder)
       file-seq
       (filter #(.isFile %))
       (mapv #(.getAbsolutePath %))
       (filter #(re-find #".*\.xml$" %))
       (remove #(re-find #".*(\.idea|\.mvn|node_modules|target).*" %))
       (remove #(re-find #".*pom.xml$" %))))

(defn -main [& args]
  (doseq [file-name (find-files (first args))]
    (try
      (do (print file-name "... ")
          (xmlfmt/format-file file-name)
          (println "Ok"))
      (catch Exception _
        (println "Something went wrong")))))
