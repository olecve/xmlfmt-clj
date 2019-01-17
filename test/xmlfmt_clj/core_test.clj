(ns xmlfmt-clj.core-test
  (:require [clojure.test :refer :all]
            [xmlfmt-clj.core :as xmlfmt]))

(deftest gen-indent-test
  (is (= "" (xmlfmt/gen-indent 0 0)))
  (is (= " " (xmlfmt/gen-indent 1 1)))
  (is (= "  " (xmlfmt/gen-indent 1 2)))
  (is (= "  " (xmlfmt/gen-indent 2 1))))

(deftest format-string-test
  (testing "single tag"
    (let [expected (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        "<tag/>\n")]
      (is (= expected (with-out-str (xmlfmt/format-string "   <tag />   "))))
      (is (= expected (with-out-str (xmlfmt/format-string "   <tag></tag>   "))))
      (is (= expected (with-out-str (xmlfmt/format-string "   <tag>   </tag>   "))))))

  (testing "single tag with attribute"
    (let [expected (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        "<tag\n"
                        "  attr1=\"value1\"\n"
                        "/>\n")]
      (is (= expected (with-out-str (xmlfmt/format-string "   <tag attr1=\"value1\" />   "))))
      (is (= expected (with-out-str (xmlfmt/format-string "   <tag attr1=\"value1\"></tag>   "))))
      (is (= expected (with-out-str (xmlfmt/format-string "   <tag   attr1   =   \"value1\"   >   </tag>   "))))))

  (testing "single tag with multiple attributes"
    (let [expected (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        "<tag\n"
                        "  a-key=\"a-val\"\n"
                        "  b-key=\"b-val\"\n"
                        "/>\n")]
      (is (= expected (with-out-str (xmlfmt/format-string "<tag a-key=\"a-val\" b-key=\"b-val\" />"))))
      (is (= expected (with-out-str (xmlfmt/format-string "<tag b-key=\"b-val\" a-key=\"a-val\" />"))))))

  (testing "single tag with string content"
    (let [expected (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        "<tag>\n"
                        "  string-content\n"
                        "</tag>\n")]
      (is (= expected (with-out-str (xmlfmt/format-string "<tag>string-content</tag>"))))))

  (testing "attributes with xmlns"
    (let [expected (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        "<root\n"
                        "  xmlns:a=\"http://a\"\n"
                        "  xmlns:b=\"http://b\"\n"
                        "  key=\"value\"\n"
                        ">\n"
                        "  string-content\n"
                        "</root>\n")]
      (is (= expected
             (with-out-str (xmlfmt/format-string "<root key=\"value\" xmlns:b=\"http://b\" xmlns:a=\"http://a\">string-content</root>")))))))
