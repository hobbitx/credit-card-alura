(ns credit-card-alura.core

  (:require [credit-card-alura.logic :as l]
            [credit-card-alura.database :as db]
            [clojure.pprint :as p]))
(use 'java-time)

(def purchases [])


(l/create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
(l/create-client "Tha" "1325" "th")
(l/show-clients)
(p/pprint (l/get-client "13528838647"))

(if-let [client (l/get-client "13528838647")]
  (l/new-card (:db/id client) "002929" 455 "10/2031" 1000.0))


(if-let [client (l/get-client "1325")]
  (l/new-card (:db/id client) "002921" 415 "10/2028" 7000.0))

(p/pprint (l/get-cards))

(let [card (l/select-cards "002929")]
  (println "\nSelecionando cartao" card)
  (println "\nLimite do cartao" (:card/number card) ": R$" (l/get-limit card)))


(let [card (l/select-cards "002929")]
          (println "Realizando compras ....")
          (l/new-purchase 70.0 (l/date-to-str (local-date)) "padaria" "alimentacao" card)
          (l/new-purchase 100.0 (l/date-to-str (local-date)) "farmacia" "saude" card)
          (l/new-purchase 200.32 (l/date-to-str (local-date)) "farmacia" "saude" card)
          (l/new-purchase 100.32 "10/07/2021" "farmacia" "saude" card))

(let [card (l/select-cards "002921")]
          (println "Realizando compras ....")
          (l/new-purchase 55.0 (l/date-to-str (local-date)) "Uber" "Mobilidade" card)
          (l/new-purchase 506.32 "10/07/2021" "Papelaria" "Educacao" card))

(let [card (l/select-cards "002929")]
  (println "\nLimite do cartao apos compras:" (:card/number card) ": R$" (l/get-limit card)))


(println "\nCompras entre 10 e 400 R$" (l/filter-purchases 10 400 :purchase/value))
(println "\nPor categoria")
(p/pprint (l/total-by-category))
(let [card (l/select-cards "002929")]
  (println "\nFatura total" (l/invoice (:db/id card))))


(p/pprint (l/client-more-purchase))
(p/pprint (l/client-max-purchases))
(db/delete-database)