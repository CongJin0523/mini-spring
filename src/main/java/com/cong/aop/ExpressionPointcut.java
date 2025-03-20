package com.cong.aop;

public interface ExpressionPointcut extends Pointcut {
  String getExpression();
  void setExpression(String expression);
}
