# JUnit Rules!

Marc Philipp, andrena objects ag

> Automatisierte Tests sind aus der heutigen Softwareentwicklung nicht mehr wegzudenken. JUnit ist das älteste und bekannteste Testing Framework für Java. Doch selbst ein so etabliertes und einfach zu benutzendes Framework wird kontinuierlich weiterentwickelt. Die Neuerungen bieten Entwicklern noch mächtigere Möglichkeiten, Tests zu schreiben und zu strukturieren.

Der Legende nach haben Kent Beck und Erich Gamma den Kern von JUnit auf dem Weg zu einer Konferenz im Flugzeug zwischen Zürich und Atlanta im Jahr 1997 geschrieben. Ihre Idee war ein Testing Framework zu schreiben, dessen Zielgruppe explizit Programmierer sind, also dieselben Leute, die auch den Code schreiben, den es zu testen gilt.

JUnit ist inzwischen weit verbreitet und wird nicht nur zum Schreiben von Unit Tests verwendet, sondern auch zur Automatisierung von Integrations- und Akzeptanztests eingesetzt. Viele erfolgreiche Open Source Projekte zeichnen sich dadurch aus, dass mit der Zeit immer neue Features eingebaut werden. Das Ergebnis ist häufig, dass die einst simple Bibliothek unübersichtlich und schwer wartbar geworden ist.

JUnit geht hier gezielt einen anderen Weg. David Saff, neben Kent Beck der zweite Maintainer von JUnit, sieht das so: „JUnit is the intersection of all possible useful Java test frameworks, not their union”. Die Wahrnehmung in der Java-Entwicklergemeinde ist dementsprechend: Da JUnit so einfach ist, meint jeder, der es schon einmal benutzt hat, es gut zu kennen. Das ist einerseits gut, denn die Hürde Unit Tests zu schreiben ist so sehr gering. Andererseits führt es dazu, dass Neuerungen von vielen Entwicklern entweder gar nicht oder erst verzögert wahrgenommen werden. 

Wenn man nach Entwicklerkollegen nach Neuerungen in JUnit frägt, wird häufig die Umstellung von Vererbung auf Annotation-basierte Testschreibweise in Version 4.0 erwähnt. Seitdem hat sich allerdings einiges getan. Die neuste Innovation, die mit Version 4.7 eingeführt wurde, heißt Rules. Zugegeben, unter dem Begriff kann man sich erst einmal nichts vorstellen. Wenn man sich diese „Regeln” für Tests aber einmal eingehend angesehen hat -- und genau das werden wir im Rest des Artikels tun -- stellt man fest: Rules werden die Art wie wir JUnit Tests schreiben nachhaltig verändern.

## Was sind Rules?

Was Rules sind, erklärt man am desten anhand ihrer Verwendung. Mithilfe der `@Rule`-Annotation markiert man Instanzvariablen einer Testklasse. Diese Felder müssen `public` und vom Typ `TestRule` oder einer Implementierung dieses Interface sein. Eine so definierte Regel wirkt sich nun auf die Ausführung jeder Testmethode in der Testklasse aus. Ähnlich einem Aspekt in der aspektorientierten Programmierung (AOP) kann die Rule Code vor, nach oder anstelle der Testmethode ausführen [[1]](http://blog.schauderhaft.de/2009/10/04/junit-rules/).

Das klingt zunächst ziemlich abstrakt. Um das ganze ein bisschen konkreter zu machen, schauen wir die Verwendung einer Rule anhand des folgenden (bedingt sinnvollen) Beispiels an:

```java
public class TemporaryFolderWithRule {

	@Rule public TemporaryFolder folder = new TemporaryFolder();

	@Test public void test() throws Exception {
		File file = folder.newFile("test.txt");
		assertTrue(file.exists());
	}
}
```

Hier ist die Instanzvariable `folder` mit der `@Rule`-Annotation versehen. Der Typ von `folder` ist `org.junit.rules.TemporaryFolder`, eine Standard-Rule, die in JUnit enthalten ist. Weiter gibt es eine Testmethode `test()`, die unsere Rule verwendet, um die Datei `test.txt` anzulegen und danach abprüft, dass die Datei erzeugt wurde. Doch wo wurde die Datei erzeugt? Der Name `TemporaryFolder` suggeriert es bereits: in einem temporären Ordner. Doch wer kümmert sich darum, dass die neue Datei und der temporäre Ordner nach dem Ende des Tests wieder gelöscht wird? Da die Rule sowohl Datei als auch Ordner angelegt hat, ist sie dafür verantwortlich diese auch wieder zu entfernen.

Würde man obigen Test auf herkömmliche Art und Weise schreiben, ohne die `TemporaryFolder`-Rule zu verwenden, sähe das in etwa so aus:

```java
public class TemporaryFolderWithoutRule {
		private File folder;

	@Before public void createTemporaryFolder() throws Exception {
		folder = File.createTempFile("myFolder", "");
		folder.delete();
		folder.mkdir();
	}

	@Test public void test() throws Exception {
		File file = new File(folder, "test.txt");
		file.createNewFile();
		assertTrue(file.exists());
	}

	@After public void deleteTemporaryFolder() {
		recursivelyDelete(folder);
	}

	private void recursivelyDelete(File file) {
		File[] files = file.listFiles();
		if (files != null) {
			for (File each : files) {
				recursivelyDelete(each);
			}
		}
		file.delete();
	}
}
```




## Wie helfen Rules, JUnit Tests einfacher zu formulieren?

### Einsatzmöglichkeiten

- Benachrichtigung über die Testausführung (siehe `TestWatchman`).
- Vorbereitung vom Test verwendeter Ressourcen (z.B. Dateien, Server, Verbindungen) und sauberes Aufräumen nach Ausführung des Tests. Besonders wichtig, wenn die Ressourcen von mehreren Tests verwendet werden (z.B. `TemporaryFolder`).
- Spezielle Überprüfungen nach oder vor jedem Test, die den Tests beispielsweise fehlschlagen lassen können. Beispiel `ErrorCollector`: Sammelt fehlgeschlagene Assertions innerhalb einer Testmethode und gibt am Ende eine Liste der Fehlschläge aus.
- Informationen über den Test innerhalb des Tests verfügbar machen (z.B. den Namen des Tests: `TestName`).

### Weitere Beispiele

TODO:

- Double-Check: Rules werden vor `@Before`- und nach `@After`-Methoden ausgeführt.

## Schreib deine eigenen Regeln!

TODO

## Fazit

Kent Beck ([Interceptors in JUnit](http://www.threeriversinstitute.org/blog/?p=155)):

> Maybe once every five years unsuspectedly powerful abstractions drop out of a program with no apparent effort.

### Vorteile von Rules

- Wiederverwendbarkeit: Ermöglichen häufig benötigten SetUp/TearDown-Code in Klassen auszulagern, die sich auf einen Aspekt konzentrieren. 
- Kombinierbarkeit: Beliebig viele Regeln in einem Test verwendbar. Machen eigene Custom Test Runner (High Lander Prinzip) überflüssig.
- Delegation statt Vererbung: Helfen Testklassenhierarchien zu vermeiden! Keine Utility-Methoden mehr in Testoberklassen.
- Erweiterbarkeit: Eigene Regeln schreiben ist einfach!


---------------------------------------

*Marc Philipp* (andrena objects ag) beschäftigt sich neben seiner Tätigkeit als Softwareentwickler mit der Arbeit an Entwicklungswerkzeugen.
