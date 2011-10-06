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

Wenn man nach Entwicklerkollegen nach Neuerungen in JUnit frägt, wird häufig die Umstellung von Vererbung auf Annotation-basierte Testschreibweise in Version 4.0 erwähnt.

Seitdem hat sich allerdings einiges getan:

- Matchers: Flexiblere, lesbarere Assertions mit `assertThat` und [Hamcrest](http://code.google.com/p/hamcrest/) Matchers.
- Theories: Formulierung von Tests als Eigenschaften mit Vor- und Nachbedingungen (Assumptions).
- Categories: Tests in Kategorien einteilen und in separaten in Testsuiten ausführen.

Aber: Rules sind am wichtigsten, weil ...

- Wiederverwendbarkeit: Ermöglichen häufig benötigten SetUp/TearDown-Code in Klassen auszulagern, die sich auf einen Aspekt konzentrieren. 
- Kombinierbarkeit: Beliebig viele Regeln in einem Test verwendbar. Machen eigene Custom Test Runner (High Lander Prinzip) überflüssig.
- Delegation statt Vererbung: Helfen Testklassenhierarchien zu vermeiden! Keine Utility-Methoden mehr in Testoberklassen.
- Erweiterbarkeit: Eigene Regeln schreiben ist einfach!

Kent Beck ([Interceptors in JUnit](http://www.threeriversinstitute.org/blog/?p=155)):

> Maybe once every five years unsuspectedly powerful abstractions drop out of a program with no apparent effort.

Rules wurden in JUnit 4.9 nochmals erweitert und lassen sich nun auch auf Klassenebene (ähnlich wie `@BeforeClass`/`@AfterClass`) und in Testsuiten (mit dem `Suite` Runner) verwenden.

In Version 4.10 wurde eine weitere Rule eingeführt, die es ermöglicht die Reihenfolge in der Rules ausgeführt werden, zu kontrollieren (`RuleChain`).


## Definition von Rules

### [Jens Schauder](http://blog.schauderhaft.de/2009/10/04/junit-rules/)

The purpose of the `@Rule` annotation is to mark public fields of a test class. These fields must be of type `TestRule`, or an implementing class. Such MethodRules behave similar to a AOP aspects, of course without use of any AOP library and specialized for Tests. They can execute code before, after or instead of a test method. 


## Einsatzbeispiele

- Benachrichtigung über die Testausführung (siehe `TestWatchman`).
- Vorbereitung vom Test verwendeter Ressourcen (z.B. Dateien, Server, Verbindungen) und sauberes Aufräumen nach Ausführung des Tests. Besonders wichtig, wenn die Ressourcen von mehreren Tests verwendet werden (z.B. `TemporaryFolder`).
- Spezielle Überprüfungen nach oder vor jedem Test, die den Tests beispielsweise fehlschlagen lassen können. Beispiel `ErrorCollector`: Sammelt fehlgeschlagene Assertions innerhalb einer Testmethode und gibt am Ende eine Liste der Fehlschläge aus.
- Informationen über den Test innerhalb des Tests verfügbar machen (z.B. den Namen des Tests: `TestName`).

## Beispiele

TODO:

- Double-Check: Rules werden vor `@Before`- und nach `@After`-Methoden ausgeführt.

## Schreib deine eigenen Regeln!

TODO
