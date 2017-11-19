(ns boot-new.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [boot-new.core]))

(deftest test-platform
  (testing "Not node"
    (is (not (and (exists? js/process) (exists? (aget js/process "env")))))))

