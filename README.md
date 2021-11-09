# &nbsp; CaRP-Assigner &nbsp; [![CodeQL](https://github.com/juhu1705/CaRP/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/juhu1705/CaRP/actions/workflows/codeql-analysis.yml) <img src="https://raw.githubusercontent.com/juhu1705/CaRP/master/src/main/resources/assets/textures/logo/CaRP.png" alt="drawing" width="20"/>

# Allgemeines

Der „Course and Research Paper Assinger“ (Kurs und Facharbeit Zuweiser = KuFa-Zuweiser) ist ein Programm zum Zuweisen von Schülern nach Wunschkursen, z.B. Sportkurse oder Facharbeiten, die nach einer Priorität geordnet sind. Dabei wird versucht eine Zuweisung zu erstellen, welche einer Verteilung mit der bestmöglichen Priorität entspricht und dabei das Limit eines jeden Kurses nicht überschreitet.

Dieses Programm wurde im Rahmen einer besonderen Lernleistung für die Luisenschule Mülheim an der Ruhr im Abschlussjahr 2019 / 2020 entwickelt.

Am Dienstag, den 9. November 2021 wurden die 200 Commits geknackt! 🥳

# Installation und Benutzung

## Windows

Sollten sie ein Windows System benutzen, so liegen im aktuellen Release spezielle Installationsdateien bereit. Diese können sie herunterladen und dann den CaRP assigner installieren. Außerdem steht auch noch eine image datei als zip-Archiv zur installation zur Verfügung.

## Linux - Debian

Sollten sie Linux benutzen so finden sie für debian basierte systeme ein image als zip-Datei.

## Andere Systeme

Für alle weiteren Betriebssysteme können sie einfach die im Release angehängte jar-Datei ausführen. Dazu laden sie sich zunächst Java, als auch JavaFX herunter. Dann laden sie sich die jar-Datei herunter und ziehen sie diese in den von ihnen gewünschten Ordner. Hier führen sie nun folgenden Befehl aus:

```
java -jar --module-path "PATH_TO_JAVAFX" --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.media,javafx.graphics,javafx.swing JAR-DATEI
```

Dabei ersetzen sie PATH_TO_JAVAFX mit dem lib Verzeichnis ihrer JavaFX installation und JAR-DATEI mit der CaRP Assigner Datei die sie gerne ausführen möchten.
