package elearning.sfg.beer.inventory.services;

import elearning.sfg.beer.brewery.dtos.BeerOrderDto;
import elearning.sfg.beer.brewery.dtos.BeerOrderLineDto;
import elearning.sfg.beer.inventory.domain.BeerInventory;
import elearning.sfg.beer.inventory.repositories.BeerInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class AllocationServiceImpl implements AllocationService {

    private final BeerInventoryRepository beerInventoryRepository;

    @Override
    public Boolean allocateOrder(BeerOrderDto beerOrderDto) {
        log.debug("Allocating OrderId: " + beerOrderDto.getId());

        AtomicInteger totalOrdered = new AtomicInteger();
        AtomicInteger totalAllocated = new AtomicInteger();

        beerOrderDto.getBeerOrderLines().forEach(beerOrderLine -> {
            if ((((beerOrderLine.getOrderQuantity() != null ? beerOrderLine.getOrderQuantity() : 0)
                    - (beerOrderLine.getQuantityAllocated() != null ? beerOrderLine.getQuantityAllocated() : 0)) > 0)) {
                allocateBeerOrderLine(beerOrderLine);
            }
            totalOrdered.set(totalOrdered.get() + beerOrderLine.getOrderQuantity());
            totalAllocated.set(totalAllocated.get() +
                    (beerOrderLine.getQuantityAllocated() != null ? beerOrderLine.getQuantityAllocated() : 0));
        });

        log.debug("Total Ordered: " + totalOrdered.get() + " Total Allocated: " + totalAllocated.get());

        return totalOrdered.get() == totalAllocated.get();
    }

    private void allocateBeerOrderLine(BeerOrderLineDto beerOrderLine) {
        List<BeerInventory> beerInventoryList = beerInventoryRepository.findAllByUpc(beerOrderLine.getUpc());

        /*
         * Allocated quantity represents the quantity of the product reserved to be used in the pending sale
         * and/or work orders. These orders are not yet completed yet but are authorised, and for successful completion,
         * items are reserved to avoid double selling. Allocated quantity is used to determine Available quantity
         * for Quoting, Ordering, and Picking.
         * */

        /*
         * On Hand quantity is always calculated based on actual transactions,
         * operations and/or inventory cards. On Hand quantity is the quantity you expect to see in your warehouse.
         * This includes products from sale orders which have yet to be fulfilled.
         * This does not include products from purchase orders that are yet to be received.
         * */

        beerInventoryList.forEach(beerInventory -> {
            int inventory = (beerInventory.getQuantityOnHand() == null) ? 0 : beerInventory.getQuantityOnHand();
            int orderQty = (beerOrderLine.getOrderQuantity() == null) ? 0 : beerOrderLine.getOrderQuantity();
            int allocatedQty = (beerOrderLine.getQuantityAllocated() == null) ? 0 : beerOrderLine.getQuantityAllocated();
            int qtyToAllocate = orderQty - allocatedQty;

            if (inventory >= qtyToAllocate) { // full allocation
                inventory = inventory - qtyToAllocate;
                beerOrderLine.setQuantityAllocated(orderQty);
                beerInventory.setQuantityOnHand(inventory);

                beerInventoryRepository.save(beerInventory);

            }
        /*
        * Available quantity is always a difference between On Hand quantity and Allocated quantity.
        * - Available quantity = On Hand – Allocated
            There are two ways that Available quantity is calculated:
                1- General Available quantity per product, regardless of the batch info
                * (the actual result depends on the Available quantity is limited to either all or selected
                * locations parameter on the General Settings page).
                2- Available quantity specific to the defined location, batch, and expiry date.
        * */
            else if (inventory > 0) { //partial allocation
                beerOrderLine.setQuantityAllocated(allocatedQty + inventory);
                beerInventory.setQuantityOnHand(0);

                beerInventoryRepository.delete(beerInventory);
            }
        });

    }
}
