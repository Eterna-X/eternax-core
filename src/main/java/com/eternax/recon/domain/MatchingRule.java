package com.eternax.recon.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * One step of a definition's matching plan (DEF-10, FR-041, FR-042). The steps are tried in
 * priority order, lowest number first, and the first one that makes a complete match wins.
 *
 * @param type how records are compared: {@code EXACT_REFERENCE} (same reference, equal amount),
 *     {@code AGGREGATE} (same reference, one source split into parts that add up) or {@code
 *     COMPOSITE} (no reference: same terminal or merchant, amount and time window)
 * @param relationship {@code ONE_TO_ONE} for exact and composite; {@code ONE_TO_MANY} or {@code
 *     MANY_TO_ONE} for aggregate
 * @param amountToleranceMinor overrides the definition's tolerance for this step, 0..100 paise
 * @param windowSeconds composite time window for this step, 60..86400
 * @param keys fields a composite step groups candidates by: {@code terminalId}, {@code merchantId}
 */
public record MatchingRule(
        String name,
        Type type,
        int priority,
        boolean enabled,
        Relationship relationship,
        Long amountToleranceMinor,
        Long windowSeconds,
        List<String> keys) {

    public enum Type {
        EXACT_REFERENCE,
        AGGREGATE,
        COMPOSITE
    }

    public enum Relationship {
        ONE_TO_ONE,
        ONE_TO_MANY,
        MANY_TO_ONE
    }

    public MatchingRule {
        keys = keys == null ? List.of() : List.copyOf(keys);
    }

    /** What every definition has when it states no rules: today's fixed order. */
    public static List<MatchingRule> defaults() {
        return List.of(
                new MatchingRule(
                        "Exact reference",
                        Type.EXACT_REFERENCE,
                        1,
                        true,
                        Relationship.ONE_TO_ONE,
                        null,
                        null,
                        List.of()),
                new MatchingRule(
                        "Aggregate parts",
                        Type.AGGREGATE,
                        2,
                        true,
                        Relationship.ONE_TO_MANY,
                        null,
                        null,
                        List.of()),
                new MatchingRule(
                        "Terminal, amount and time",
                        Type.COMPOSITE,
                        3,
                        true,
                        Relationship.ONE_TO_ONE,
                        null,
                        null,
                        List.of("terminalId")));
    }

    /** Enabled steps in the order they are tried; the defaults when none are stated. */
    public static List<MatchingRule> plan(List<MatchingRule> stated) {
        List<MatchingRule> rules = stated == null || stated.isEmpty() ? defaults() : stated;
        return rules.stream()
                .filter(MatchingRule::enabled)
                .sorted(java.util.Comparator.comparingInt(MatchingRule::priority))
                .toList();
    }

    /** Every reason a rule list cannot be used; empty means it is valid. */
    public static List<String> problems(List<MatchingRule> rules) {
        List<String> problems = new ArrayList<>();
        if (rules == null || rules.isEmpty()) {
            return problems;
        }
        Set<String> names = new HashSet<>();
        Set<Integer> priorities = new HashSet<>();
        for (MatchingRule r : rules) {
            String id =
                    r.name() == null || r.name().isBlank()
                            ? "(unnamed rule)"
                            : "'" + r.name() + "'";
            if (r.name() == null || r.name().isBlank() || r.name().length() > 80) {
                problems.add("every rule needs a name of at most 80 characters");
            } else if (!names.add(r.name().trim().toLowerCase())) {
                problems.add("rule name " + id + " is used twice");
            }
            if (r.type() == null) {
                problems.add("rule " + id + " has no type");
                continue;
            }
            if (r.priority() < 1 || r.priority() > 100) {
                problems.add("rule " + id + ": priority must be between 1 and 100");
            } else if (!priorities.add(r.priority())) {
                problems.add(
                        "rule "
                                + id
                                + ": priority "
                                + r.priority()
                                + " is used twice, so the order would be undefined");
            }
            if (r.amountToleranceMinor() != null
                    && (r.amountToleranceMinor() < 0 || r.amountToleranceMinor() > 100)) {
                problems.add("rule " + id + ": amount tolerance must be within 0..100 paise");
            }
            if (r.windowSeconds() != null
                    && (r.windowSeconds() < 60 || r.windowSeconds() > 86_400)) {
                problems.add(
                        "rule " + id + ": the time window must be between 1 minute and 24 hours");
            }
            Relationship rel =
                    r.relationship() == null
                            ? (r.type() == Type.AGGREGATE
                                    ? Relationship.ONE_TO_MANY
                                    : Relationship.ONE_TO_ONE)
                            : r.relationship();
            switch (r.type()) {
                case EXACT_REFERENCE, COMPOSITE -> {
                    if (rel != Relationship.ONE_TO_ONE) {
                        problems.add(
                                "rule "
                                        + id
                                        + ": a "
                                        + r.type()
                                        + " step is one-to-one; use an AGGREGATE step for"
                                        + " one-to-many or many-to-one");
                    }
                }
                case AGGREGATE -> {
                    if (rel == Relationship.ONE_TO_ONE) {
                        problems.add(
                                "rule " + id + ": an AGGREGATE step is ONE_TO_MANY or MANY_TO_ONE");
                    }
                }
            }
            if (r.type() == Type.COMPOSITE) {
                if (r.keys().isEmpty()) {
                    problems.add(
                            "rule "
                                    + id
                                    + ": a composite step needs at least one key (terminalId or"
                                    + " merchantId)");
                }
                for (String k : r.keys()) {
                    if (!k.equals("terminalId") && !k.equals("merchantId")) {
                        problems.add(
                                "rule "
                                        + id
                                        + ": unknown composite key '"
                                        + k
                                        + "'; use terminalId or merchantId");
                    }
                }
            } else if (!r.keys().isEmpty()) {
                problems.add("rule " + id + ": keys apply to COMPOSITE steps only");
            }
            if (r.type() != Type.COMPOSITE && r.windowSeconds() != null) {
                problems.add("rule " + id + ": a time window applies to COMPOSITE steps only");
            }
        }
        if (rules.stream().noneMatch(MatchingRule::enabled)) {
            problems.add("at least one rule must be enabled");
        }
        return problems;
    }
}
