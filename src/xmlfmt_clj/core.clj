(ns xmlfmt-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :refer [starts-with?]]
            [clojure.xml :as xml]))

(defn string->input-stream [s] (-> s (.getBytes "UTF-8") (io/input-stream)))

(defn gen-indent [level nr-of-spaces] (->> " " (repeat (* level nr-of-spaces)) (apply str)))

(defn p [& more] (print (apply str more)))

(defn p-declaration [] (p "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"))

(defn p-attr [attr-indent attr] (p attr-indent (name (key attr)) "=\"" (val attr) "\"\n"))

(def ^:const xmlns "xmlns:")
(defn xmlns-attr? [attr] (starts-with? (name attr) xmlns))

(defn compare-attrs [left right]
  (cond
    (and (not (xmlns-attr? left)) (xmlns-attr? right)) 1
    (and (xmlns-attr? left) (not (xmlns-attr? right))) -1
    :else (compare left right)))

(defn p-attrs [{:keys [attrs]} level]
  (when attrs
    (let [attr-indent (gen-indent (+ 1 level) 2)]
      (p "\n")
      (doseq [attr (into (sorted-map-by #(compare-attrs %1 %2)) attrs)]
        (p-attr attr-indent attr)))))

(defn el->tag [el] (name (:tag el)))

(defn p-el [el level]
  (let [indent (gen-indent level 2)]
    (if (string? el)
      (p indent el "\n")
      (let [tag (el->tag el)
            close-indent (if (:attrs el) indent "")]
        (p indent "<" tag)
        (p-attrs el level)
        (if-let [content (:content el)]
          (do
            (p close-indent ">\n")
            (doseq [inner-el content]
              (p-el inner-el (inc level)))
            (p indent "</" tag ">\n"))
          (p close-indent "/>\n"))))))

(defn format-string [str]
  (let [data (xml/parse (string->input-stream str))]
    (p-declaration)
    (p-el data 0)))

(defn format-file [file-name]
  (let [input-stream (io/input-stream file-name)
        parsed (xml/parse input-stream)]
    (with-open [output (clojure.java.io/writer file-name)]
      (binding [*out* output]
        (p-declaration)
        (p-el parsed 0)))))
