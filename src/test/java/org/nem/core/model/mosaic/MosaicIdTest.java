package org.nem.core.model.mosaic;

import net.minidev.json.JSONObject;
import org.hamcrest.core.*;
import org.junit.*;
import org.nem.core.model.namespace.NamespaceId;
import org.nem.core.serialization.*;
import org.nem.core.test.*;
import wiremock.org.apache.commons.lang.StringUtils;

import java.util.*;

public class MosaicIdTest {

	//region ctor

	@Test
	public void canCreateMosaicId() {
		// Act:
		final MosaicId mosaicId = createMosaicId("alice.vouchers", "foo");

		// Assert:
		Assert.assertThat(mosaicId.getNamespaceId(), IsEqual.equalTo(new NamespaceId("alice.vouchers")));
		Assert.assertThat(mosaicId.getName(), IsEqual.equalTo("foo"));
	}

	@Test
	public void cannotCreateMosaicIdWithNullNamespace() {
		// Assert:
		ExceptionAssert.assertThrows(
				v -> new MosaicId(null, "foo"),
				IllegalArgumentException.class);
	}

	@Test
	public void cannotCreateMosaicIdWithEmptyName() {
		// Assert:
		for (final String name : Arrays.asList(null, "")) {
			ExceptionAssert.assertThrows(
					v -> createMosaicId("alice.vouchers", name),
					IllegalArgumentException.class);
		}
	}

	@Test
	public void cannotCreateMosaicIdWithInvalidName() {
		// Assert:
		createInvalidIdList().stream()
				.forEach(s -> ExceptionAssert.assertThrows(
						v -> createMosaicId("alice.vouchers", s),
						IllegalArgumentException.class));
	}

	private static Collection<String> createInvalidIdList() {
		return Arrays.asList("-id", "_id", " id", StringUtils.repeat("too long", 5));
	}

	//endregion

	//region serialization

	@Test
	public void canSerializeMosaicId() {
		// Arrange:
		final MosaicId mosaicId = createMosaicId("alice.vouchers", "foo");

		// Act:
		final JSONObject object = JsonSerializer.serializeToJson(mosaicId);

		// Assert:
		Assert.assertThat(object.size(), IsEqual.equalTo(2));
		Assert.assertThat(object.get("namespaceId"), IsEqual.equalTo("alice.vouchers"));
		Assert.assertThat(object.get("name"), IsEqual.equalTo("foo"));
	}

	@Test
	public void canDeserializeMosaicId() {
		// Act:
		final MosaicId mosaicId = deserialize("alice.vouchers", "foo");

		// Assert:
		Assert.assertThat(mosaicId.getNamespaceId(), IsEqual.equalTo(new NamespaceId("alice.vouchers")));
		Assert.assertThat(mosaicId.getName(), IsEqual.equalTo("foo"));
	}

	@Test
	public void canRoundTripMosaicId() {
		// Arrange:
		final MosaicId originalMosaicId = createMosaicId("alice.vouchers", "foo");
		final Deserializer deserializer = Utils.roundtripSerializableEntity(originalMosaicId, null);

		// Act:
		final MosaicId mosaicId = new MosaicId(deserializer);

		// Assert:
		Assert.assertThat(mosaicId.getNamespaceId(), IsEqual.equalTo(new NamespaceId("alice.vouchers")));
		Assert.assertThat(mosaicId.getName(), IsEqual.equalTo("foo"));
	}

	@Test
	public void cannotDeserializeWithMissingNamespace() {
		// Assert:
		ExceptionAssert.assertThrows(
				v -> deserialize(null, "foo"),
				MissingRequiredPropertyException.class);
	}

	@Test
	public void cannotDeserializeWithMissingName() {
		// Assert:
		for (final String name : Arrays.asList(null, "")) {
			ExceptionAssert.assertThrows(
					v -> deserialize("alice.vouchers", name),
					MissingRequiredPropertyException.class);
		}
	}

	@Test
	public void cannotDeserializeMosaicIdWithInvalidName() {
		// Assert:
		createInvalidIdList().stream()
				.forEach(s -> ExceptionAssert.assertThrows(
						v -> deserialize("alice.vouchers", s),
						IllegalArgumentException.class));
	}

	private static MosaicId deserialize(final String namespaceId, final String name) {
		final JSONObject jsonObject = new JSONObject();
		if (null != namespaceId) {
			jsonObject.put("namespaceId", namespaceId);
		}

		if (null != name) {
			jsonObject.put("name", name);
		}

		return new MosaicId(new JsonDeserializer(jsonObject, null));
	}

	//endregion

	//region toString

	@Test
	public void toStringReturnsId() {
		// Arrange:
		final MosaicId mosaicId = createMosaicId("BoB.SilveR", "BaR");

		// Assert:
		Assert.assertThat(mosaicId.toString(), IsEqual.equalTo("bob.silver * BaR"));
	}

	//endregion

	//region equals / hashCode

	private static Map<String, MosaicId> createMosaicIdsForEqualityTests() {
		return new HashMap<String, MosaicId>() {
			{
				this.put("default", createMosaicId("foo.bar.baz", "zip"));
				this.put("diff-namespace-case", createMosaicId("FoO.bAr.BaZ", "zip"));
				this.put("diff-name-case", createMosaicId("foo.bar.baz", "ZiP"));
				this.put("diff-namespace", createMosaicId("xyz.bar.baz", "zip"));
				this.put("diff-name", createMosaicId("foo.bar.baz", "rar"));
			}
		};
	}

	@Test
	public void equalsOnlyReturnsTrueForEquivalentObjects() {
		// Arrange:
		final MosaicId mosaicId = createMosaicId("foo.bar.baz", "zip");

		// Assert:
		for (final Map.Entry<String, MosaicId> entry : createMosaicIdsForEqualityTests().entrySet()) {
			Assert.assertThat(
					entry.getValue(),
					isDiffExpected(entry.getKey()) ? IsNot.not(IsEqual.equalTo(mosaicId)) : IsEqual.equalTo(mosaicId));
		}

		Assert.assertThat(new Object(), IsNot.not(IsEqual.equalTo(mosaicId)));
		Assert.assertThat(null, IsNot.not(IsEqual.equalTo(mosaicId)));
	}

	@Test
	public void hashCodesAreEqualForEquivalentObjects() {
		// Arrange:
		final int hashCode = createMosaicId("foo.bar.baz", "zip").hashCode();

		// Assert:
		for (final Map.Entry<String, MosaicId> entry : createMosaicIdsForEqualityTests().entrySet()) {
			Assert.assertThat(
					entry.getValue().hashCode(),
					isDiffExpected(entry.getKey()) ? IsNot.not(IsEqual.equalTo(hashCode)) : IsEqual.equalTo(hashCode));
		}
	}

	private static boolean isDiffExpected(final String propertyName) {
		return !propertyName.endsWith("-case") && !propertyName.equals("default");
	}

	//endregion

	private static MosaicId createMosaicId(final String namespaceId, final String name) {
		return new MosaicId(new NamespaceId(namespaceId), name);
	}
}
