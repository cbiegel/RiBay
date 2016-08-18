Open tasks:
===========


* CD: Checkout-Vorgang
    * Bestellabschluss mit Constraints
        * (alles erledigt bis auf:)
        * evtl. Prüfung ob Artikel noch in Stock (kann nicht vollständig geprüft werden, da 'Eventual Consistency')
    * Auflistung aller Bestellungen der Anwendung im Administrationsbereich (Händlersicht)
    * Überlegungen bzgl. Konsistenz

* CD: Suche verbessern
    * weitere Filterungsmöglichkeiten
    * weitere Sortierungsmöglichkeiten
    * Typeahead für Textfeld
    * konsistente Suche (bei gleichem Filter: gleiche Werte)

* CD: Wunschliste
    * Benutzer kann selbstständig die Liste ordnen
    * CRDT für Listen? Kann eventuell durch manuelle Konfliktresolution implementiert werden. Siehe CRDT-Beschreibung für kollaborativen Text-Editor

* CB: Benutzerbereich
    * Evtl. neue Features für User Settings ausdenken? (Amazon hat ziemlich viele Preferences)

* CB: Erweiterte Review-Darstellungsmöglichkeiten
    * Sortierung nach Datum
    * ...

* NN: Erweiterung der Status-View
    * Darstellungsmöglichkeit für CRDT-Werte
        * Generische CRDT-Map -> generische Java-Map
        
* CD: Pixelschubsen
    * Schönere Startseite
    * Navigationsleiste überarbeiten
    * Footer und anderes Füllmaterial

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