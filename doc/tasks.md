Open tasks:
===========


* CD: Dynamische Artikel-Daten (price, stock, countRatings, sumRatings) in beiden Buckets synchronisieren
    * Dynamische Daten zurücksetzen (evtl. inkl. Reviews)
    * evtl. Bezeichner in beiden Buckets angleichen
    * Dynamische Daten inital füllen
    * Code anpassen, dass bei Update beide Buckets gefüllt werden
    * Test, ob alles funktioniert (Suche, Artikelseite, Reviews, Administrationsbereich)

* CD: Checkout-Vorgang
    * Checkout-Start
        * Generierung einer Bestellungs-ID
    * Zwischenseite mit Warenkorb-Review, Adressliste, etc.
        * Nur **eine** Zwischenseite wie aktuell bei Amazon: verringert die Komplexität der Logik und der UI erheblich
        * Bearbeiten und Auswahl von Adressen
        * Bearbeiten und Auswahl von Zahlungsmöglichkeit (optional, da ähnlich wie Adressen)
    * Bestellabschluss mit Constraints
        * Bestellung darf nicht bereits abgeschlossen worden sein (Check mit Bestellungs-ID)
        * muss immer noch eingeloggt sein
        * keine zwischenzeitliche Änderung des Warenkorbs
        * keine zwischenzeitliche Änderung des Preises
        * evtl. Prüfung ob Artikel noch in Stock
    * Auflistung aller Bestellungen des Users im Userbereich
    * Auflistung aller Bestellungen der Anwendung im Administrationsbereich (Händlersicht)
    * Überlegungen bzgl. Konsistenz

* CD: Suche verbessern
    * weitere Filterungsmöglichkeiten
    * Typeahead für Textfeld
    * konsistente Suche (bei gleichem Filter: gleiche Werte)

* NN: Erweiterte Review-Darstellungsmöglichkeiten
    * Sortierung nach Datum
    * Filterung nach Anzahl der Sterne (nur 3-Stern-Bewertungen)
    * ...

* NN: Artikel-Vorschläge generieren
    * Optional, da nur geringer Bezug zur DB
    * Job zur Analyse von abgeschlossenen Käufen
        * z.B. über Nacht
        * Job evtl. auch über Admin-Seite ausführbar machen (für Live-Demo)
        * Apriori-Algorithmus
        * Häufig gemeinsam gekaufte Artikel (Kombinationen als Ergebnis des Algorithmus) in DB speichern
            * Key: Artikel-ID
            * Value: Liste von anderen Artikeln, die zusammen mit Artikel (Key) gekauft wurden
    * In Warenkorb-Ansicht: Anhand der Kombinationen Empfehlungen anzeigen (Nutzer die Artikel A kauften, kaufen auch B, C, ...)
    * Ebenso auf Startseite: Empfehlungen aufgrund von bisher getätigten Käufen

* NN: Erweiterung der Status-View
    * Top-Level-Auswahlmöglichkeit: Bucket-Type
    * Darstellungsmöglichkeit für CRDT-Werte
        * Generische CRDT-Map -> generische Java-Map

* NN: Benutzerbereich
    * Frühere Bestellungen einsehen (siehe anderen Task weiter oben)
    * Adressen bearbeiten
    * Account-Details bearbeiten
        * Benutzername
        * Passwort

* ...

* Abschlusspräsentation
    * Anfordergunen aus E-Mail:
        * 30-45 min, inklusive Fragen
        * Einleitung über die Anwendung / eingsetzte Technologien
        * Wie sieht euer Datenbankcluster aus
        * Wie habt ihr eure Anwendung in der Datenbank modelliert
        * andere Besonderheiten
        * Demo der Anwendung / Slideshow als Backup
    * ...

* Projektbericht
    * Inhalte aus der Abschlusspräsentation
    * Referenzieren der Seminarinhalte
        * Als Zwischenkapitel?
        * Als Anhang?
    * ...