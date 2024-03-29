package org.nem.core.crypto;

import org.hamcrest.core.*;
import org.junit.*;
import org.nem.core.crypto.ed25519.arithmetic.*;
import org.nem.core.serialization.Deserializer;
import org.nem.core.test.Utils;

public class PublicKeyTest {

	private final byte[] TEST_BYTES = new byte[] { 0x22, (byte)0xAB, 0x71 };
	private final byte[] MODIFIED_TEST_BYTES = new byte[] { 0x22, (byte)0xAB, 0x72 };
	private static final Ed25519GroupElement A = getA(true);

	//region constructors / factories

	@Test
	public void canCreateFromBytes() {
		// Arrange:
		final PublicKey key = new PublicKey(this.TEST_BYTES);

		// Assert:
		Assert.assertThat(key.getRaw(), IsEqual.equalTo(this.TEST_BYTES));
	}

	@Test
	public void canCreateFromHexString() {
		// Arrange:
		final PublicKey key = PublicKey.fromHexString("227F");

		// Assert:
		Assert.assertThat(key.getRaw(), IsEqual.equalTo(new byte[] { 0x22, 0x7F }));
	}

	@Test(expected = CryptoException.class)
	public void cannotCreateAroundMalformedHexString() {
		// Act:
		PublicKey.fromHexString("22G75");
	}

	//endregion

	//region serializer

	@Test
	public void keyCanBeRoundTripped() {
		// Act:
		final PublicKey key = createRoundTrippedKey(new PublicKey(this.TEST_BYTES));

		// Assert:
		Assert.assertThat(key, IsEqual.equalTo(new PublicKey(this.TEST_BYTES)));
	}

	public static PublicKey createRoundTrippedKey(final PublicKey originalKey) {
		// Act:
		final Deserializer deserializer = Utils.roundtripSerializableEntity(originalKey, null);
		return new PublicKey(deserializer);
	}

	//endregion

	//region equals / hashCode

	@Test
	public void equalsOnlyReturnsTrueForEquivalentObjects() {
		// Arrange:
		final PublicKey key = new PublicKey(this.TEST_BYTES);

		// Assert:
		Assert.assertThat(new PublicKey(this.TEST_BYTES), IsEqual.equalTo(key));
		Assert.assertThat(new PublicKey(this.MODIFIED_TEST_BYTES), IsNot.not(IsEqual.equalTo(key)));
		Assert.assertThat(null, IsNot.not(IsEqual.equalTo(key)));
		Assert.assertThat(this.TEST_BYTES, IsNot.not(IsEqual.equalTo((Object)key)));
	}

	@Test
	public void hashCodesAreEqualForEquivalentObjects() {
		// Arrange:
		final PublicKey key = new PublicKey(this.TEST_BYTES);
		final int hashCode = key.hashCode();

		// Assert:
		Assert.assertThat(new PublicKey(this.TEST_BYTES).hashCode(), IsEqual.equalTo(hashCode));
		Assert.assertThat(new PublicKey(this.MODIFIED_TEST_BYTES).hashCode(), IsNot.not(IsEqual.equalTo(hashCode)));
	}

	//endregion

	//region toString

	@Test
	public void toStringReturnsHexRepresentation() {
		// Assert:
		Assert.assertThat(new PublicKey(this.TEST_BYTES).toString(), IsEqual.equalTo("22ab71"));
	}

	//endregion

	//region group element A

	@Test(expected = RuntimeException.class)
	public void ctorWithProjectiveCoordinatesThrowsIfAIsNull() {
		// Assert:
		new PublicKey(this.TEST_BYTES, null);
	}

	@Test(expected = RuntimeException.class)
	public void ctorWithProjectiveCoordinatesThrowsIfAIsNotPrecomputed() {
		// Assert:
		new PublicKey(this.TEST_BYTES, getA(false));
	}

	@Test
	public void canCreatePublicKeyWithProjectiveCoordinatesIfAllParamsAreCorrect() {
		// Assert:
		new PublicKey(this.TEST_BYTES, A);
	}

	//endregion

	private static Ed25519GroupElement getA(final boolean precompute) {
		final Ed25519GroupElement A = Ed25519GroupElement.p3(
				Ed25519Field.ZERO,
				Ed25519Field.ONE,
				Ed25519Field.ONE,
				Ed25519Field.ZERO);
		if (precompute) {
			A.precomputeForDoubleScalarMultiplication();
		}

		return A;
	}
}
