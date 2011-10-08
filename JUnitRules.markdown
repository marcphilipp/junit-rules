# JUnit Rules!

Marc Philipp, andrena objects ag

_Automatisierte Tests sind aus der heutigen Softwareentwicklung nicht mehr wegzudenken. JUnit ist das älteste und bekannteste Testing Framework für Java. Doch selbst ein so etabliertes und einfach zu benutzendes Framework wird kontinuierlich weiterentwickelt. Die Neuerungen bieten Entwicklern noch mächtigere Möglichkeiten, Tests zu schreiben und zu strukturieren._

--------------------------------------------------------------------------------------------------------------------------------

Der Legende nach haben Kent Beck und Erich Gamma den Kern von JUnit auf dem Weg zu einer Konferenz im Flugzeug zwischen Zürich und Atlanta im Jahr 1997 geschrieben. Ihre Idee war ein Testing Framework, dessen Zielgruppe explizit Programmierer sind, also dieselben Leute, die auch den Code schreiben, den es zu testen gilt.

JUnit ist inzwischen weit verbreitet. Es wird nicht nur zum Schreiben von Unit Tests verwendet, sondern auch zur Automatisierung von Integrations- und Akzeptanztests eingesetzt. Viele erfolgreiche Open Source Projekte zeichnen sich dadurch aus, dass mit der Zeit immer neue Features eingebaut werden. Dies führt jedoch häufig dazu, dass die einst simple Bibliothek unübersichtlich und schwer wartbar wird.

JUnit geht hier gezielt einen anderen Weg. David Saff, neben Kent Beck der zweite Maintainer von JUnit, sieht das so: „JUnit is the intersection of all possible useful Java test frameworks, not their union”. Die Wahrnehmung in der Java-Entwicklergemeinde ist dementsprechend: Da JUnit so einfach ist, meint jeder, der es schon einmal benutzt hat, es gut zu kennen. Das ist einerseits gut, denn die Hürde Unit Tests zu schreiben ist so sehr niedrig. Andererseits führt es dazu, dass Neuerungen von vielen Entwicklern entweder gar nicht oder erst verzögert wahrgenommen werden. 

Wenn man nach Entwicklerkollegen nach Neuerungen in JUnit frägt, wird häufig die Umstellung von Vererbung auf Annotation-basierte Testschreibweise in Version 4.0 erwähnt. Seitdem hat sich allerdings einiges getan. Die neuste Innovation, die mit Version 4.7 eingeführt wurde, heißt Rules. Zugegeben, unter dem Begriff kann man sich erst einmal nichts vorstellen. Wenn man sich diese „Regeln” für Tests aber einmal eingehend angesehen hat -- und genau das werden wir in diesem Artikel tun -- stellt man fest: Rules werden die Art, wie wir JUnit Tests schreiben, nachhaltig verändern.

## Was sind Rules?

Für die Verwendung von Rules wurde eine neue Annotation eingeführt: Mithilfe der `@Rule`-Annotation markiert man Instanzvariablen einer Testklasse. Diese Felder müssen `public` und vom Typ `TestRule` oder einer Implementierung dieses Interface sein. Eine so definierte Regel wirkt sich nun auf die Ausführung jeder Testmethode in der Testklasse aus. Ähnlich einem Aspekt in der aspektorientierten Programmierung (AOP) kann die Rule Code vor, nach oder anstelle der Testmethode ausführen [[1]](http://blog.schauderhaft.de/2009/10/04/junit-rules/).

Das klingt zunächst recht abstrakt. Um das Ganze konkreter zu machen, schauen wir die Verwendung einer Rule anhand des folgenden Beispiels an:

~~~java
public class TemporaryFolderWithRule {

	@Rule public TemporaryFolder folder = new TemporaryFolder();

	@Test public void test() throws Exception {
		File file = folder.newFile("test.txt");
		assertTrue(file.exists());
	}
}
~~~

Hier ist die Instanzvariable `folder` mit der `@Rule`-Annotation versehen. Der Typ von `folder` ist `org.junit.rules.TemporaryFolder`, eine Standard-Rule, die in JUnit enthalten ist. Weiter gibt es eine Testmethode `test()`, die unsere Rule verwendet, um die Datei `test.txt` anzulegen und danach abprüft, dass die Datei erzeugt wurde. Doch wo wurde die Datei erzeugt? Der Name `TemporaryFolder` suggeriert es bereits: in einem temporären Ordner. Doch wer kümmert sich darum, dass die neue Datei und der temporäre Ordner nach dem Ende des Tests wieder gelöscht wird? Da die Rule sowohl Datei als auch Ordner angelegt hat, ist sie dafür verantwortlich diese auch wieder zu entfernen.

Würde man obigen Test auf herkömmliche Art und Weise schreiben, ohne die `TemporaryFolder`-Rule zu verwenden, sähe das in etwa so aus:

~~~java
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
~~~

In diesem Fall hat uns die `TemporaryFolder`-Rule also geholfen, den Test wesentlich kürzer und prägnanter zu formulieren.

## Wozu kann ich Rules verwenden?

### Bereitstellung externe Ressourcen

Den häufigsten Anwendungsfall, insbesondere bei Integrationstests, haben wir bereits gesehen: die Vorbereitung vom Test verwendeter externer Ressourcen (z.B. Dateien, Server, Verbindungen) inklusive dem sauberen Aufräumen nach Ausführung des Tests. Besonders wichtig ist dies, wenn die Ressource von mehreren Tests verwendet werden. `TemporaryFolder` ist eine beispielhafte Implementierung für eine solche Rule. 

Andere lassen sich leicht selbst implementieren, indem man von der Basisklasse `ExternalResource` ableitet. Möchte man etwa für einen Test sicherstellen, dass eine System Property einen bestimmten Wert hat und nach dem Test der alte Wert wiederhergestellt wird, könnte man die Methoden `before()` und `after()` wie folgt implementieren:

~~~java
public class ProvideSystemProperty extends ExternalResource {

	private final String key, value;
	private String oldValue;

	public ProvideSystemProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override protected void before() {
		oldValue = System.getProperty(key);
		System.setProperty(key, value);
	}

	@Override protected void after() {
		if (oldValue == null) {
			System.clearProperty(key);
		} else {
			System.setProperty(key, oldValue);
		}
	}
}
~~~

Und schon können wir unsere Rule in einem Test verwenden:

~~~java
public class SomeTestUsingSystemProperty {

	@Rule public ProvideSystemProperty property = new ProvideSystemProperty("someKey", "someValue");

	@Test public void test() {
		assertThat(System.getProperty("someKey"), is("someValue"));
	}
}
~~~

### Benachrichtigung über die Testausführung

Da man mit einer Rule Code vor und nach dem Aufruf der Testmethoden ausführen kann, lässt sich damit eine Benachrichtigung über die Testausführung realisieren. Dazu stellt JUnit die abstrakte Oberklasse `TestWatcher` bereit. Diese besitzt vier leer implementierte Methoden, die man nach Bedarf überschreiben und implementieren kann: `starting()`, `succeeded()`, `failed()` und `finished()`:

~~~java
public class BeepOnFailure extends TestWatcher {

	@Override protected void failed(Throwable e, Description description) {
		Toolkit.getDefaultToolkit().beep();
	}
}
~~~

### Überprüfungen vor/nach der Tests

Desweiteren lassen sich spezielle Überprüfungen, die den Tests beispielsweise fehlschlagen lassen können, vor oder nach jedem Test ausführen. Der `ErrorCollector` sammelt fehlgeschlagene Assertions innerhalb einer Testmethode und gibt am Ende eine Liste der Fehlschläge aus.

### Bereitstellung von Informationen über den Test

Eine Rule kann außerdem Informationen über den Test innerhalb des Tests verfügbar machen. So kann man mit der `TestName` Rule etwa auf den Namen des aktuellen Tests zugreifen.


### ClassRules (4.9)

### RuleChain (4.10)

TODO: Erst Ausführung generell erklären, dann Problem 


## Schreib deine eigenen Regeln!

Warum sollte ich Rules verwenden?

Kent Beck ([Interceptors in JUnit](http://www.threeriversinstitute.org/blog/?p=155)):

> Maybe once every five years unsuspectedly powerful abstractions drop out of a program with no apparent effort.

- Wiederverwendbarkeit: Ermöglichen häufig benötigten SetUp/TearDown-Code in Klassen auszulagern, die sich auf einen Aspekt konzentrieren. 
- Kombinierbarkeit: Beliebig viele Regeln in einem Test verwendbar. Machen eigene Custom Test Runner (High Lander Prinzip) überflüssig.
- Delegation statt Vererbung: Helfen Testklassenhierarchien zu vermeiden! Keine Utility-Methoden mehr in Testoberklassen.
- Erweiterbarkeit: Eigene Regeln schreiben ist einfach!
