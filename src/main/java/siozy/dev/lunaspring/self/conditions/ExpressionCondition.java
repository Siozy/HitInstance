package siozy.dev.lunaspring.self.conditions;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import siozy.dev.lunaspring.API.conditions.abs.ConditionId;
import siozy.dev.lunaspring.API.conditions.abs.ConditionNullable;
import siozy.dev.lunaspring.API.conditions.abs.ConditionParams;
import siozy.dev.lunaspring.API.conditions.abs.StringCondition;
import siozy.dev.lunaspring.API.util.utilities.Utils;

import java.util.Arrays;
import java.util.regex.Matcher;

@ConditionId("EXPRESSION")
@ConditionNullable
@ConditionParams(identifiers = "expression", idClasses = String.class)
public class ExpressionCondition implements StringCondition {

    @Override
    public boolean check(OfflinePlayer player, String[] strings) {
        if (strings.length == 0) return false;
        String expression = strings[0].replaceAll("\\s+", "");

        String[] orExpressions = expression.split("\\|\\|");
        return Arrays.stream(orExpressions)
                .anyMatch(orExpr -> {
                    String[] andExpressions = orExpr.split("&&");
                    return Arrays.stream(andExpressions)
                            .allMatch(this::evaluateComparison);
                });
    }

    private boolean evaluateComparison(String expr) {
        Matcher matcher = Utils.utilsObjects.COMPARISON_PATTERN.matcher(expr);

        if (!matcher.matches()) {
            return false;
        }

        String left = matcher.group(1);
        String op = matcher.group(2);
        String right = matcher.group(3);

        return evaluateWithOperator(left, op, right);
    }

    private boolean evaluateWithOperator(String left, String op, String right) {
        try {
            double leftNum = Double.parseDouble(left);
            double rightNum = Double.parseDouble(right);

            return switch (op) {
                case "<" -> leftNum < rightNum;
                case "<=" -> leftNum <= rightNum;
                case ">" -> leftNum > rightNum;
                case ">=" -> leftNum >= rightNum;
                case "==" -> Math.abs(leftNum - rightNum) < 1e-10;
                case "!=" -> Math.abs(leftNum - rightNum) >= 1e-10;
                case "===" -> leftNum == rightNum;
                case "!==" -> leftNum != rightNum;
                default -> false;
            };
        } catch (NumberFormatException e) {
            return switch (op) {
                case "==" -> left.equalsIgnoreCase(right);
                case "===" -> left.equals(right);
                case "!=" -> !left.equalsIgnoreCase(right);
                case "!==" -> !left.equals(right);
                default -> false;
            };
        }
    }

    @Override
    public Object[] generateObjects(ConfigurationSection section) {
        return new Object[]{section.getString("expression")};
    }
}