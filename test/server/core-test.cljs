(ns boot-new.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [boot-new.core]))

(deftest test-platform
  (testing "Node"
    (is (exists? js/process))
    (is (exists? (aget js/process "env")))))

