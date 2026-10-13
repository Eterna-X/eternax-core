package com.eternax.recon.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.eternax.recon.domain.MatchingRule.Relationship;
import com.eternax.recon.domain.MatchingRule.Type;
import java.util.List;
import org.junit.jupiter.api.Test;

class MatchingRuleTest {

    private static MatchingRule rule(String name, Type type, int priority, boolean enabled) {
        return new MatchingRule(
                name,
                type,
                priority,
                enabled,
                type == Type.AGGREGATE ? Relationship.ONE_TO_MANY : Relationship.ONE_TO_ONE,
                null,
                null,
                type == Type.COMPOSITE ? List.of("terminalId") : List.of());
    }

    @Test
    void noRules_meansTheDefaultOrder() {
        assertThat(MatchingRule.plan(null))
                .extracting(MatchingRule::type)
                .containsExactly(Type.EXACT_REFERENCE, Type.AGGREGATE, Type.COMPOSITE);
        assertThat(MatchingRule.problems(null)).isEmpty();
    }

    @Test
    void thePlanIsOrderedByPriority_andSkipsDisabledRules() {
        var plan =
                MatchingRule.plan(
                        List.of(
                                rule("c", Type.COMPOSITE, 1, true),
                                rule("e", Type.EXACT_REFERENCE, 5, true),
                                rule("a", Type.AGGREGATE, 3, false)));
        assertThat(plan).extracting(MatchingRule::name).containsExactly("c", "e");
    }

    @Test
    void invalidRulesAreExplainedAllAtOnce() {
        var bad =
                List.of(
                        rule("dup", Type.EXACT_REFERENCE, 1, true),
                        rule("dup", Type.AGGREGATE, 1, true),
                        new MatchingRule(
                                "x",
                                Type.EXACT_REFERENCE,
                                2,
                                true,
                                Relationship.ONE_TO_MANY,
                                500L,
                                5L,
                                List.of("terminalId")),
                        new MatchingRule(
                                "y",
                                Type.COMPOSITE,
                                3,
                                true,
                                Relationship.ONE_TO_ONE,
                                null,
                                100L,
                                List.of("pan")));
        assertThat(MatchingRule.problems(bad))
                .hasSizeGreaterThanOrEqualTo(6)
                .anyMatch(p -> p.contains("used twice"))
                .anyMatch(p -> p.contains("0..100"))
                .anyMatch(p -> p.contains("keys apply to COMPOSITE"))
                .anyMatch(p -> p.contains("unknown composite key"));
        assertThat(MatchingRule.problems(List.of(rule("only", Type.EXACT_REFERENCE, 1, false))))
                .anyMatch(p -> p.contains("at least one rule must be enabled"));
    }

    @Test
    void aValidPlanHasNoProblems() {
        assertThat(MatchingRule.problems(MatchingRule.defaults())).isEmpty();
    }
}
