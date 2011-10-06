# JUnit Rules

von Marc Philipp

## Definitionen

### [Jens Schauder](http://blog.schauderhaft.de/2009/10/04/junit-rules/)

The purpose of the @Rule annotation is to mark public fields of a test class. These fields must be of type `TestRule`, or an implementing class. Such MethodRules behave similar to a AOP aspects, of course without use of any AOP library and specialized for Tests. They can execute code before, after or instead of a test method. 

## Einsatzbeispiele

- Benachrichtigung über die Testausführung (siehe `TestWatchman`).
- Vorbereitung vom Test verwendeter Ressourcen (z.B. Dateien, Server, Verbindungen) und sauberes Aufräumen nach Ausführung des Tests. Besonders wichtig, wenn die Ressourcen von mehreren Tests verwendet werden (z.B. `TemporaryFolder`).
- Spezielle Überprüfungen nach oder vor jedem Test, die den Tests beispielsweise fehlschlagen lassen können. Beispiel `ErrorCollector`: Sammelt fehlgeschlagene Assertions innerhalb einer Testmethode und gibt am Ende eine Liste der Fehlschläge aus.
- Informationen über den Test innerhalb des Tests verfügbar machen (z.B. den Namen des Tests: `TestName`).
