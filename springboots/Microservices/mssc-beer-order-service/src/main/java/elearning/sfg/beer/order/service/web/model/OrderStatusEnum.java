package elearning.sfg.beer.order.service.web.model;

public enum OrderStatusEnum {
    NEW, VALIDATED, VALIDATION_EXCEPTION, ALLOCATED, ALLOCATION_EXCEPTION,
    PENDING_INVENTORY, PICKED_UP, DELIVERED, DELIVERY_EXCEPTION
}
