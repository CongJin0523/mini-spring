package com.cong.aop.aspectj;

import com.cong.aop.ClassFilter;
import com.cong.aop.ExpressionPointcut;
import com.cong.aop.MethodMatcher;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class AspectJExpressionPointcut implements ExpressionPointcut, ClassFilter, MethodMatcher {
  private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

  static {
    SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
  }

  private String expression;
  private PointcutExpression pointcutExpression;
  private final PointcutParser pointcutParser;

  public AspectJExpressionPointcut() {
    this.pointcutParser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
      SUPPORTED_PRIMITIVES,
      this.getClass().getClassLoader()
    );
  }

  @Override
  public void setExpression(String expression) {
    this.expression = expression;
    this.pointcutExpression = pointcutParser.parsePointcutExpression(expression);
  }

  @Override
  public String getExpression() {
    return this.expression;
  }

  @Override
  public boolean matches(Class<?> targetClass) {
    checkReadyToMatch();
    return pointcutExpression.couldMatchJoinPointsInType(targetClass);
  }

  @Override
  public boolean matches(Method method, Class<?> targetClass) {
    checkReadyToMatch();
    ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(method);
    return shadowMatch.alwaysMatches();
  }

  @Override
  public ClassFilter getClassFilter() {
    return this;
  }

  @Override
  public MethodMatcher getMethodMatcher() {
    return this;
  }

  private void checkReadyToMatch() {
    if (getExpression() == null) {
      throw new IllegalStateException("Must set property 'expression' before attempting to match");
    }
    if (pointcutExpression == null) {
      pointcutExpression = pointcutParser.parsePointcutExpression(expression);
    }
  }
}
