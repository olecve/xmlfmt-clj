(ns xmlfmt-clj.core
  (:require [clojure.java.io :as io]
            [clojure.xml :as xml])
  (:gen-class))

(defn string->input-stream [s] (-> s (.getBytes "UTF-8") (io/input-stream)))

(defn gen-indent [level nr-of-spaces] (->> " " (repeat (* level nr-of-spaces)) (apply str)))

(defn p [& more] (print (apply str more)))

(defn p-declaration [] (p "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"))

(defn p-attr [attr-indent attr] (p attr-indent (name (key attr)) "=\"" (val attr) "\"\n"))

(defn p-attrs [{:keys [attrs]} level]
  (when attrs
    (let [attr-indent (gen-indent (+ 1 level) 2)]
      (p "\n")
      (doseq [attr (into (sorted-map) attrs)]
        (p-attr attr-indent attr)))))

(defn el->tag [el] (name (:tag el)))

(defn p-el [el level]
  (let [indent (gen-indent level 2)]
    (if (string? el)
      (p indent el "\n")
      (let [tag (el->tag el)]
        (p indent "<" tag)
        (p-attrs el level)
        (if-let [content (:content el)]
          (do
            (p indent ">\n")
            (doseq [inner-el content]
              (p-el inner-el (inc level)))
            (p indent "</" tag ">\n"))
          (p indent "/>\n"))))))

(defn format-string [str]
  (let [data (xml/parse (string->input-stream str))]
    (p-declaration)
    (p-el data 0)))

(defn find-files [folder]
  (->> (clojure.java.io/file folder)
       file-seq
       (filter #(.isFile %))
       (filter #(clojure.string/ends-with? (.getFileName (.toPath %)) ".xml"))
       (mapv #(.getAbsolutePath %))))

(defn format-file [file-name]
  (let [input-stream (io/input-stream file-name)
        parsed (xml/parse input-stream)]
    (with-open [output (clojure.java.io/writer file-name)]
      (binding [*out* output]
        (p-declaration)
        (p-el parsed 0)))))

(defn format-files [folder]
  (doseq [file-name (find-files folder)]
    (do (println file-name)
        (format-file file-name))))

(defn -main []
  (println "not implemented yet"))
