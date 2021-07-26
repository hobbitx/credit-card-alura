(ns credit-card-alura.Card)

(def cards [])

(defn new-card [number cpf cvv validate limit]
  (def cards (conj cards  { :cpf cpf, :number number, :cvv cvv, :validate validate :limit limit})))

(defn get-client-cards [cpf]
  (filter #(= cpf (:cpf %)) cards))


(new-card "002929" "13528838647" "455" "10/2031" 1000)
(println (get-client-cards "13528838647"))