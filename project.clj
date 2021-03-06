(defproject xmlfmt-clj "0.1.0-alpha.2"
  :description "xmlfmt-clj"
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :plugins [[lein-ancient "0.6.14"]
            [lein-ring "0.9.7"]
            [lein-try "0.4.3"]
            [test2junit "1.2.2"]]
  :main xmlfmt-clj.main
  :scm {:name "git"
        :url  "https://github.com/olecve/xmlfmt-cl"}
  :profiles {:uberjar {:aot :all}
             :dev     {:dependencies          [[org.clojure/tools.namespace "0.2.11"]]
                       :test2junit-output-dir "target/test-results"
                       :source-paths          ["dev"]}})
