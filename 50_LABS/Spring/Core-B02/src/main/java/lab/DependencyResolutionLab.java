package lab;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DependencyResolutionLab {

    private DependencyResolutionLab() {
    }

    public static void main(String[] args) {
        runSuccessfulResolution();
        runAmbiguousResolution();
    }

    private static void runSuccessfulResolution() {
        System.out.println("=== SUCCESSFUL RESOLUTION ===");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(GoodConfiguration.class)) {

            CheckoutService service = context.getBean(CheckoutService.class);
            service.printResolutionReport();
        }
    }

    private static void runAmbiguousResolution() {
        System.out.println();
        System.out.println("=== EXPECTED AMBIGUITY ===");

        try (AnnotationConfigApplicationContext ignored =
                     new AnnotationConfigApplicationContext(AmbiguousConfiguration.class)) {
            throw new AssertionError("Context should not start with two unresolved candidates");
        } catch (BeansException error) {
            Throwable root = rootCause(error);
            System.out.println("Context failed as expected");
            System.out.println("Root cause: " + root.getClass().getSimpleName());
            System.out.println("Message:    " + root.getMessage());
        }
    }

    private static Throwable rootCause(Throwable error) {
        Throwable current = error;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    @Configuration
    static class GoodConfiguration {

        @Bean
        @Primary
        @Qualifier("online")
        PaymentGateway stripeGateway() {
            return new NamedGateway("stripe");
        }

        @Bean
        @Qualifier("online")
        PaymentGateway paypalGateway() {
            return new NamedGateway("paypal");
        }

        @Bean
        @Qualifier("offline")
        PaymentGateway cashGateway() {
            return new NamedGateway("cash");
        }

        @Bean
        CheckoutService checkoutService(
                PaymentGateway defaultGateway,
                @Qualifier("online") List<PaymentGateway> onlineGateways,
                Map<String, PaymentGateway> allGateways,
                Optional<AuditSink> auditSink,
                ObjectProvider<DiscountPolicy> discountPolicies
        ) {
            return new CheckoutService(
                    defaultGateway,
                    onlineGateways,
                    allGateways,
                    auditSink,
                    discountPolicies
            );
        }
    }

    @Configuration
    static class AmbiguousConfiguration {

        @Bean
        PaymentGateway firstGateway() {
            return new NamedGateway("first");
        }

        @Bean
        PaymentGateway secondGateway() {
            return new NamedGateway("second");
        }

        @Bean
        AmbiguousConsumer ambiguousConsumer(PaymentGateway gateway) {
            return new AmbiguousConsumer(gateway);
        }
    }

    interface PaymentGateway {
        String id();
    }

    static final class NamedGateway implements PaymentGateway {
        private final String id;

        NamedGateway(String id) {
            this.id = id;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    interface AuditSink {
        void write(String event);
    }

    interface DiscountPolicy {
        String name();
    }

    static final class NoDiscountPolicy implements DiscountPolicy {
        @Override
        public String name() {
            return "no-discount";
        }
    }

    static final class CheckoutService {
        private final PaymentGateway defaultGateway;
        private final List<PaymentGateway> onlineGateways;
        private final Map<String, PaymentGateway> allGateways;
        private final Optional<AuditSink> auditSink;
        private final ObjectProvider<DiscountPolicy> discountPolicies;

        CheckoutService(
                PaymentGateway defaultGateway,
                List<PaymentGateway> onlineGateways,
                Map<String, PaymentGateway> allGateways,
                Optional<AuditSink> auditSink,
                ObjectProvider<DiscountPolicy> discountPolicies
        ) {
            this.defaultGateway = defaultGateway;
            this.onlineGateways = onlineGateways;
            this.allGateways = allGateways;
            this.auditSink = auditSink;
            this.discountPolicies = discountPolicies;
        }

        void printResolutionReport() {
            System.out.println("Default gateway (@Primary): " + defaultGateway.id());
            System.out.println("Online gateways (@Qualifier + List): " + onlineGateways);
            System.out.println("All gateway bean names (Map): " + allGateways.keySet());
            System.out.println("Audit available (Optional): " + auditSink.isPresent());
            System.out.println(
                    "Discount policy (ObjectProvider fallback): " +
                            discountPolicies
                                    .getIfAvailable(NoDiscountPolicy::new)
                                    .name()
            );
        }
    }

    static final class AmbiguousConsumer {
        private final PaymentGateway gateway;

        AmbiguousConsumer(PaymentGateway gateway) {
            this.gateway = gateway;
        }

        PaymentGateway gateway() {
            return gateway;
        }
    }
}
