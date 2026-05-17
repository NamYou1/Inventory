# Purchase Service Fix - Summary

## Problems Fixed

### 1. **Grand Total Not Being Set**
   - **Issue**: The `grandTotal` was calculated but never set on the `purchases` entity
   - **Fix**: Added `purchases.setGrandTotal(grandTotal)` to store the calculated value

### 2. **Total Not Being Set**
   - **Issue**: The `total` field was not being calculated or set
   - **Fix**: Added logic to accumulate and set the total from all item subtotals

### 3. **Total Discount Not Captured**
   - **Issue**: Item-level and order-level discounts were not being tracked
   - **Fix**: 
     - Added `totalDiscount` field to the Purchases entity (was commented out)
     - Accumulated item-level discounts from each item
     - Added order-level discount to the total
     - Set the total discount on the purchases entity

### 4. **Missing Field in Entity**
   - **Issue**: The Purchases entity was missing the `totalDiscount` field
   - **Fix**: Added `private BigDecimal totalDiscount;` to line 29 of Purchases.java

## Changes Made

### File: PurchaseServiceImp.java
- Restructured `createPurchase()` method to:
  1. Calculate totals properly (total and item-level discounts)
  2. Handle order-level discount
  3. Calculate grand total as: `total - totalDiscount`
  4. Set all calculated values on the purchase entity
  5. Ensure purchase items and stock are updated correctly

### File: Purchases.java
- Added: `private BigDecimal totalDiscount;`

## Calculation Logic

```
For each item:
  - subtotal = quantity × costPrice
  - total += subtotal
  - totalDiscount += item.totalDiscount (if present)

After loop:
  - totalDiscount += order.orderDiscount (if present)
  - grandTotal = total - totalDiscount

Save purchase with:
  - total = total
  - totalDiscount = totalDiscount
  - grandTotal = grandTotal
  - tblPurchaseItem = purchaseItems (cascade saves items)
```

## Response Example

```json
{
  "payload": {
    "id": 8,
    "reference": "string",
    "date": null,
    "supplierId": 1,
    "supplierName": "string",
    "storeId": 1,
    "storeName": "string",
    "sellerId": 1,
    "sellerName": "string",
    "total": 200.00,           // sum of all subtotals
    "totalDiscount": 20.00,    // item discounts + order discount
    "grandTotal": 180.00,      // total - totalDiscount
    "purchasesStatus": "ORDERED",
    "items": [
      {
        "productId": 1,
        "quantity": 10,
        "costPrice": 20,
        "subtotal": 200
      }
    ]
  }
}
```

## Testing

Run: `./gradlew build`

All tests pass successfully! ✅

