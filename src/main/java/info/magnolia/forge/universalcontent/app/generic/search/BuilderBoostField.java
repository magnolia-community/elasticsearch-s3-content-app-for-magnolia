/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.search;

/**
 * The Class BuilderBoostField.
 */
public class BuilderBoostField {

	/** The boost field. */
	BoostField boostField;

	/**
	 * Creates the.
	 *
	 * @return the builder boost field
	 */
	public static BuilderBoostField create() {
		BuilderBoostField builder = new BuilderBoostField();
		builder.boostField = new BoostField();
		return builder;
	}

	/**
	 * Field name.
	 *
	 * @param fieldName the field name
	 * @return the builder boost field
	 */
	public BuilderBoostField fieldName(String fieldName) {
		this.boostField.setFieldName(fieldName);
		return this;
	}

	/**
	 * Boost.
	 *
	 * @param boost the boost
	 * @return the builder boost field
	 */
	public BuilderBoostField boost(Integer boost) {
		this.boostField.setBoost(boost);
		return this;
	}

	/**
	 * Must contain word.
	 *
	 * @param mustContainWord the must contain word
	 * @return the builder boost field
	 */
	public BuilderBoostField mustContainWord(String mustContainWord) {
		this.boostField.setMustContainWord(mustContainWord);
		return this;
	}

	/**
	 * Should contain word.
	 *
	 * @param shouldContainWord the should contain word
	 * @return the builder boost field
	 */
	public BuilderBoostField shouldContainWord(String shouldContainWord) {
		this.boostField.setShouldContainWord(shouldContainWord);
		return this;
	}

	/**
	 * Not contain word.
	 *
	 * @param notContainWord the not contain word
	 * @return the builder boost field
	 */
	public BuilderBoostField notContainWord(String notContainWord) {
		this.boostField.setNotContainWord(notContainWord);
		return this;
	}

	/**
	 * Gets the.
	 *
	 * @return the boost field
	 */
	public BoostField get() {
		return this.boostField;
	}
}
