package org.nem.core.model;

import org.junit.*;
import org.nem.core.test.IsEquivalent;

import java.util.*;

public class TransactionTypesTest {

	@Test
	public void getActiveTypesReturnsAllExpectedTypes() {
		// Arrange:
		final Collection<Integer> expectedTypes = Arrays.asList(
				TransactionTypes.TRANSFER,
				TransactionTypes.IMPORTANCE_TRANSFER,
				TransactionTypes.MULTISIG_AGGREGATE_MODIFICATION,
				// TODO 20150717 J-* re-enable after next release
				/*TransactionTypes.PROVISION_NAMESPACE,
				TransactionTypes.MOSAIC_CREATION,*/
				TransactionTypes.MULTISIG,
				TransactionTypes.MULTISIG_SIGNATURE
				/*TransactionTypes.SMART_TILE_SUPPLY_CHANGE*/);

		// Act:
		final Collection<Integer> types = TransactionTypes.getActiveTypes();

		// Assert:
		Assert.assertThat(types, IsEquivalent.equivalentTo(expectedTypes));
	}

	@Test
	public void getBlockEmbeddableTypesReturnsAllExpectedTypes() {
		// Arrange:
		final Collection<Integer> expectedTypes = Arrays.asList(
				TransactionTypes.TRANSFER,
				TransactionTypes.IMPORTANCE_TRANSFER,
				TransactionTypes.MULTISIG_AGGREGATE_MODIFICATION,
				// TODO 20150717 J-* re-enable after next release
				/*TransactionTypes.PROVISION_NAMESPACE,
				TransactionTypes.MOSAIC_CREATION,*/
				TransactionTypes.MULTISIG/*,
				TransactionTypes.SMART_TILE_SUPPLY_CHANGE*/);

		// Act:
		final Collection<Integer> types = TransactionTypes.getBlockEmbeddableTypes();

		// Assert:
		Assert.assertThat(types, IsEquivalent.equivalentTo(expectedTypes));
	}

	@Test
	public void getMultisigEmbeddableTypesReturnsAllExpectedTypes() {
		// Arrange:
		final Collection<Integer> expectedTypes = Arrays.asList(
				TransactionTypes.TRANSFER,
				TransactionTypes.IMPORTANCE_TRANSFER,
				TransactionTypes.MULTISIG_AGGREGATE_MODIFICATION
				// TODO 20150717 J-* re-enable after next release
				/*,
				TransactionTypes.PROVISION_NAMESPACE,
				TransactionTypes.MOSAIC_CREATION,
				TransactionTypes.SMART_TILE_SUPPLY_CHANGE*/);

		// Act:
		final Collection<Integer> types = TransactionTypes.getMultisigEmbeddableTypes();

		// Assert:
		Assert.assertThat(types, IsEquivalent.equivalentTo(expectedTypes));
	}
}