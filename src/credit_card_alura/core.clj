(ns credit-card-alura.core
  (:require [credit-card-alura.logic :as l]
            [credit-card-alura.model :as m]
            [schema.core :as s]))
(use 'java-time)

(def clients [])
(def purchases [])
(def cards [])

(l/show-clients clients)
(def clients (conj clients (l/create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")))
(def clients (conj clients (l/create-client "Tha" "1325" "th")))

(println "\nBuscando  cliente" (l/get-client "13528838647" clients))

(if-let [client (l/exist-client? "13528838647" clients)]
  (def cards (conj cards (l/new-card (:cpf client) "002929" 455 "10/2031" 1000.0))))

(let [card (l/select-cards "002929" cards)]
  (println "\nSelecionando cartao" card)
  (println "\nLimite do cartao" (:number card) ": R$" (l/get-limit card)))

(let [card (l/select-cards "002929" cards)]
  (def purchases (conj purchases (l/new-purchase 700.0 (local-date) "padaria" "alimentacao" card purchases)))
  (def purchases (conj purchases (l/new-purchase 100.0 (local-date) "farmacia" "saude" card purchases)))
  (def purchases (conj purchases (l/new-purchase 200.0 (local-date) "farmacia" "saude" card purchases)))
  (println "\nCompras" (l/get-purchases (:number card) purchases))
  (println "\nPor categoria" (l/total-by-category (:number card) purchases))
  (println "\nFatura total" (l/invoice (:number card) purchases)))




(println "\nCompras entre 10 e 400 R$" (l/filter-purchases 10 400 :value purchases))