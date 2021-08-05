(ns credit-card-alura.logic-test
  (:require [clojure.test :refer :all]
            [credit-card-alura.logic :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer (defspec)]
            [clojure.test.check.properties :as prop]
            [schema-generators.complete :as c]
            [credit-card-alura.model :as m]))
(use 'java-time)

(deftest create-purchase-test

  (testing "Test create purchase with Limit > value "
    (let [client (create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
          card (new-card (:cpf client) "002929" 455 "10/2031" 1000.0)
          purchase (new-purchase 700.0 (date-to-str (local-date)) "padaria" "alimentacao" card [])]
      (is (= purchase {
                       :card-number   (:number card),
                       :date          (date-to-str (local-date)),
                       :value         700.0,
                       :category      "alimentacao",
                       :establishment "padaria"
                       :pay           false
                       }))))

  (testing "Test purchase with value > limit"
    (let [client (create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
          card (new-card (:cpf client) "002929" 455 "10/2031" 500.0)]
      (is (try
            (new-purchase 700.0 (date-to-str (local-date)) "padaria" "alimentacao" card [])
            false
            (catch Exception e
              (= :card-not-valid-limit (:cause (ex-data e))))))))

  (testing "Test purchase with value > actual-limit"
    (let [client (create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
          card (new-card (:cpf client) "002929" 455 "10/2031" 1000.0)]
      (is (try
            (new-purchase 700.0 (date-to-str (local-date)) "padaria" "alimentacao" card [{:card-number "002929", :date (date-to-str (local-date)), :value 700.0, :category "alimentacao", :establishment "padaria", :pay false}])
            false
            (catch Exception e
              (= :card-not-valid-limit (:cause (ex-data e))))))))

  (testing "Test purchase with value 0"
    (let [client (create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
          card (new-card (:cpf client) "002929" 455 "10/2031" 500.0)]
      (is (try
            (new-purchase 0 (date-to-str (local-date)) "padaria" "alimentacao" card [])
            false
            (catch Exception e
              (= :purchase-value-not-valid (:cause (ex-data e)))))))))

(deftest purchases-realized-test
    (testing "Test get purchases"
      (let [purchases [
                       {:card-number "002929", :date (date-to-str (local-date)), :value 700.0, :category "alimentacao", :establishment "padaria", :pay false}
                       {:card-number "002921", :date (date-to-str (local-date)), :value 100.0, :category "saude", :establishment "farmacia", :pay false}
                       {:card-number "002929", :date (date-to-str (local-date)) :value 200.0, :category "saude", :establishment "farmacia", :pay false}]
            expect (map #(dissoc % :date) (get-purchases "002929" purchases))
            ]
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

    (testing "Test get purchases with empty collection"
      (let [purchases []]
        (is (= (get-purchases "002929" purchases) (list)))))

    (testing "Test get purchases with not value in collection"
      (let [purchases [{:card-number "002929", :date (date-to-str (local-date)), :value 700.0, :category "alimentacao", :establishment "padaria", :pay false}]]
        (is (= (get-purchases "002232" purchases) (list))))))


(defn generate-samples
  [category value card-number]
  (c/complete {:category category,:value value, :card-number card-number} m/Purchase))

(defn total-value
        [purchases keyword]
        (reduce + (map #(keyword %) purchases)
        ))

(defspec test-with-total-by-category-same-total 50
         (prop/for-all
           [
            card-numbers (gen/vector (gen/elements ["0000" "0001" "0002" "0003"]) 1 10)
            values (gen/vector (gen/double* {:min 1, :max 1000, :infinite? true, :NaN? false}) 1 10)
            categorys (gen/vector (gen/elements ["Alimentacao" "Saude" "Educacao" "Diversao"]) 1 10)
            ]
           (let [
                 value (rand-nth values)
                 card-number (rand-nth card-numbers)
                 purchases (map #(generate-samples % value (rand-nth card-numbers)) categorys)
                 by-category (total-by-category card-number purchases)
                 ]
             (is (= (total-value (filter #(= card-number (:card-number %)) purchases) :value)
                    (total-value by-category :total))))))

