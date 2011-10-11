# JUnit Rules

Marc Philipp, andrena objects ag

_Automatisierte Tests sind aus der heutigen Softwareentwicklung nicht mehr wegzudenken. JUnit ist das älteste und bekannteste Testing-Framework für Java. Doch selbst ein so etabliertes und einfach zu benutzendes Framework wird kontinuierlich weiterentwickelt. Eine der Neuerungen sind JUnit Rules, die Entwicklern eine neue mächtige Möglichkeit bieten, Tests zu formulieren und besser zu strukturieren._

Der Legende nach haben Kent Beck und Erich Gamma 1997 den Kern von JUnit auf dem Weg zu einer Konferenz im Flugzeug zwischen Zürich und Atlanta geschrieben. Ihre Idee war ein Testing-Framework, dessen Zielgruppe explizit Programmierer sind, also dieselben Leute, die auch den Code schreiben, den es zu testen gilt. JUnit ist inzwischen weit verbreitet. Es wird nicht nur zum Schreiben von Unittests, sondern auch zur Automatisierung von Integrations- und Akzeptanztests verwendet.

Viele erfolgreiche Open-Source-Projekte zeichnen sich dadurch aus, dass mit der Zeit immer neue Features eingebaut werden. Dies führt häufig dazu, dass einst simple Bibliotheken unübersichtlich und schwer wartbar werden. JUnit geht hier gezielt einen anderen Weg. David Saff, neben Kent Beck der zweite Maintainer von JUnit, sieht das so: „JUnit is the intersection of all possible useful Java test frameworks, not their union”.

Die Wahrnehmung in der Java-Entwicklergemeinde ist dementsprechend: Da JUnit so einfach ist, meint jeder, der es schon einmal benutzt hat, es gut zu kennen. Das ist einerseits gut, denn die Hürde Unittests zu schreiben ist so sehr niedrig. Andererseits führt es dazu, dass Neuerungen von vielen Entwicklern gar nicht oder erst verzögert wahrgenommen werden. Fragt man Entwicklerkollegen nach Neuerungen in JUnit, wird häufig die Umstellung von Vererbung auf Annotations-basierte Testschreibweise in Version 4.0 erwähnt.

Seitdem hat sich allerdings einiges getan. Die neueste Innovation, die mit Version 4.7 eingeführt wurde, heißt Rules. Zugegeben, unter dem Begriff kann man sich erst einmal nichts vorstellen. Hat man sich diese „Regeln” für Tests aber einmal eingehend angesehen -- und genau das werden wir in diesem Artikel tun -- stellt man fest: Rules werden die Art, wie wir JUnit-Tests schreiben, nachhaltig verändern.



## Was sind Rules?

Für die Verwendung von Rules wurde eine neue Annotation eingeführt: Mithilfe der `@Rule`-Annotation markiert man Instanzvariablen einer Testklasse. Diese Felder müssen `public` und vom Typ `TestRule` oder einer Implementierung dieses Interface sein. Eine so definierte Regel wirkt sich auf die Ausführung jeder Testmethode in der Testklasse aus. Ähnlich einem Aspekt in der aspektorientierten Programmierung (AOP) kann die Rule Code vor, nach oder anstelle der Testmethode ausführen [[1]][JensSchauderBlog].

Das klingt zunächst recht abstrakt. Um das Ganze konkreter zu machen, schauen wir die Verwendung einer Rule anhand des folgenden Beispiels an (der Source Code aller Beispiele ist auf GitHub verfügbar [[2]][GitHubPage]):

~~~java
public class TemporaryFolderWithRule {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void test() throws Exception {
		File file = folder.newFile("test.txt");
		assertTrue(file.exists());
	}
}
~~~

Hier ist die Instanzvariable `folder` mit der `@Rule`-Annotation versehen. Der Typ von `folder` ist `org.junit.rules.TemporaryFolder`, eine Standard-Rule, die in JUnit enthalten ist. Weiter gibt es eine Testmethode `test()`, die unsere Rule verwendet, um die Datei `test.txt` anzulegen und danach überprüft, dass die Datei erzeugt wurde. Doch wo wurde die Datei erzeugt? Der Name `TemporaryFolder` suggeriert es bereits: in einem temporären Ordner. Doch wer kümmert sich darum, dass die neue Datei und der temporäre Ordner nach Ablauf des Tests wieder gelöscht wird? Da die Rule sowohl Datei als auch Ordner angelegt hat, ist sie dafür verantwortlich diese auch wieder zu entfernen.

Würde man obigen Test auf herkömmliche Art und Weise schreiben, ohne die `TemporaryFolder`-Rule zu verwenden, sähe das in etwa so aus:

~~~java
public class TemporaryFolderWithoutRule {
	private File folder;

	@Before
	public void createTemporaryFolder() throws Exception {
		folder = File.createTempFile("myFolder", "");
		folder.delete();
		folder.mkdir();
	}

	@Test
	public void test() throws Exception {
		File file = new File(folder, "test.txt");
		file.createNewFile();
		assertTrue(file.exists());
	}

	@After
	public void deleteTemporaryFolder() {
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

In diesem Fall hat die `TemporaryFolder`-Rule also geholfen, den Test wesentlich kürzer und prägnanter zu formulieren. Zudem haben wir keine Hilfsmethoden in der Testklasse benötigt, der Code zum Anlegen und Löschen eines temporären Ordners ist in einer Klasse gekapselt, die in anderen Tests wiederverwendet werden kann.


## Bereitstellung externer Ressourcen

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

	@Override
	protected void before() {
		oldValue = System.getProperty(key);
		System.setProperty(key, value);
	}

	@Override
	protected void after() {
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

	@Rule
	public ProvideSystemProperty property = new ProvideSystemProperty("someKey", "someValue");

	@Test
	public void test() {
		assertThat(System.getProperty("someKey"), is("someValue"));
	}
}
~~~


## Benachrichtigung über die Testausführung

Da man mit einer Rule Code vor und nach dem Aufruf der Testmethoden ausführen kann, lässt sich damit eine Benachrichtigung über die Testausführung realisieren. Dazu stellt JUnit die abstrakte Oberklasse `TestWatcher` bereit. Diese besitzt vier leer implementierte Methoden, die man nach Bedarf überschreiben kann: `starting()`, `succeeded()`, `failed()` und `finished()`:

~~~java
public class BeepOnFailure extends TestWatcher {

	@Override
	protected void failed(Throwable e, Description description) {
		Toolkit.getDefaultToolkit().beep();
	}
}
~~~

Die Benutzung in einem Test sieht dann so aus:

~~~java
public class FailingTestThatBeeps {
	
	@Rule
	public BeepOnFailure beep = new BeepOnFailure();

	@Test
	public void test() {
		fail();
	}
}
~~~

## Bereitstellung von Informationen über den Test

Eine Rule kann außerdem Informationen über den Test innerhalb des Tests verfügbar machen. So kann man mit der `TestName` Rule etwa auf den Namen des aktuellen Tests zugreifen.

~~~java
public class NameRuleTest {
	@Rule
	public TestName test = new TestName();

	@Test
	public void test() {
		assertThat(test.getMethodName(), is("test"));
	}
}
~~~


## Überprüfungen vor und nach den Tests

Desweiteren lassen sich spezielle Überprüfungen, die einen Test beispielsweise fehlschlagen lassen können, vor oder nach jedem Test ausführen. Der `ErrorCollector` sammelt fehlgeschlagene Assertions innerhalb einer Testmethode und gibt am Ende eine Liste der Fehlschläge aus. So kann man etwa alle Elemente in einer Liste überprüfen und den Test erst am Ende fehlschlagen lassen, wenn die Überprüfung eines oder mehrerer Elemente fehlgeschlagen ist.

~~~java
public class ErrorCollectingTest {

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Test
	public void test() {
		collector.checkThat(1 + 1, is(3));
		collector.addError(new Exception("something went wrong"));
	}
}
~~~

Wenn man diesen Test ausführt, erhält man zwei Fehlernachrichten mit jeweils einem Stacktrace, der einen zu der Zeile im Programmcode führt, wo die Überprüfung fehlgeschlagen ist.

Eigene Rules, die zusätzliche Überprüfungen durchführen können, lassen sich bequem implementieren, indem man von der Klasse `Verifier` ableitet und die `verify()`-Methode implementiert.

Rules können jedoch nicht nur bewirken, dass ein Test, der ohne die Rule grün wäre, rot wird. Auch das Gegenteil ist möglich. Ein gutes Beispiel dafür stellt die `ExpectedException`-Rule dar, die einen Test nur dann als erfolgreich markiert, wenn eine bestimmte Exception aufgetreten ist.

~~~java
public class ExpectedExceptionWithRule {

	int[] threeNumbers = { 1, 2, 3 };

	@Rule public ExpectedException thrown = ExpectedException.none();

	@Test
	public void exception() {
		thrown.expect(ArrayIndexOutOfBoundsException.class);
		threeNumbers[3] = 4;
	}

	@Test
	public void exceptionWithMessage() {
		thrown.expect(ArrayIndexOutOfBoundsException.class);
		thrown.expectMessage("3");
		threeNumbers[3] = 4;
	}
}
~~~

Natürlich lässt sich das auch mit herkömmlichen Mitteln erreichen, denn genau dafür ist ja eigentlich der `expected`-Parameter der `@Test`-Annotation vorgesehen. Möchte man aber auch die Nachricht der Exception testen, war man bisher gezwungen, auf einen `try`-`catch`-Block auszuweichen:

~~~java
public class ExpectedExceptionWithoutRule {

	int[] threeNumbers = { 1, 2, 3 };

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void exception() {
		threeNumbers[3] = 4;
	}

	@Test
	public void exceptionWithMessage() {
		try {
			threeNumbers[3] = 4;
			fail("ArrayIndexOutOfBoundsException expected");
		} catch (ArrayIndexOutOfBoundsException expected) {
			assertEquals("3", expected.getMessage());
		}
	}
}
~~~

Nun lässt sich sowohl die Klasse als auch die Nachricht der erwarteten Exception über die gleiche Notation testen.

Die `@Test`-Annotation hat einen weiteren optionalen Parameter: `timeout`. Auch dafür gibt es nun eine Rule, die sich einsetzen lässt, wenn für alle Tests in einer Testklasse der gleiche Timeout gelten soll. Die beiden folgenden Tests sind äquivalent:

~~~java
public class GlobalTimeout {

	@Rule
	public Timeout timeout = new Timeout(20);

	@Test
	public void firstTest() {
		while (true) {}
	}

	@Test
	public void secondTest() {
		for (;;) {}
	}
}
~~~

~~~java
public class LocalTimeouts {

	@Test(timeout = 20)
	public void firstTest() {
		while (true) {}
	}

	@Test(timeout = 20)
	public void secondTest() {
		for (;;) {}
	}
}
~~~


## Regeln auf Klassenebene

Alle Rules, die wir bisher gesehen haben, wurden für jede Methode einzeln angewandt, genauso wie Methoden, die mit `@Before` und `@After` annotiert sind, vor bzw. nach jedem Test ausgeführt werden. Manchmal möchte man allerdings die Möglichkeit haben, Code nur einmal vor der ersten bzw. nach der letzten Testmethode in einer Klasse auszuführen. Ein häufiger Anwendungsfall sind Integrationstests, die eine Verbindung zu einem Server aufbauen und wieder schließen müssen. Das war bisher nur mit den Annotations `@BeforeClass` bzw. `@AfterClass` möglich, Rules konnte man dazu nicht verwenden. Um dieses Problem zu lösen, wurden in JUnit 4.9 ClassRules eingeführt.

Ähnlich einer normalen Rule definiert man ein Feld in der Testklasse. Analog zu `@BeforeClass`-/`@AfterClass`-Methoden muss dieses Feld `public` und `static` sein. Der Typ des Feldes muss wie bei der `@Rule`-Annotation das `TestRule`-Interface implementieren. Eine solche Rule lässt sich nicht nur in einer normalen Testklasse verwenden, sondern auch in einer Test-Suite, wie das folgende Beispiel [[3]][ReleaseNotes4.9] illustriert:

~~~java
@RunWith(Suite.class)
@SuiteClasses({A.class, B.class, C.class})
public class UsesExternalResource {
	public static Server myServer = new Server();

	@ClassRule
	public static ExternalResource connection = new ExternalResource() {
	
		@Override protected void before() throws Throwable {
			myServer.connect();
		};

		@Override protected void after() {
			myServer.disconnect();
		};
	};
}
~~~


## Mehrere Regeln kombinieren

Einen weiteren Vorteil von Rules gegenüber Hilfsmethoden in Testoberklassen stellt ihre Kombinierbarkeit dar. Es lassen sich beliebig viele Rules in einem Test verwenden:

~~~java
public class CombiningMultipleRules {

	@Rule public TestRule beep = new BeepOnFailure();
	@Rule public ExpectedException exceptions = ExpectedException.none();
	@Rule public TestName test = new TestName();

	@Test
	public void test() {
		exceptions.expect(IllegalArgumentException.class);
		throw new RuntimeException("Hello from " + test.getMethodName());
	}
}
~~~

Das funktioniert wunderbar, solange die Rules voneinander unabhängig sind. JUnit macht absichtlich keinerlei Zusicherungen was die Reihenfolge der Abarbeitung von Rules angeht [[4]][KentBeckRuleChain]. Manchmal möchte man aber dennoch eine bestimmte Reihenfolge vorgeben. Angenommen man hat zwei Rules, von denen die erste eine bestimmte Ressource zur Verfügung stellt, die von der zweiten Rule benutzt wird. Dann möchte man sehr wohl sicherstellen, dass zuerst die Ressource bereitgestellt wird, bevor sie konsumiert wird. Dafür wurde in JUnit 4.10 die `RuleChain`-Klasse eingeführt. `RuleChain` implementiert selbst das `TestRule`-Interface, kann also verwendet werden, wie eine normale Rule [[5]][ReleaseNotes4.10]:

~~~java
public class UseRuleChain {
	@Rule
	public TestRule chain = RuleChain.outerRule(new LoggingRule("outer rule"))
			.around(new LoggingRule("middle rule"))
			.around(new LoggingRule("inner rule"));
	@Test
	public void test() {}
}
~~~

Wenn man diesen Test ausführt, erhält man folgende Ausgabe:

~~~
starting outer rule
starting middle rule
starting inner rule
finished inner rule
finished middle rule
finished outer rule
~~~

Die erste Regel (`outer rule`) umschließt also die mittlere (`middle rule`) und diese wiederum die dritte und letzte (`inner rule`).


## Schreib deine eigenen Regeln!

Warum sollte man also Rules verwenden? Ein großer Pluspunkt von Rules ist ihre *Wiederverwendbarkeit*. Sie ermöglichen häufig benutzten `SetUp`/`TearDown`-Code in eine eigene `TestRule`-Klasse, die nur eine Verantwortlichkeit hat, auszulagern.

Ein weiterer Vorteil ist die *Kombinierbarkeit* von Rules. Wie wir in diesem Artikel gesehen haben, lassen sich beliebig viele Regeln in einem Test verwenden, sowohl aus Klassen- als auch auf Methodenebene. Viele Dinge, für die es in der Vergangenheit eines eigenen Test Runners bedurfte, lassen sich nun mit Rules ebenso implementieren. Da man immer nur einen Test Runner aber beliebig viele Rules verwenden kann, stehen einem dadurch deutlich mehr Möglichkeiten offen. 

Rules sind die Umsetzung von *Delegation statt Vererbung* für Unittests. Wo früher Testklassenhierarchien mit Utility-Methoden gewuchert sind, kann man jetzt auf einfache Art und Weise verschiedene Rules kombinieren. 

Die vorgestellten, konkreten Rules demonstrieren lediglich die Vielfältigkeit der Einsatzmöglichkeiten. Eigene Regeln zu schreiben ist Dank der zur Verfügung gestellten Basisklassen einfach. Erst diese *Erweiterbarkeit* macht Rules zu einem wirklichen Novum.

Die Macher von JUnit setzen jedenfalls für die Zukunft von JUnit voll auf den Einsatz und die Erweiterung von Rules. Kent Beck schreibt darüber in seinem Blog [[6]][KentBeckBlog]: „Maybe once every five years unsuspectedly powerful abstractions drop out of a program with no apparent effort.”

## Links & Literatur

1. [Blog von Jens Schauder][JensSchauderBlog]
2. [Source Code der Beispiele auf GitHub][GitHubPage]
2. [JUnit 4.9 Release Notes][ReleaseNotes4.9]
3. [Mailing List Post von Kent Beck über das Design von Rules][KentBeckRuleChain]
4. [JUnit 4.10 Release Notes][ReleaseNotes4.10]
5. [Blog von Kent Beck][KentBeckBlog]

[JensSchauderBlog]:  http://blog.schauderhaft.de/2009/10/04/junit-rules/
[GitHubPage]:        http://marcphilipp.github.com/junit-rules/
[ReleaseNotes4.9]:   http://github.com/KentBeck/junit/blob/master/doc/ReleaseNotes4.9.txt
[KentBeckRuleChain]: http://tech.groups.yahoo.com/group/junit/message/23537
[ReleaseNotes4.10]:  http://github.com/KentBeck/junit/blob/master/doc/ReleaseNotes4.10.txt
[KentBeckBlog]:      http://www.threeriversinstitute.org/blog/?p=155
