(ns electionresult.core
  (:require [net.cgrand.enlive-html :as html])
  (:require [clj-http.client :as client])
  (:require [clojure.java.io :as io])
  (:gen-class)
)


(defn fetchUrl [url] 
  (html/html-resource (java.net.URL. url)))

(defn getStatesMap []
  (html/select (fetchUrl "http://eciresults.nic.in/PartyWiseResultU01.htm?st=") [:select :option]))

(defn getStatesCode []
  (map (fn [x] [((x :attrs) :value) (x :content)]) (getStatesMap)))

(defn getStatePage [code]
  (fetchUrl (clojure.string/join "" ["http://eciresults.nic.in/PartyWiseResult" code ".htm?st=" code])))


                                        ;// get the rows 
(defn getRows [dom]
  (filter (fn [x] 
            (= 4 (count (html/select x [:td])))  ;// get 4 columns rows
            )(html/select dom [:tr]) ;// get all the tr tags. find how to get th tags too in the same []
             ) ;// end filter
  )

;// sample test
;// (getRows (getStatePage( first (nth (getStatesCode) 2))))

;// get the first column of first row
;// ((first((first (getRows (getStatePage(first (nth (getStatesCode) 2))))) :content)) :content)

;// so get the rows of second state
;// (map (fn [x] (map (fn[x] (x :content)) x )) (map (fn[x] (html/select x [:td])) (getRows (getStatePage(first (nth (getStatesCode) 2))))))

                                        ;// get the content of the rows
(defn getContent [rows]
                                        ;// for each individual column get the content
  (map (fn [x] 
         (map (fn[x] 
                (x :content)) 
              x )
         ) 
                                        ;// get the individual columns
       (map (fn[x] 
              (html/select x [:td])) 
            rows)
       )
  )

                                        ;// get the content of each row in a list
(defn getDataRows [code]
  (map flatten 
       (getContent 
        (getRows 
         (getStatePage code))))
  )

;// sample find  all the results
;// (map (fn [x] (getDataRows (first x))) (getStatesCode))


(defn -main
  "Fetches, Parses the Indian election results and saves to the file."
  [& args]
  ;// (let [ partyWise (client/get "http://eciresults.nic.in/PartyWiseResultU01.htm?st=")]
  ;//  (html/html-resource (java.io.StringReader. (partyWise :body))))

  ;// write  it to file
  
  (with-open [myWriter (io/writer "partyWiseResults.txt")] ;// call the close automatically
    (binding [*out* myWriter] ;// bind this *out* to the myWriter
                                        ;// for each element print in a line
      (doseq [line (map (fn [x] { (nth x 1) (getDataRows (first x)) }) (getStatesCode))] (print myWriter line "\n"))))
 
)
