(ns credit-card-alura.Client)


(defn create-client [name cpf email clients]
  (conj clients  { :cpf cpf, :name name, :email email}))

(defn get-client [cpf clients]
  (first (filter #(= cpf (:cpf %)) clients)))

(defn show-clients
  [clients]
  (println "-----------------------------")
  (println clients))


