# JUnit Rules!

von Marc Philipp

## Über JUnit

Kent Beck:

> A programmer-oriented testing framework for Java.

David Saff:

> JUnit is the intersection of all possible useful Java test frameworks, not their union.

Nicht nur für Unit Tests!

## Motivation

Jeder meint, JUnit zu kennen: JUnit ist einfach. Neue Features? Gibt's nicht. Oder?

Seit der Umstellung von Vererbung auf Annotation-basierte Testschreibweise in Version 4.0 hat sich einiges getan:

- Matchers: Flexiblere, lesbarere Assertions mit `assertThat` und [Hamcrest](http://code.google.com/p/hamcrest/) Matchers.
- Theories: Formulierung von Tests als Eigenschaften mit Vor- und Nachbedingungen (Assumptions).
- Categories: Tests in Kategorien einteilen und in separaten in Testsuiten ausführen.

Aber: Rules sind am wichtigsten, weil ...


## Definition von Rules

### [Jens Schauder](http://blog.schauderhaft.de/2009/10/04/junit-rules/)

The purpose of the @Rule annotation is to mark public fields of a test class. These fields must be of type `TestRule`, or an implementing class. Such MethodRules behave similar to a AOP aspects, of course without use of any AOP library and specialized for Tests. They can execute code before, after or instead of a test method. 


## Einsatzbeispiele

- Benachrichtigung über die Testausführung (siehe `TestWatchman`).
- Vorbereitung vom Test verwendeter Ressourcen (z.B. Dateien, Server, Verbindungen) und sauberes Aufräumen nach Ausführung des Tests. Besonders wichtig, wenn die Ressourcen von mehreren Tests verwendet werden (z.B. `TemporaryFolder`).
- Spezielle Überprüfungen nach oder vor jedem Test, die den Tests beispielsweise fehlschlagen lassen können. Beispiel `ErrorCollector`: Sammelt fehlgeschlagene Assertions innerhalb einer Testmethode und gibt am Ende eine Liste der Fehlschläge aus.
- Informationen über den Test innerhalb des Tests verfügbar machen (z.B. den Namen des Tests: `TestName`).
