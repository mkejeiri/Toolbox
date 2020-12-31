package com.elearning.msscstatemachine.domain;

public enum PaymentEvent {
    PRE_AUTH_REQUESTED, PRE_AUTH_APPROVED, PRE_AUTH_DECLINED,
    AUTH_REQUESTED, AUTH_APPROVED, AUTH_DECLINED
}