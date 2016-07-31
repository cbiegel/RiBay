Open tasks:
===========


* CD: Checkout-Vorgang
    * Zwischenseite mit Warenkorb-Review, Adressliste, etc.
        * Nur **eine** Zwischenseite wie aktuell bei Amazon: verringert die Komplexität der Logik und der UI erheblich
        * Bearbeiten und Auswahl von Adressen
        * Fehlerbahndlung für Fehler, die bei normalem Betrieb auftreten (cart changed)
    * Bestellabschluss mit Constraints
        * (alles erledigt bis auf:)
        * evtl. Prüfung ob Artikel noch in Stock
    * Auflistung aller Bestellungen des Users im Userbereich
    * Auflistung aller Bestellungen der Anwendung im Administrationsbereich (Händlersicht)
    * Überlegungen bzgl. Konsistenz

* CD: Suche verbessern
    * weitere Filterungsmöglichkeiten
    * weitere Sortierungsmöglichkeiten
    * Typeahead für Textfeld
    * konsistente Suche (bei gleichem Filter: gleiche Werte)

* NN: Erweiterte Review-Darstellungsmöglichkeiten
    * Sortierung nach Datum
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