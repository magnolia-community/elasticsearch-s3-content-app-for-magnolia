
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.whitelabel.app.generic.search.BoostField;
import com.whitelabel.app.generic.search.BuilderBoostField;

public class BuilderBoostFieldTest {
	BoostField boostField;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void create() {
		BuilderBoostField builder = BuilderBoostField.create();
		assertNotNull(builder);
	}

	@Test
	void fieldName() {
		String fieldName = "test";
		BuilderBoostField builder = BuilderBoostField.create().fieldName(fieldName);

		assertEquals(builder.get().getFieldName(), fieldName);
	}

	@Test
	void boost() {
		Integer boost = 1;
		BuilderBoostField builder = BuilderBoostField.create().boost(boost);
		assertEquals(builder.get().getBoost(), boost);
	}

	@Test
	void mustContainWord() {
		String mustContainWord = "mustContainWord";
		BuilderBoostField builder = BuilderBoostField.create().mustContainWord(mustContainWord);
		assertEquals(builder.get().getMustContainWord(), mustContainWord);
	}

	@Test
	void shouldContainWord() {
		String shouldContainWord = "shouldContainWord";
		BuilderBoostField builder = BuilderBoostField.create().shouldContainWord(shouldContainWord);
		assertEquals(builder.get().getShouldContainWord(), shouldContainWord);
	}

	@Test
	void notContainWord() {
		String notContainWord = "notContainWord";
		BuilderBoostField builder = BuilderBoostField.create().notContainWord(notContainWord);
		assertEquals(builder.get().getNotContainWord(), notContainWord);
	}

}
