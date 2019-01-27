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
             (with-out-str (xmlfmt/format-string "<root key=\"value\" xmlns:b=\"http://b\" xmlns:a=\"http://a\">string-content</root>"))))))

  (testing "attributes with different prefixes"
    (let [expected (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        "<root\n"
                        "  xmlns:sling=\"xmlns\"\n"
                        "  jcr:mixin=\"jcr\"\n"
                        "/>\n")]
      (is (= expected
             (with-out-str (xmlfmt/format-string "<root xmlns:sling=\"xmlns\" jcr:mixin=\"jcr\"/>"))))
      (is (= expected
             (with-out-str (xmlfmt/format-string "<root jcr:mixin=\"jcr\" xmlns:sling=\"xmlns\"/>"))))))

  (testing "multiple children"
    (let [expected (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        "<level-1>\n"
                        "  <level-2/>\n"
                        "</level-1>\n")]
      (is (= expected
             (with-out-str (xmlfmt/format-string "<level-1><level-2 /></level-1>"))))
      (is (= expected
             (with-out-str (xmlfmt/format-string "<level-1   ><level-2   />   </level-1>")))))

    (let [expected (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        "<level-1>\n"
                        "  <level-2>\n"
                        "    <level-3/>\n"
                        "  </level-2>\n"
                        "</level-1>\n")]
      (is (= expected
             (with-out-str (xmlfmt/format-string "<level-1><level-2><level-3/></level-2></level-1>"))))))

  (testing "child tag with keys"
    (let [expected (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        "<root>\n"
                        "  <child\n"
                        "    a-key=\"a-val\"\n"
                        "    b-key=\"b-val\"\n"
                        "  />\n"
                        "</root>\n")]
      (is (= expected
             (with-out-str (xmlfmt/format-string "<root><child a-key=\"a-val\" b-key=\"b-val\" /></root>"))))
      (is (= expected
             (with-out-str (xmlfmt/format-string "<root   ><child a-key=\"a-val\" b-key=\"b-val\" />   </root>")))))))
