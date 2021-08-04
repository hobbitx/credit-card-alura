(ns credit-card-alura.logic-test
  (:require [clojure.test :refer :all]
            [credit-card-alura.logic :refer :all]))
(use 'java-time)

(deftest create-purchase-test

  (testing "Test create purchase with Limit > value ")
  (let [client (create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
        card (new-card (:cpf client) "002929" 455 "10/2031" 1000.0)
        purchase (new-purchase 700.0 (local-date) "padaria" "alimentacao" card [])]
    (is (= purchase {
                     :card-number   (:number card),
                     :date          (local-date),
                     :value         700.0,
                     :category      "alimentacao",
                     :establishment "padaria"
                     :pay           false
                     })))

  (testing "Test purchase with value > limit")
  (let [client (create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
        card (new-card (:cpf client) "002929" 455 "10/2031" 500.0)]
    (is (try
          (new-purchase 700.0 (local-date) "padaria" "alimentacao" card [])
          false
          (catch Exception e
            (= :card-not-valid-limit (:cause (ex-data e)))))))

  (testing "Test purchase with value > actual-limit")
  (let [client (create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
        card (new-card (:cpf client) "002929" 455 "10/2031" 1000.0)]
    (is (try
          (new-purchase 700.0 (local-date) "padaria" "alimentacao" card [{:card-number "002929", :date (local-date), :value 700.0, :category "alimentacao", :establishment "padaria", :pay false}])
          false
          (catch Exception e
            (= :card-not-valid-limit (:cause (ex-data e)))))))

  (testing "Test purchase with value 0")
  (let [client (create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
        card (new-card (:cpf client) "002929" 455 "10/2031" 500.0)]
    (is (try
          (new-purchase 0 (local-date) "padaria" "alimentacao" card [])
          false
          (catch Exception e
            (= :purchase-value-not-valid (:cause (ex-data e))))))))

(deftest purchases-realized-test
  (testing "Test get purchases")
  (let [purchases [
                    {:card-number "002929", :date (local-date), :value 700.0, :category "alimentacao", :establishment "padaria", :pay false}
                    {:card-number "002921", :date (local-date), :value 100.0, :category "saude", :establishment "farmacia", :pay false}
                    {:card-number "002929", :date (local-date) :value 200.0, :category "saude", :establishment "farmacia", :pay false}]
        expect  (map #(dissoc % :date ) (get-purchases "002929" purchases))
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
                    :value         200.0}))))

  (testing "Test get purchases with empty collection")
  (let [purchases []]
    (is (= (get-purchases "002929" purchases) (list))))

  (testing "Test get purchases with not value in collection")
  (let [purchases [{:card-number "002929", :date (local-date), :value 700.0, :category "alimentacao", :establishment "padaria", :pay false}]]
    (is (= (get-purchases "002232" purchases) (list)))))

