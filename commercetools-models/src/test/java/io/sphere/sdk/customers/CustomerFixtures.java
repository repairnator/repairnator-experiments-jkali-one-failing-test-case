package io.sphere.sdk.customers;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.SetCustomerEmail;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.customergroups.CustomerGroup;
import io.sphere.sdk.customers.commands.CustomerCreateCommand;
import io.sphere.sdk.customers.commands.CustomerDeleteCommand;
import io.sphere.sdk.customers.commands.CustomerUpdateCommand;
import io.sphere.sdk.customers.commands.updateactions.AddAddress;
import io.sphere.sdk.customers.commands.updateactions.SetCustomerGroup;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.AddressBuilder;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static io.sphere.sdk.customergroups.CustomerGroupFixtures.*;
import static io.sphere.sdk.test.SphereTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomerFixtures {
    public static final CustomerName CUSTOMER_NAME = CustomerName.ofFirstAndLastName("John", "Smith");
    public static final String PASSWORD = "secret";

    public static void withCustomerWithOneAddress(final BlockingSphereClient client, final Consumer<Customer> customerConsumer) {
        final Consumer<Customer> customerUpdater = customer -> {
            final Address address = AddressBuilder.of(DE).city("address city").build();
            final Customer customerWithAddress = client.executeBlocking(CustomerUpdateCommand.of(customer, AddAddress.of(address)));
            assertThat(customerWithAddress.getAddresses()).hasSize(1);
            customerConsumer.accept(customerWithAddress);
        };
        withCustomer(client, newCustomerDraft(), customerUpdater);
    }

    public static void withCustomerInGroup(final BlockingSphereClient client, final BiConsumer<Customer, CustomerGroup> consumer) {
        withB2cCustomerGroup(client, group -> {
            withCustomer(client, customer -> {
                final Customer customerInGroup = client.executeBlocking(CustomerUpdateCommand.of(customer, SetCustomerGroup.of(group)));
                consumer.accept(customerInGroup, group);
            });
        });
    }

    public static void withUpdateableCustomer(final BlockingSphereClient client, final UnaryOperator<Customer> operator) {
        final CustomerSignInResult signInResult = client.executeBlocking(CustomerCreateCommand.of(newCustomerDraft()));
        final Customer customerToDelete = operator.apply(signInResult.getCustomer());
        client.executeBlocking(CustomerDeleteCommand.of(customerToDelete));
    }

    public static void withCustomer(final BlockingSphereClient client, final Consumer<Customer> customerConsumer) {
        withCustomer(client, newCustomerDraft(), customerConsumer);
    }

    public static void withCustomer(final BlockingSphereClient client,
                                    final CustomerDraft draft, final Consumer<Customer> customerConsumer) {
        final CustomerSignInResult signInResult = client.executeBlocking(CustomerCreateCommand.of(draft));
        customerConsumer.accept(signInResult.getCustomer());
        //currently the backend does not allow customer deletion
    }

    public static void withCustomerAndCart(final BlockingSphereClient client, final BiConsumer<Customer, Cart> consumer) {
        withCustomer(client, customer -> {
            final CartDraft cartDraft = CartDraft.of(EUR).withCustomerId(customer.getId());
            final Cart cart = client.executeBlocking(CartCreateCommand.of(cartDraft));
            consumer.accept(customer, client.executeBlocking(CartUpdateCommand.of(cart, SetCustomerEmail.of(customer.getEmail()))));
        });
    }

    public static CustomerDraftDsl newCustomerDraft() {
        return CustomerDraftDsl.of(CUSTOMER_NAME, randomEmail(CustomerFixtures.class), PASSWORD);
    }
}
