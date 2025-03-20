package com.cong.aop;

import com.cong.aop.aspectj.AspectJExpressionPointcut;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

  public class AspectJExpressionPointcutTest {

    @Test
    public void testExecutionExpression() throws Exception {
      AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
      pointcut.setExpression("execution(* com.cong.aop.AspectJExpressionPointcutTest.*(..))");

      assertTrue(pointcut.matches(AspectJExpressionPointcutTest.class));
      assertTrue(pointcut.matches(
        AspectJExpressionPointcutTest.class.getDeclaredMethod("testExecutionExpression"),
        AspectJExpressionPointcutTest.class
      ));
    }

    @Test
    public void testMethodMatchWithArgs() throws Exception {
      AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
      pointcut.setExpression("execution(* com.cong.aop.AspectJExpressionPointcutTest.testMethodMatch*(..))");

      assertTrue(pointcut.matches(
        AspectJExpressionPointcutTest.class.getDeclaredMethod("testMethodMatchWithArgs"),
        AspectJExpressionPointcutTest.class
      ));

      assertFalse(pointcut.matches(
        AspectJExpressionPointcutTest.class.getDeclaredMethod("testExecutionExpression"),
        AspectJExpressionPointcutTest.class
      ));
    }
  }
