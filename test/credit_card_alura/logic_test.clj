(ns credit-card-alura.logic-test
  (:require [clojure.test :refer :all]
            [credit-card-alura.logic :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer (defspec)]
            [clojure.test.check.properties :as prop]
            [schema-generators.complete :as c]
            [credit-card-alura.model :as m]
            [clojure.pprint :as p]))
(use 'java-time)
(deftest create-purchase-test
  (testing "Test create purchase with Limit > value "
    (let [_ (create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
          client (get-client "13528838647")
          card-number (str (first (gen/sample (gen/elements ["1000" "1001" "1002" "1003"]) 1)))
          _ (new-card (:db/id client) card-number 455 "10/2031" 1000.0)
          card (select-cards card-number)
          purchase (new-purchase 700.0 (date-to-str (local-date)) "padaria" "alimentacao" card)]
      (is (= purchase {
                       :card-number   (:card/number card),
                       :date          (date-to-str (local-date)),
                       :value         700.0,
                       :category      "alimentacao",
                       :establishment "padaria"
                       :pay           false
                       }))))

  (testing "Test purchase with value > limit"
    (let [_ (create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
          client (get-client "13528838647")
          card-number (str (first (gen/sample (gen/elements ["2000" "2001" "2002" "2003"]) 1)))
          _ (new-card (:db/id client) card-number 455 "10/2031" 500.0)
          card (select-cards card-number)]
      (is (try
            (new-purchase 700.0 (date-to-str (local-date)) "padaria" "alimentacao" card)
            false
            (catch Exception e
              (= :card-not-valid-limit (:cause (ex-data e))))))))

  (testing "Test purchase with value > actual-limit"
    (let [_ (create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
          client (get-client "13528838647")
          card-number (str (first (gen/sample (gen/elements ["0000" "0001" "0002" "0003" "0004" "0005"]) 1)))
          _ (new-card (:db/id client) card-number 455 "10/2031" 1000.0)
          card (select-cards card-number)
          _ (new-purchase 700.0 (date-to-str (local-date)) "farmacia" "saude" card)]
      (is (try
            (new-purchase 600.0 (date-to-str (local-date)) "padaria" "alimentacao" card)
            false
            (catch Exception e
              (= :card-not-valid-limit (:cause (ex-data e))))))))

  (testing "Test purchase with value 0"
    (let [_ (create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
          consumer-id (:db/id (get-client "13528838647"))
          card (new-card consumer-id "002929" 455 "10/2031" 500.0)]
      (is (try
            (new-purchase 0 (date-to-str (local-date)) "padaria" "alimentacao" card)
            false
            (catch Exception e
              (= :purchase-value-not-valid (:cause (ex-data e)))))))))

(deftest purchases-realized-test
  (testing "Test get purchases"
    (let [
          _ (create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
          client (get-client "13528838647")
          card-number (str (first (gen/sample (gen/elements ["0000" "0001" "0002" "0003" "0004" "0005"]) 1)))
          _ (new-card (:db/id client) card-number 455 "10/2031" 1000.0)
          card (select-cards card-number)
          p1 (new-purchase 40.0 (date-to-str (local-date)) "farmacia" "saude" card)
          p2 (new-purchase 70.0 (date-to-str (local-date)) "farmacia" "saude" card)
          p3 (new-purchase 120.0 (date-to-str (local-date)) "BH" "Alimentacao" card)
          purchases (get-purchases )
          ]
      (p/pprint [p1 p2 p3])
      (p/pprint card)
      (p/pprint purchases)
      (def expect (map #(dissoc % :date) (get-purchases (:db/id card))))
      (is (= expect (list {:card-number   "002929"
                           :category      "alimentacao"
                           :establishment "padaria"
                           :pay           false
                           :value         700.0}
                          {:card-number   "002929"
                           :category      "saude"
                           :establishment "farmacia"
                           :pay           false
                           :value         200.0})))))
)
