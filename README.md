# &nbsp; CaRP-Assigner &nbsp; [![CodeQL](https://github.com/juhu1705/CaRP/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/juhu1705/CaRP/actions/workflows/codeql-analysis.yml) <img src="https://raw.githubusercontent.com/juhu1705/CaRP/master/src/main/resources/assets/textures/logo/CaRP.png" alt="drawing" width="20"/>

# Allgemeines

Der „Course and Research Paper Assinger“ (Kurs und Facharbeit Zuweiser = KuFa-Zuweiser) ist ein Programm zum Zuweisen von Schülern nach Wunschkursen, z.B. Sportkurse oder Facharbeiten, die nach einer Priorität geordnet sind. Dabei wird versucht eine Zuweisung zu erstellen, welche einer Verteilung mit der bestmöglichen Priorität entspricht und dabei das Limit eines jeden Kurses nicht überschreitet.

Dieses Programm wurde im Rahmen einer besonderen Lernleistung für die Luisenschule Mülheim an der Ruhr im Abschlussjahr 2019 / 2020 entwickelt.

# Installation und Benutzung

## Windows

Sollten Sie ein Windows-System benutzen, so liegen im aktuellen Release spezielle Installationsdateien bereit. Diese können Sie herunterladen und dann den CaRP-Assigner installieren. Außerdem steht auch noch eine Image-Datei als Zip-Archiv zur Installation zur Verfügung.

## Linux - Debian

Sollten Sie Linux benutzen, so finden Sie für debianbasierte Systeme ein Image als Zip-Datei.

## Andere Systeme

Für alle weiteren Betriebssysteme können Sie einfach die im Release angehängte Jar-Datei ausführen. Dazu laden Sie sich zunächst Java, als auch JavaFX herunter. Dann laden Sie sich die Jar-Datei herunter und ziehen diese in den von Ihnen gewünschten Ordner. Hier führen Sie nun folgenden Befehl aus:

```
java -jar --module-path "PATH_TO_JAVAFX" --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.media,javafx.graphics,javafx.swing JAR-DATEI
```

Dabei ersetzen Sie PATH_TO_JAVAFX mit dem `lib`-Verzeichnis ihrer JavaFX-Installation und JAR-DATEI mit der CaRP-Assigner-Datei, die Sie ausführen möchten.
