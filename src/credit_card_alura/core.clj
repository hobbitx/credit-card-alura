(ns credit-card-alura.core
  (:require [credit-card-alura.Client :as clients]
            [credit-card-alura.Card :as cards]
            [credit_card-alura.Purchase :as purchases]))
(use 'java-time)

(def clients [])
(def purchases [])
(def cards [])

(clients/show-clients clients)
(def clients (clients/create-client "Robert" "13528838647" "robert.cristian@nubank.com.br" clients))
(def clients (clients/create-client "Tha" "1325" "th" clients))
(println "Buscando  cliente" (clients/get-client "13528838647" clients))
(clients/show-clients clients)

(def cards (cards/new-card "002929" "13528838647" "455" "10/2031" 1000.0 cards clients))
(println "Selecionando cartao" (def card (cards/select-cards "002929" cards)))
(println "Limite do cartao" (:number card) ":" (cards/get-limit card))

(def purchases (purchases/new-purchase (local-date) 500.0 "padaria" "alimentacao" card purchases))
(def purchases (purchases/new-purchase (local-date) 400.0 "farmacia" "saude" card purchases))

(println "Compras" (purchases/get-purchases (:number card) purchases))
(println "Por categoria" (purchases/total-by-category card purchases))
(println "Fatura total" (purchases/invoice (:number card) purchases))
(println (purchases/purchases-by-value 10 2000 purchases))