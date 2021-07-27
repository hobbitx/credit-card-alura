(ns credit-card-alura.core
  (:require [credit-card-alura.Client :as clients]
            [credit-card-alura.Card :as cards]
            [credit_card-alura.Purchase :as purchases]))
(use 'java-time)


(clients/show-clients)
(clients/create-client "Robert" "13528838647" "robert.cristian@nubank.com.br")
(clients/create-client "Tha" "1325" "th")
(println "Buscando  cliente" (clients/get-client "13528838647"))
(clients/show-clients)

(cards/new-card "002929" "13528838647" "455" "10/2031" 1000)
(println (cards/get-client-cards "13528838647"))
(println (cards/get-limit "002929"))

(purchases/new-purchase "002929" (local-date-time) 200 "padaria" "alimentacao")
(println "Compras" (purchases/get-purchases "002929"))
(println "Valor das compras" (purchases/purchases-value "002929"))
(println (purchases/category))
(println (purchases/invoice "002929"))
(println (purchases/invoice "002929" 7))